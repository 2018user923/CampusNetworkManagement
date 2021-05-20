package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
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
    String userRechargeSubmit(HttpServletRequest request, Integer rechargeAmount) {
        return userService.userRechargeAppHandler(request, rechargeAmount);
    }

    @CrossOrigin
    @PostMapping("/userInfoUpdateSubmit")
    String userInfoUpdateSubmit(HttpServletRequest request, User user, @RequestParam(value = "file", required = false) MultipartFile file) {
        return "none";
//        return userService.userInfoUpdateHandler(request, user);
    }
}
