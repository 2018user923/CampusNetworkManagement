package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
        userDataService.insertUser(user);
        myUtil.sendMail(user.getEmail(), "绑定邮箱！");
        return user;
    }

    @GetMapping("/table")
    public ModelAndView table(HttpSession session) {
        ModelAndView res = new ModelAndView();
        User user = (User) session.getAttribute("user");
        List<Record> records = recordService.getRecordsByUserName(user.getUserName());
        res.addObject("records", records);
        res.setViewName("table");
        log.info(JSON.toJSONString(records));
        return res;
    }

    /**
     * 登录系统
     */
    @PostMapping("/login")
    public String login(User user, HttpSession session, HttpServletRequest request) {
        //todo 中级步骤需要校验是否登录成功,待编写。
        User userByUserName = userDataService.getUserByUserName(user.getUserName());
        if (userByUserName == null || !userByUserName.getPassWord().equals(user.getPassWord())) {
            return "/index";
        }
        user = userByUserName;
        //将流量数据存入缓存
        JSONObject netInfo = myUtil.getNetInfo();
        netInfo.put("signIn", new Date());
        cache.hset(EncryptionKey.netData, String.valueOf(user.getId()), netInfo);
        //将user对象存入 session 中.
        session.setAttribute("user", user);
        return "/main";
    }

    /**
     * 退出系统
     */
    @RequestMapping("/logOut")
    public ModelAndView logOut(HttpSession session) {
        ModelAndView res = new ModelAndView();
        res.setViewName("/index");


        //todo 这里后续需要编写一些异常处理
        User user = (User) session.getAttribute("user");

        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, String.valueOf(user.getId()));
        JSONObject curNetInfo = myUtil.getNetInfo();
        BigDecimal curData = curNetInfo.getBigDecimal("getData");
        //之前的流量
        BigDecimal preNetData = json.getBigDecimal("getData");
        //花费的流量
        BigDecimal costData = curData.subtract(preNetData);


        Record record = new Record();
        record.setUserName(user.getUserName());
        record.setSignIn(json.getDate("signIn"));
        record.setSignOut(new Date());
        record.setCostData(costData);

        recordService.insertRecord(record);

        //移除该用户的信息
        cache.hdel(EncryptionKey.netData, String.valueOf(user.getId()));
        session.removeAttribute("user");
        return res;
    }

    @PostMapping("/register")
    public String register(User user, HttpSession session) {
        //todo 在这之前需要判断是否注册成功。
        //注册用户
        int primaryKey = userDataService.insertUser(user);
        user.setId(primaryKey);
        //将流量数据存入缓存
        JSONObject netInfo = myUtil.getNetInfo();
        netInfo.put("signIn", new Date());

        cache.hset(EncryptionKey.netData, String.valueOf(primaryKey), netInfo);
        //将user对象存入 session 中.
        session.setAttribute("user", user);
        return "/main";
    }

    @RequestMapping("/form")
    public ModelAndView form(HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/form");

        User user = (User) session.getAttribute("user");

        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, String.valueOf(user.getId()));
        JSONObject curNetInfo = myUtil.getNetInfo();
        BigDecimal curData = curNetInfo.getBigDecimal("getData");
        //之前的流量
        BigDecimal preNetData = json.getBigDecimal("getData");
        //花费的流量
        BigDecimal costData = curData.subtract(preNetData).divide(new BigDecimal(1048576));

        modelAndView.addObject("costData", costData.toBigInteger());
        return modelAndView;
    }
}
