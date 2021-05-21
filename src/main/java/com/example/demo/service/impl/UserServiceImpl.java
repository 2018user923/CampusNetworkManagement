package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.UserService;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

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


    /**
     * 用户登录服务
     */
    @Override
    public boolean login(User user, HttpServletRequest request) {
        String userName = user.getUserName(), passWord = user.getPassWord();
        //todo 中级步骤需要校验是否登录成功,待编写。
        user = userDataService.getUserByUserName(userName);
        //todo 用户名或密码不正确,后续需要区分是用户名不存在还是密码不正确
        if (user == null || !user.getPassWord().equals(passWord)) {
            return false;
        }
        //将ip 地址作为主键，将流量数据存入缓存,
        String ipAddress = httpService.getIpAddress(request);
        log.info("UserServiceImpl#login ipAddress : " + ipAddress);
        JSONObject netInfo = myUtil.getNetInfo();
        netInfo.put("signIn", new Date());
        return cache.hset(EncryptionKey.netData, ipAddress, netInfo) && cache.hset(EncryptionKey.userLoginInfo, ipAddress, user);
    }

    /**
     * 用户退出服务
     */
    @Override
    public boolean logOut(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);

        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);

        JSONObject curNetInfo = myUtil.getNetInfo();
        BigDecimal curData = curNetInfo.getBigDecimal("getData");
        //之前的流量
        BigDecimal preNetData = json.getBigDecimal("getData");
        //花费的流量
        BigDecimal costData = curData.subtract(preNetData);

        Record record = new Record();
        record.setUserName(user.getUserName());
        record.setSignIn(json.getDate("signIn"));
        record.setSignOut(new Date());
        record.setCostData(costData);

        recordService.insertRecord(record);

        //移除该用户的信息
        cache.hdel(EncryptionKey.netData, ipAddress);
        cache.hdel(EncryptionKey.userLoginInfo, ipAddress);
        return true;
    }

    /**
     * @param request 用户请求的 form
     * @return
     */
    @Override
    public ModelAndView form(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/form");
        String ipAddress = httpService.getIpAddress(request);
        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
        JSONObject curNetInfo = myUtil.getNetInfo();
        BigDecimal curData = curNetInfo.getBigDecimal("getData");
        //之前的流量
        BigDecimal preNetData = json.getBigDecimal("getData");
        //花费的流量
        BigDecimal costData = curData.subtract(preNetData).divide(new BigDecimal(1048576));
        modelAndView.addObject("costData", costData.toBigInteger());
        modelAndView.addObject("user", cache.hget(EncryptionKey.userLoginInfo, ipAddress));
        return modelAndView;
    }

    /**
     * 用户充值处理程序
     *
     * @return
     */
    @Override
    public String userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount) {
//        String ipAddress = httpService.getIpAddress(request);
//        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
//        userDataService.updateUserByUserName(user.getUserName());
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
}
