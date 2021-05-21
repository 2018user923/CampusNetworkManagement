package com.example.demo.service;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface HttpService {
    /**
     * 根据 request 获取 ip 地址
     *
     * @param request
     * @return
     */
    String getIpAddress(HttpServletRequest request);

    /**
     * 普通用户表单提交
     */
    ModelAndView ordinaryUserForm(HttpServletRequest request);

    Map<String,Object> getNetworkTrafficHandler(HttpServletRequest request);
}
