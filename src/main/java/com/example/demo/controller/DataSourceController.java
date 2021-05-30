package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.HttpService;
import com.example.demo.service.RecordsService;
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

@Slf4j
@RestController
public class DataSourceController {
    @Resource
    private UserService userService;

    @Resource
    private HttpService httpService;

    @Resource
    private RecordsService recordsService;

    /**
     * 提交的充值请求
     */
    @CrossOrigin
    @PostMapping("/userRechargeSubmit")
    ResultResponse userRechargeSubmit(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return userService.userRechargeAppHandler(request, Integer.valueOf(map.get("rechargeAmount")));
    }

    @CrossOrigin
    @PostMapping("/userInfoUpdateSubmit")
    String userInfoUpdateSubmit(HttpServletRequest request, User user, @RequestParam(value = "file", required = false) MultipartFile file) {
        return userService.userInfoUpdateHandler(request, user);
    }

    /**
     * 获取用户的基本信息
     */
    @CrossOrigin
    @PostMapping("/getUserInfo")
    User getUserInfo(HttpServletRequest request) {
        return userService.getUserInfoHandler(request);
    }

    /**
     * 获取当前 ip 使用的流量数据
     */
    @CrossOrigin
    @RequestMapping("/getNetworkTraffic")
    Map<String, Object> getNetworkTraffic(HttpServletRequest request) {
        return httpService.getNetworkTrafficHandler(request);
    }

    /**
     * 注册账户邮箱发送验证码
     */
    @CrossOrigin
    @RequestMapping("/register/sendEmail")
    String registerSendEmail(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return httpService.registerSendEmail(request, map.get("email"));
    }

    /**
     * 登录账户邮箱发送验证码
     */
    @CrossOrigin
    @RequestMapping("/login/sendEmail")
    String loginSendEmail(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return httpService.loginSendEmail(request, map.get("email"));
    }

    @CrossOrigin
    @RequestMapping("/getUserAuthorityList")
    Set<Integer> getUserAuthorityList(HttpServletRequest request) {
        return userService.getUserAuthorityListHandler(request);
    }

    @CrossOrigin
    @PostMapping("/getRecords")
    ResultResponse getRecords(HttpServletRequest request, @RequestBody DBInputInfo dbInputInfo) {
        return recordsService.getRecordsHandler(request, dbInputInfo);
    }

    /*
     * 获取当前用户今日的所以申请信息
     * */
    @CrossOrigin
    @PostMapping("/getRecordsForLogIn")
    ResultResponse getRecordsForLogIn(HttpServletRequest request) {
        return recordsService.getRecordsForLogInHandler(request);
    }

    /**
     * 使用邮箱登录
     */
    @CrossOrigin
    @RequestMapping("/login/emailLogin")
    String loginEmailLogin(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return userService.loginByEmailHandler(request, map.get("email"), map.get("code"), map.get("userName")) ? "successed" : "failed";
    }

    /**
     * 正常登录
     */
    @CrossOrigin
    @RequestMapping("/login/userLogin")
    ResultResponse loginUserLogin(HttpServletRequest request, @RequestBody User user) {
        return userService.loginUserLoginHandler(request, user);
    }

    @CrossOrigin
    @RequestMapping("/register/userRegister")
    ResultResponse userRegister(HttpServletRequest request, @RequestBody Map<String, String> map) {
        User user = User.builder()
                .userName(map.get("userName"))
                .passWord(map.get("passWord"))
                .email(map.get("email"))
                .build();
        String code = map.get("code");
        return userService.userRegister(request, user, code);
    }

    /**
     * 取消按钮点击逻辑
     */
    @CrossOrigin
    @RequestMapping("/buttons/cancelRecord/{id}")
    ResultResponse cancelRecord(@PathVariable("id") Integer id) {
        return recordsService.cancelRecordHandler(id);
    }

    /**
     * 再次提交按钮点击逻辑
     */
    @CrossOrigin
    @RequestMapping("/buttons/repeatedSubmitRecord/{id}")
    ResultResponse repeatedSubmitRecord(@PathVariable("id") Integer id) {
        return recordsService.repeatedSubmitHandler(id);
    }

    /**
     * 删除按钮点击逻辑
     */
    @CrossOrigin
    @RequestMapping("/buttons/deleteRecord/{id}")
    ResultResponse deleteRecord(@PathVariable("id") Integer id) {
        return recordsService.deleteSubmitHandler(id);
    }

    /**
     * 同意按钮点击逻辑
     */
    @CrossOrigin
    @RequestMapping("/buttons/agreeRecord/{id}")
    ResultResponse agreeRecord(HttpServletRequest request, @PathVariable("id") Integer id) {
        return recordsService.agreeRecordHandler(request, id);
    }

    /**
     * 驳回按钮点击逻辑
     */
    @CrossOrigin
    @RequestMapping("/buttons/turnDownRecord/{id}")
    ResultResponse turnDownRecord(HttpServletRequest request, @PathVariable("id") Integer id) {
        return recordsService.turnDownRecordHandler(request, id);
    }
}
