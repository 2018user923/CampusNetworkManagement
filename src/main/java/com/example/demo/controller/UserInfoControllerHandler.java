package com.example.demo.controller;

import com.example.demo.domain.Chat;
import com.example.demo.domain.User;
import com.example.demo.service.HttpService;
import com.example.demo.service.UserService;
import com.example.demo.util.DBInputInfo;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * 获取用户信息处理程序
 *
 * @author <247702560@qq.com>
 * @since 2021/6/1 14:37
 */
@Slf4j
@RestController
public class UserInfoControllerHandler {
    @Resource
    private UserService userService;

    @Resource
    private HttpService httpService;

    /**
     * 描述: 返回当前登录用户的权限
     *
     * @return 返回的是当前用户的权限，结果是 set
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:01
     * @param: request
     */
    @CrossOrigin
    @RequestMapping("/getUserAuthorityList")
    Set<Integer> getUserAuthorityList(HttpServletRequest request) {
        return userService.getUserAuthorityListHandler(request);
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Description 获取当前 ip 使用的流量数据
     * @Author <247702560@qq.com>
     * @Date 2021/5/31 23:00
     * @Param [request]
     */
    @CrossOrigin
    @RequestMapping("/getNetworkTraffic")
    Map<String, Object> getNetworkTraffic(HttpServletRequest request) {
        return httpService.getNetworkTrafficHandler(request);
    }

    /**
     * @Description 获取用户的基本信息
     * @Author <247702560@qq.com>
     * @Date 2021/5/31 23:00
     * @Param [request]
     */
    @CrossOrigin
    @PostMapping("/getUserInfo")
    User getUserInfo(HttpServletRequest request) {
        return userService.getUserInfoHandler(request);
    }

    /**
     * 描述: 用户更新信息处理
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:09
     * @param: request
     * @param: user
     * @param: file
     */
    @CrossOrigin
    @PostMapping("/userInfoUpdateSubmit")
    ResultResponse userInfoUpdateSubmit(HttpServletRequest request, User user, @RequestParam(value = "file", required = false) MultipartFile file) {
        return userService.userInfoUpdateHandler(request, user, file);
    }

    /**
     * 描述:  用户提交的充值申请处理
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:08
     * @param: request
     * @param: map
     */
    @CrossOrigin
    @PostMapping("/userRechargeSubmit")
    ResultResponse userRechargeSubmit(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return userService.userRechargeAppHandler(request, Integer.valueOf(map.get("rechargeAmount")));
    }

    @CrossOrigin
    @RequestMapping("/userSendMessage")
    ResultResponse userSendMessage(HttpServletRequest request, @RequestBody Chat chat) {
        return userService.userSendMessageHandler(request, chat);
    }

    @CrossOrigin
    @RequestMapping("/getMessageByTime")
    ResultResponse getMessage(HttpServletRequest request, @RequestBody DBInputInfo dbInputInfo) {
        return userService.getMessageHandler(request, dbInputInfo);
    }
}
