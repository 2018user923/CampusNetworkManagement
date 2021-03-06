package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.service.HttpService;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import com.example.demo.util.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HttpServiceImpl implements HttpService {
    /*redis缓存*/
    @Resource
    private RedisUtil cache;

    @Resource
    private HttpService httpService;

    /*工具类*/
    @Autowired
    private MyUtil myUtil;

    @Autowired
    private RecordMapper recordDataService;

    /**
     * 根据 HttpServletRequest 获取 ipAddress
     */
    @Override
    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public Map<String, Object> getNetworkTrafficHandler(HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        String ipAddress = httpService.getIpAddress(request);
        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
//        之前的流量
        BigDecimal preNetData = json.getBigDecimal("getData");
//        当前的流量
        JSONObject curNetInfo = myUtil.getNetInfo();
        BigDecimal curData = curNetInfo.getBigDecimal("getData");

//        花费的流量, bytes,转换为 mb 需要除以 2^20
        BigDecimal costData = curData.subtract(preNetData);

        map.put("costData", costData);
        map.put("curTime", new Date());
        map.put("signIn", json.get("signIn"));
        return map;
    }

    @Override
    public String registerSendEmail(HttpServletRequest request, String email) {
        String code = myUtil.sendMail(email, "注册账户验证码");
        String ipAddress = httpService.getIpAddress(request);
        cache.hset(EncryptionKey.registerEmail, ipAddress, code, 60);
        return "successed";
    }

    @Override
    public ResultResponse loginSendEmail(HttpServletRequest request, String email) {
        String code = myUtil.sendMail(email, "登录账户验证码");
        String ipAddress = httpService.getIpAddress(request);
        cache.hset(EncryptionKey.loginEmail, ipAddress, code, 60);
        return ResultResponse.createSimpleSuccess(null, null);
    }

    @Override
    public List<Record> getRecordsByType(HttpServletRequest request, Integer authority) {
        String ipAddress = httpService.getIpAddress(request);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
        return recordDataService.getRecordsByUserNameAndType(user.getUserName(), authority);
    }
}
