package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.UserService;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RecordTypeEnum;
import com.example.demo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    /*用户持久层服务*/
    @Resource
    private UserMapper userDataService;

    /*消费记录服务*/
    @Resource
    private RecordMapper recordService;

    /*redis 缓存*/
    @Resource
    private RedisUtil cache;

    /*工具类*/
    @Resource
    private MyUtil myUtil;

    @Autowired
    private HttpService httpService;

    @Autowired
    private UserService userService;

    @Value("${user.newUserAuthority}")
    private String newUserAuthority;


    /**
     * 用户登录服务
     */
    @Override
    public boolean loginHandler(User user, HttpServletRequest request) {
        String userName = user.getUserName(), passWord = user.getPassWord();
        //todo 中级步骤需要校验是否登录成功,待编写。
        user = userDataService.getUserByUserName(userName);
        //todo 用户名或密码不正确,后续需要区分是用户名不存在还是密码不正确
        if (user == null || !user.getPassWord().equals(passWord)) {
            return false;
        }
        user.setAuthorityToSet(JSON.parseObject(user.getAuthority(), new TypeReference<>() {
        }));
        //将ip 地址作为主键，将流量数据存入缓存,
        String ipAddress = httpService.getIpAddress(request);
        JSONObject netInfo = myUtil.getNetInfo();
        netInfo.put("signIn", new Date());
        return cache.hset(EncryptionKey.netData, ipAddress, netInfo) && cache.hset(EncryptionKey.userLoginInfo, ipAddress, user);
    }

    /**
     * 用户退出服务
     */
    @Override
    public boolean logOutHandler(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);

        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);

        JSONObject curNetInfo = myUtil.getNetInfo();
        BigDecimal curData = curNetInfo.getBigDecimal("getData");
        //之前的流量
        BigDecimal preNetData = json.getBigDecimal("getData");
        //花费的流量
        BigDecimal costData = curData.subtract(preNetData);

        Record record = Record.builder()
                .userName(user.getUserName())
                .signIn(json.getDate("signIn"))
                .signOut(new Date())
                .costData(costData)
                .type(RecordTypeEnum.userExpenses.getVal())
                .build();

        recordService.insertRecord(record);

        //移除该用户的信息
        cache.hdel(EncryptionKey.netData, ipAddress);
        cache.hdel(EncryptionKey.userLoginInfo, ipAddress);
        return true;
    }

    /**
     * @param request 用户请求的 form
     */
    @Override
    public ModelAndView form(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/form");
        return modelAndView;
    }

    /**
     * 用户充值处理程序
     */
    @Override
    public String userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount) {
        String ipAddress = httpService.getIpAddress(request);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
        //
        Record record = Record.builder()
                .userName(user.getUserName())
                .createTime(new Date())
                .type(RecordTypeEnum.userRechargeSubmit.getVal())
                .rechargeAmount(new BigDecimal(rechargeAmount))
                .build();
        recordService.insertRecord(record);
        return "successed";
    }

    @Override
    public String userInfoUpdateHandler(HttpServletRequest request, User user) {
        int primaryKey = userDataService.updateUser(user);
        String ipAddress = httpService.getIpAddress(request);
        user = userDataService.getUserById(primaryKey);
        log.info("UserServiceImpl#userInfoUpdateHandler ipAddress : " + ipAddress);
        cache.hset(EncryptionKey.userLoginInfo, ipAddress, user);
        request.getSession().setAttribute("user", user);
        return "successed";
    }

    @Override
    public User getUserInfoHandler(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);
        return (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
    }

    @Override
    public List<Record> getRecords(HttpServletRequest request) {
        User user = getUserInfoHandler(request);
        return recordService.getRecordsByUserName(user.getUserName());
    }

    @Override
    public List<Record> getRecords(HttpServletRequest request, Integer page, Integer size) {
        User user = getUserInfoHandler(request);
        return recordService.getRecordsByUserNameForPages(user.getUserName(), (page - 1) * size, size, RecordTypeEnum.userExpenses.getVal());
    }

    /**
     * 处理用户注册
     */
    @Override
    public boolean userRegisterHandler(HttpServletRequest request, User user, String code) {
        //用户名已存在，无法注册。
        if (userDataService.getUserByUserName(user.getUserName()) != null) {
            return false;
        }
        //校验邮箱验证码
        String ipAddress = httpService.getIpAddress(request);
        String emailCode = (String) cache.hget(EncryptionKey.registerEmail, ipAddress);
        //验证码不正确
        if (emailCode == null || !emailCode.equals(code)) {
            return false;
        }

        //未该用户添加权限
        user.setAuthority(newUserAuthority);

        //注册用户成功过，插入数据。
        int primaryKey = userDataService.insertUser(user);
        user.setId(primaryKey);

        //注册用户登录
        return userService.loginHandler(user, request);
    }

    /**
     * 得到用户权限
     */
    @Override
    public Set<Integer> getUserAuthorityListHandler(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
        return user.getAuthorityToSet();
    }
}
