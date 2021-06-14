package com.example.demo.controller;

import com.example.demo.util.EncryptionKey;
import com.example.demo.util.RedisUtil;
import com.example.demo.util.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 测试控制器
 *
 * @author <247702560@qq.com>
 * @since 2021/6/14 11:31
 */
@Controller
@Slf4j
@RestController
public class TestController {
    @Autowired
    private RedisUtil cache;

    @CrossOrigin
    @RequestMapping("/test/hGetAll")
    ResultResponse hetAll() {
        Map<Object, Object> res = cache.hmget(EncryptionKey.loginIpAddress);
        return ResultResponse.createSimpleSuccess(null, res);
    }
}
