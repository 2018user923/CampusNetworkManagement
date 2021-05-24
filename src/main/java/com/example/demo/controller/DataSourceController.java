package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.UserService;
import com.example.demo.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

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
    @PostMapping("/getNetworkTraffic")
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
    List<Record> getRecords(HttpServletRequest request, @PathVariable("index") Integer index, @PathVariable("size") Integer size) {
        return userService.getRecords(request, index, size);
    }

    //邮箱发送验证码
    @CrossOrigin
    @RequestMapping("/register/sendEmail")
    String sendEmail(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return httpService.sendEmailHandler(request, map.get("email"));
    }

}
