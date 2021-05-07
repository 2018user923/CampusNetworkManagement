package com.example.demo.controller;

import com.example.demo.mapper.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class DataSourceController {
    @Resource
    private UserMapper userMapper;

    @Value("${user.images.path}")
    private String userImagesPath;

    @Resource
    private MyUtil myUtil;


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
        userMapper.updateUser(user);
        return user;
    }

    @RequestMapping("/time")
    String getTime(){
        return myUtil.getCureTime();
    }
}
