package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.mapper.UserMapper;
import com.example.demo.domain.User;
import com.example.demo.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Controller
@Slf4j
public class ResponseController {
    @Resource
    UserMapper userMapper;

    @Value("${user.images.path}")
    String userImagesPath;

    @Value("${user.images.default}")
    String userImagesDefault;

    @Resource
    MyUtil myUtil;


    @PostMapping("/insertUser")
    @ResponseBody
    public User upload1(@RequestParam("file") MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            log.info("没有上传任何文件！！！");
            user.setAvatar(userImagesDefault);
        } else {
            // 存储照片的绝对路径。
            String absSolutePath = userImagesPath + "/" + user.getCollegeNum();
            file.transferTo(new File(absSolutePath));
            user.setAvatar(absSolutePath);
        }
        //将数据插入数据库中。
        userMapper.insertUser(user);
        myUtil.sendMail(user.getEmail(), "绑定邮箱！");
        return user;
    }

    @PostMapping("/abc")
    @ResponseBody
    public String register(@RequestParam Map<String, String> map) {
        return JSON.toJSONString(map);
    }
}
