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
 * 处理用户登录控制器
 *
 * @author <247702560@qq.com>
 * @since 2021/6/1 14:32
 */
@Slf4j
@RestController
public class LoginControllerHandler {
    @Resource
    private UserService userService;
    @Resource
    private HttpService httpService;

    /**
     * 描述: 登录账户邮箱发送验证码
     *
     * @return todo 返回 200 即可,这里后续需要修改！当前这里是返回 succeed
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 13:58
     * @param: request
     * @param: map 前端传入的数据，邮箱
     */
    @CrossOrigin
    @RequestMapping("/login/sendEmail")
    ResultResponse loginSendEmail(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return httpService.loginSendEmail(request, map.get("email"));
    }

    /**
     * 描述:  处理用户使用邮箱登录。
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:07
     * @param: request
     * @param: map 包含用户登录的用户名、邮箱、邮箱验证码等信息.
     */
    @CrossOrigin
    @RequestMapping("/login/emailLogin")
    ResultResponse loginEmailLogin(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return userService.loginByEmailHandler(request, map.get("email"), map.get("code"), map.get("userName"));
    }

    /**
     * 描述: 用户登录请求处理
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:06
     * @param: request
     * @param: user 用户登录信息
     */
    @CrossOrigin
    @RequestMapping("/login/userLogin")
    ResultResponse userLogin(HttpServletRequest request, @RequestBody User user) {
        return userService.loginUserHandler(request, user);
    }
}
