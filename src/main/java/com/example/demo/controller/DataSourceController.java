package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.RecordsService;
import com.example.demo.service.UserService;
import com.example.demo.util.MyUtil;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class DataSourceController {
    /*用户持久层服务*/
    @Resource
    private UserMapper userDataService;

    /*消费记录服务*/
    @Resource
    private RecordMapper recordService;

    /*工具类*/
    @Resource
    private MyUtil myUtil;

    @Resource
    private UserService userService;

    @Resource
    private HttpService httpService;

    @Resource
    private RecordsService recordsService;


    /**
     * @param id 管理员的 id
     * @return 返回从数据库中查询到的对象。
     */
    @GetMapping("/test/getUserById/{id}")
    User getUserById(@PathVariable("id") Integer id) {
        return userDataService.getUserById(id);
    }

    @CrossOrigin
    @RequestMapping("/test/time")
    String getTime() {
        return myUtil.getCureTime();
    }

    @RequestMapping("/test/getRecordsByUserName")
    List<Record> getRecordsByUserName(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return recordService.getRecordsByUserName(user.getUserName());
    }

    @RequestMapping("/test/getNetInfo")
    JSONObject getNetInfo() {
        return myUtil.getNetInfo();
    }

    @CrossOrigin
    @PostMapping("/sendEmail")
    String sendEmail(String userName, String Subject) {
        //todo 这里利用 userName 从缓存中取出 email 地址。
        String email = "";
        //code 是发送邮寄的返回的6位随机验证码,限时 60 s,存入缓存中。
//        return myUtil.sendMail(email, userName);
        return "";
    }

    /**
     * 提交的充值请求
     */
    @CrossOrigin
    @PostMapping("/userRechargeSubmit")
    String userRechargeSubmit(HttpServletRequest request, @RequestBody Map<String, String> map) {
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

    @CrossOrigin
    @PostMapping("/getRecords")
    List<Record> getRecords(HttpServletRequest request) {
        return userService.getRecords(request);
    }

    //分页获取，这里注意，前端传来的 index 最小为 1。
    @CrossOrigin
    @RequestMapping("/getRecordsForPage/{index}/{size}")
    ResultResponse getRecords(HttpServletRequest request, @PathVariable("index") Integer index, @PathVariable("size") Integer size) {
        return userService.getRecords(request, index, size);
    }

    //分页获取，这里注意，前端传来的 index 最小为 1。
    @CrossOrigin
    @RequestMapping("/getRecordsForPage/{index}/{size}/{type}")
    ResultResponse getRecords(HttpServletRequest request,
                              @PathVariable("index") Integer index,
                              @PathVariable("size") Integer size,
                              @PathVariable("type") Integer type) {
        return userService.getRecords(request, index, size, type);
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
    @RequestMapping("/register")
    String register(HttpServletRequest request, @RequestBody Map<String, String> map) {
        User user = User.builder()
                .userName(map.get("userName"))
                .email(map.get("email"))
                .passWord(map.get("passWord"))
                .build();
        return userService.userRegisterHandler(request, user, map.get("code")) ? "successed" : "failed";
    }

    @CrossOrigin
    @RequestMapping("/getUserAuthorityList")
    Set<Integer> getUserAuthorityList(HttpServletRequest request) {
        return userService.getUserAuthorityListHandler(request);
    }

    /**
     * 根据响应的权限查询相关的数据
     */
    @CrossOrigin
    @RequestMapping("/getRecords/{authority}")
    List<Record> getRecordsByType(HttpServletRequest request, @PathVariable("authority") Integer authority) {
        return httpService.getRecordsByType(request, authority);
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

    @CrossOrigin
    @RequestMapping("/logOut/userLogOut")
    ResultResponse logOut(HttpServletRequest request) {
        return userService.logOutHandler(request);
    }

    @CrossOrigin
    @RequestMapping("/buttons/cancelRecord/{id}")
    ResultResponse cancelRecord(@PathVariable("id") Integer id) {
        return recordsService.cancelRecordHandler(id);
    }

    @CrossOrigin
    @RequestMapping("/buttons/repeatedSubmitRecord/{id}")
    ResultResponse repeatedSubmitRecord(@PathVariable("id") Integer id) {
        return recordsService.repeatedSubmitHandler(id);
    }

    @CrossOrigin
    @RequestMapping("/buttons/deleteRecord/{id}")
    ResultResponse deleteRecord(@PathVariable("id") Integer id) {
        return recordsService.deleteSubmitHandler(id);
    }

    @CrossOrigin
    @RequestMapping("/buttons/agreeRecord/{id}")
    ResultResponse agreeRecord(HttpServletRequest request, @PathVariable("id") Integer id) {
        return recordsService.agreeRecordHandler(request, id);
    }

    //驳回
    @CrossOrigin
    @RequestMapping("/buttons/turnDownRecord/{id}")
    ResultResponse turnDownRecord(HttpServletRequest request, @PathVariable("id") Integer id) {
        return recordsService.turnDownRecord(request, id);
    }
}
