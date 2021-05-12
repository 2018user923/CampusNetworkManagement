package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.domain.User;
import com.example.demo.domain.UserLoginInfo;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.crypto.EncryptedPrivateKeyInfo;
import java.util.Date;

@RestController
@Slf4j
public class DataSourceController {
    @Resource
    private UserMapper userMapper;

    @Value("${user.images.path}")
    private String userImagesPath;

    @Resource
    private MyUtil myUtil;

    @Resource
    private RedisUtil cache;


    /**
     * @param id 管理员的 id
     * @return 返回从数据库中查询到的对象。
     */
    @GetMapping("/getUserById/{id}")
    User getUserById(@PathVariable("id") Integer id) {
        return userMapper.getUserById(id);
    }

    /**
     * @param user 管理员信息。
     */
//    @PostMapping("/insertUser")
//    User insertUser(User user) {
//        userMapper.insertUser(user);
//        return user;
//    }
    @PostMapping("/updateUser")
    User updateUser(User user) {
//        userMapper.updateUser(user);
        return user;
    }

    @RequestMapping("/time")
    String getTime() {
        return myUtil.getCureTime();
    }

    @PostMapping("/login")
    String login(User user) {
        return "";
    }

    @PostMapping("/register")
    User register(User user) {
        int primaryKey = userMapper.insertUser(user);
        user.setId(primaryKey);
        UserLoginInfo info = UserLoginInfo.builder().loginInTime(new Date()).user(user).build();
        cache.hset(EncryptionKey.userLoginInfo, String.valueOf(primaryKey), JSON.toJSON(info));
        return user;
    }
}
