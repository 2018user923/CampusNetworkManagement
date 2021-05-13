package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.MyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

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

    /**
     * @param id 管理员的 id
     * @return 返回从数据库中查询到的对象。
     */
    @GetMapping("/test/getUserById/{id}")
    User getUserById(@PathVariable("id") Integer id) {
        return userDataService.getUserById(id);
    }

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
}
