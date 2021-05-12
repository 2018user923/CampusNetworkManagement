package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.domain.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Controller
@Slf4j
public class ResponseController {
    /*用户持久层服务*/
    @Resource
    private UserMapper userDataService;

    /*默认的头像地址*/
    @Value("${user.images.path}")
    private String userImagesPath;

    @Value("${user.images.default}")
    private String userImagesDefault;

    /*redis 缓存*/
    @Resource
    private RedisUtil cache;

    /*工具类*/
    @Resource
    private MyUtil myUtil;


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
        userDataService.insertUser(user);
        myUtil.sendMail(user.getEmail(), "绑定邮箱！");
        return user;
    }

    @GetMapping("/table")
    public ModelAndView a() {
        ModelAndView res = new ModelAndView();
        res.setViewName("table");
        return res;
    }

    /**
     * 登录系统
     */
    @RequestMapping("/login")
    public String login(User user, HttpSession session) {
        log.info("user = " + JSON.toJSONString(user));
        //todo 中级步骤需要校验是否登录成功,待编写。
        session.setAttribute("user", user);
        return "/main";
    }

    /**
     * 退出系统
     */
    @RequestMapping("/logOut")
    public ModelAndView logOut(HttpServletRequest request, User user) {
        //todo 这里后续需要编写一些异常处理
        ModelAndView res = new ModelAndView();
        request.getSession().removeAttribute("user");
        cache.hdel(EncryptionKey.userLoginInfo, String.valueOf(user.getId()));
        return res;
    }
}
