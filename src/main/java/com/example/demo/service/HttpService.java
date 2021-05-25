package com.example.demo.service;

import com.example.demo.domain.Record;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    Map<String, Object> getNetworkTrafficHandler(HttpServletRequest request);

    String registerSendEmail(HttpServletRequest request, String email);

    String loginSendEmail(HttpServletRequest request, String email);

    List<Record> getRecordsByType(HttpServletRequest request, Integer authority);
}
