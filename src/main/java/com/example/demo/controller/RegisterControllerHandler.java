package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.HttpService;
import com.example.demo.service.UserService;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户注册控制器处理
 *
 * @author <247702560@qq.com>
 * @since 2021/6/1 14:34
 */
@Slf4j
@RestController
public class RegisterControllerHandler {
    @Resource
    private UserService userService;
    @Resource
    private HttpService httpService;

    /**
     * 描述: 用户注册请求的逻辑处理，不能够创建管理员用户！
     *
     * @return 返回注册成功的信息，如果注册成功，那么会再次调用登录处理程序。
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:05
     * @param: request
     * @param: map 包含了传教用户的基本信息，及其邮箱验证码。
     */
    @CrossOrigin
    @RequestMapping("/register/userRegister")
    ResultResponse userRegister(HttpServletRequest request, @RequestBody Map<String, String> map) {
        User user = User.builder()
                .userName(map.get("userName"))
                .passWord(map.get("passWord"))
                .email(map.get("email"))
                .build();
        String code = map.get("code");
        return userService.userRegisterHandler(request, user, code);
    }

    /**
     * @return java.lang.String
     * @Description 注册账户邮箱发送验证码
     * @Author <247702560@qq.com>
     * @Date 2021/5/31 23:00
     * @Param [request, map]
     */
    @CrossOrigin
    @RequestMapping("/register/sendEmail")
    String registerSendEmail(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return httpService.registerSendEmail(request, map.get("email"));
    }
}
