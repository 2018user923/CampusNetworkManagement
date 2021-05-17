package com.example.demo.service.impl;

import com.example.demo.service.HttpService;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class HttpServiceImpl implements HttpService {
    /*redis缓存*/
    @Resource
    private RedisUtil cache;

    @Resource
    private HttpService httpService;

    /*工具类*/
//    @Resource
//    private MyUtil myUtil;

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

    /**
     * 普通用户表单提交页面的 modeAndView
     */
    @Override
    public ModelAndView ordinaryUserForm(HttpServletRequest request) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("/form");
//        String ipAddress = httpService.getIpAddress(request);
//        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
//        JSONObject curNetInfo = myUtil.getNetInfo();
//        BigDecimal curData = curNetInfo.getBigDecimal("getData");
//        //之前的流量
//        BigDecimal preNetData = json.getBigDecimal("getData");
//        //花费的流量
//        BigDecimal costData = curData.subtract(preNetData).divide(new BigDecimal(1048576));
//        modelAndView.addObject("costData", costData.toBigInteger());

        return null;
    }
}
