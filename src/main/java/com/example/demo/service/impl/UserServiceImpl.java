package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.domain.MappingTitleAndButtons;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.UserService;
import com.example.demo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    @Value("${user.newUserAuthority}")
    private String newUserAuthority;

    @Autowired
    private Map<Integer, MappingTitleAndButtons> map;

    @Autowired
    private SimpleDateFormat simpleDateFormat;

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
    public ResultResponse logOutHandler(HttpServletRequest request) {
        ResultResponse response = null;
        try {
            String ipAddress = httpService.getIpAddress(request);
            JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
            User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
            JSONObject curNetInfo = myUtil.getNetInfo();
            BigDecimal curData = curNetInfo.getBigDecimal("getData");
            //之前的流量
            BigDecimal preNetData = json.getBigDecimal("getData");
            //花费的流量
            BigDecimal costData = curData.subtract(preNetData);

            Date signIn = json.getDate("signIn");
            Date signOut = new Date();

            Record record = Record.builder()
                    .userName(user.getUserName())
                    .signIn(signIn)
                    .signOut(signOut)
                    .costData(costData)
                    .type(RecordTypeEnum.userExpenses.getVal())
                    //这里的金额需要除以 1000
                    .costMoney(myUtil.calcSpend(signIn, signOut))
                    .build();

            recordService.insertRecord(record);

            //移除该用户的信息
            cache.hdel(EncryptionKey.netData, ipAddress);
            cache.hdel(EncryptionKey.userLoginInfo, ipAddress);
            response = ResultResponse.createSimpleSuccess("http://localhost:8080/", null);
        } catch (Exception e) {
            response = ResultResponse.createError(-1, "退出失败！");
        }
        return response;
    }

    @Override
    public boolean loginByEmailHandler(HttpServletRequest request, String email, String code, String userName) {
        User user = userDataService.getUserByUserNameAndEmail(userName, email);
        //数据库中没有查询到这个用户
        if (user == null) {
            return false;
        }

        //校验邮箱验证码
        String ipAddress = httpService.getIpAddress(request);
        String emailCode = (String) cache.hget(EncryptionKey.loginEmail, ipAddress);

        //验证码不正确
        if (emailCode == null || !emailCode.equals(code)) {
            return false;
        }

        //注册用户登录
        return loginHandler(user, request);
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
    public ResultResponse getRecords(HttpServletRequest request, Integer page, Integer size) {
        User user = getUserInfoHandler(request);

        List<Record> records = recordService.getRecordsByUserNameForPages(user.getUserName(), (page - 1) * size, size, RecordTypeEnum.userExpenses.getVal());

        if (records == null) {
            return ResultResponse.createError(-1, "查询数据库异常！");
        }

        ResultResponse response = new ResultResponse();
        response.setCode(200);

        ResultResponse.Success success = new ResultResponse.Success();
        success.setData(records);

        response.setSuccess(success);
        return response;
    }

    @Override
    public ResultResponse getRecords(HttpServletRequest request, Integer page, Integer size, Integer type) {
        User user = getUserInfoHandler(request);

        List<Record> records = null;

        switch (type) {
            case 0, 1, 2, 3, 4 -> records = recordService.getRecordsByUserNameForPages(user.getUserName(), (page - 1) * size, size, type);
            case 5 -> records = recordService.getRecordsByUserNameForPages(null, (page - 1) * size, size, 4);
            case 6 -> records = recordService.getRecordsByUserNameForPages(null, (page - 1) * size, size, 1);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        if (records == null) {
            return ResultResponse.createError(-1, "查询数据库异常！");
        }

        return ResultResponse.createSuccessForTypeAndRecords(null, type, records, map, myUtil);
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

        //为该用户添加权限
        user.setAuthority(newUserAuthority);

        //注册用户成功过，插入数据。
        int primaryKey = userDataService.insertUser(user);
        user.setId(primaryKey);

        //注册用户登录
        return loginHandler(user, request);
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

    @Override
    public ResultResponse loginUserLoginHandler(HttpServletRequest request, User user) {
        ResultResponse res = new ResultResponse();

        String userName = user.getUserName(), passWord = user.getPassWord();

        //todo 中级步骤需要校验是否登录成功,待编写。
        user = userDataService.getUserByUserName(userName);

        if (user == null) {
            return ResultResponse.createError(-1, "用户名不存在!");
        } else if (!user.getPassWord().equals(passWord)) {
            return ResultResponse.createError(-1, "密码错误!");
        }
        //将用户的权限转化为 set 集合类型
        user.setAuthorityToSet(JSON.parseObject(user.getAuthority(), new TypeReference<>() {
        }));

        //将ip 地址作为key，将流量数据及其对象信息存入缓存,
        String ipAddress = httpService.getIpAddress(request);
        JSONObject netInfo = myUtil.getNetInfo();
        netInfo.put("signIn", new Date());
        boolean save = cache.hset(EncryptionKey.netData, ipAddress, netInfo) && cache.hset(EncryptionKey.userLoginInfo, ipAddress, user);
        if (!save) {
            ResultResponse.createError(-1, "内部错误，数据存入缓存异常!");
        } else {
            //登录成功
            res.setCode(200);
            ResultResponse.Success success = new ResultResponse.Success();

            //设置成功之后的跳转页面
            success.setUrl("http://localhost:8080/form");

            res.setSuccess(success);
        }
        return res;
    }

    @Override
    public ResultResponse userRegister(HttpServletRequest request, User user, String code) {
        //用户名已经存在，这里后续可以优化
        if (userDataService.getUserByUserName(user.getUserName()) != null) {
            return ResultResponse.createError(-1, "用户名已经存在！");
        }

        //校验邮箱验证码
        String ipAddress = httpService.getIpAddress(request);
        String emailCode = (String) cache.hget(EncryptionKey.registerEmail, ipAddress);
        //验证码不正确
        if (emailCode == null || !emailCode.equals(code)) {
            return ResultResponse.createError(-1, "邮箱验证码已过期！");
        }

        //为该用户添加权限
        user.setAuthority(newUserAuthority);

        //注册用户成功过，插入数据。
        int primaryKey = userDataService.insertUser(user);
        user.setId(primaryKey);

        //返回登录处理结果。
        return loginUserLoginHandler(request, user);
    }
}
