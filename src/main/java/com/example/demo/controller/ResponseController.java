package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.UserService;
import com.example.demo.util.MyUtil;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;

@Controller
@Slf4j
public class ResponseController {
    /*用户持久层服务*/
    @Resource
    private UserMapper userDataService;

    @Value("${user.images.default}")
    private String userImagesDefault;

    /*工具类*/
    @Resource
    private MyUtil myUtil;

    @Autowired
    private UserService userService;


    @PostMapping("/insertUser")
    @ResponseBody
    public User upload1(@RequestParam("file") MultipartFile file, User user) throws IOException {
        if (file.isEmpty()) {
            log.info("没有上传任何文件！！！");
            user.setAvatar(userImagesDefault);
        } else {
            // 存储照片的绝对路径。
//            String absSolutePath = userImagesPath + "/" + user.getCollegeNum();
//            file.transferTo(new File(absSolutePath));
//            user.setAvatar(absSolutePath);
        }
        //将数据插入数据库中。
        if (user.getBalance() == null) {
            user.setBalance(new BigDecimal(0));
        }
        userDataService.insertUser(user);
        myUtil.sendMail(user.getEmail(), "绑定邮箱！");
        return user;
    }

    /**
     * 登录系统,登录成功之后将请求的 ip 地址及其 userName 存入缓存之中。
     */
    @PostMapping("/login")
    public String login(User user, HttpServletRequest request) {
        if (userService.loginHandler(user, request)) {
            return "/main";
        } else {
            return "/index";
        }
    }

    /**
     * 退出系统，删除 ip 对应的信息,并且将流量数据插入其中
     */
    @RequestMapping("/logOut")
    public String logOut(HttpServletRequest request) {
        ResultResponse response = userService.logOutHandler(request);
        return "redirect:" + response.getSuccess().getUrl();
    }
}
