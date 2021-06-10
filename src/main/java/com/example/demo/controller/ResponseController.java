package com.example.demo.controller;

import com.example.demo.service.UserService;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class ResponseController {
    @Autowired
    private UserService userService;

    /**
     * @return java.lang.String
     * @Description 退出系统，删除 ip 对应的信息,并且将流量数据插入其中
     * @Author <247702560@qq.com>
     * @Date 2021/5/31 22:56
     * @Param [request]
     */
    @RequestMapping("/logOut")
    public String logOut(HttpServletRequest request) {
        ResultResponse response = userService.logOutHandler(request);
        return "redirect:" + response.getSuccess().getUrl();
    }
}
