package com.example.demo;

import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.domain.Record;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.util.Random;

@SpringBootTest
class DemoApplicationTests {
    @Resource
    private Random random;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private MyUtil myUtil;

    @Value("${user.images.path}")
    private String userImagesPath;

    @Value("${user.images.default}")
    private String userImagesDefault;

    @Resource
    private RecordMapper recordMapper;

    @Autowired
    private RedisUtil cache;

    @Test
    void testEmail() {
        myUtil.sendMail("247702560@qq.com", "验证码！！");
    }

    @Test
    void testDataBase() {
//        User user = new User();
//        user.setEmail("247702560@qq.com");
//        user.setUserName("TuQi");
//        user.setPassWord("123");
//        user.setPhone("181");
//        user.setCollegeNum("201711110");
//        user.setIdCard("360421");
//        user.setAvatar("123666");
//        userMapper.insertUser(user);
//        System.out.println(user);
//        user.setId(1);
//        user.setEmail("110");
//        user.setPassWord("12344");
//        userMapper.updateUser(user);
//        System.out.println(userMapper.getUserById(user.getId()));
//        userMapper.deleteUserById(1);
//        System.out.println("查询的结果为 : " + userMapper.getUserById(1));
        System.out.println(userMapper.getUserByCollegeNum("201711110"));
    }

    @Test
    void testFile() {
        File file = new File(userImagesDefault);
        System.out.println(file.getAbsoluteFile());
    }

    @Test
    void testDateTime() {
        String cureTime = myUtil.getCureTime();
        Record record = new Record();
        //      测试插入数据.
        record.setBalance(1882L);
        record.setSignIn(cureTime);
        record.setSignOut(cureTime);
        record.setCollegeNum("201822222");
        recordMapper.insertRecord(record);
    }

    @Test
    void testCache() {
        cache.hset("zhangsan","name","111000",10);
        cache.del("hello");
        System.out.println(cache.hget("zhangsan","name"));
        System.out.println(cache.hget("zhangsan","111000"));
        System.out.println(cache.get("name"));
    }

}
