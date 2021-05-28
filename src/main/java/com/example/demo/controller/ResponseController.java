package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.UserService;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

@Controller
@Slf4j
public class ResponseController {
    /*用户持久层服务*/
    @Resource
    private UserMapper userDataService;

    /*消费记录服务*/
    @Resource
    private RecordMapper recordService;

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

    @Autowired
    private HttpService httpService;

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
        if(user.getBalance() == null){
            user.setBalance(new BigDecimal(0));
        }
        userDataService.insertUser(user);
        myUtil.sendMail(user.getEmail(), "绑定邮箱！");
        return user;
    }

    @GetMapping("/table")
    public ModelAndView table(HttpSession session) {
        ModelAndView res = new ModelAndView();
        res.setViewName("table");
        return res;
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

//    @PostMapping("/register")
//    public String register(User user, HttpSession session) {
//        //todo 在这之前需要判断是否注册成功。
//        //注册用户
//        int primaryKey = userDataService.insertUser(user);
//        user.setId(primaryKey);
//        //将流量数据存入缓存
//        JSONObject netInfo = myUtil.getNetInfo();
//        netInfo.put("signIn", new Date());
//
//        cache.hset(EncryptionKey.netData, String.valueOf(primaryKey), netInfo);
//        //将user对象存入 session 中.
//        session.setAttribute("user", user);
//        return "/main";
//    }

    @RequestMapping("/form")
    public ModelAndView form(HttpServletRequest request) {
        return userService.form(request);
    }

    @RequestMapping({"/", "/index"})
    String loginAndRegister() {
        return "/index";
    }
}
