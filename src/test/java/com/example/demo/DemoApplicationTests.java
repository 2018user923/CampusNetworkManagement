package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.MyUtil;
import com.example.demo.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SpringBootTest
class DemoApplicationTests {
    @Resource
    private Random random;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private MyUtil myUtil;

    @Value("${user.images.path}")
    private String userImagesPath;

    @Value("${user.images.default}")
    private String userImagesDefault;

    @Value("${python.netWorkTraffic}")
    private String netWorkTrafficPath;


    @Value("${user.newUserAuthority}")
    private String newUserAuthority;

    @Autowired
    private RedisUtil cache;

    @Test
    void testEmail() {
        myUtil.sendMail("247702560@qq.com", "验证码！！");
    }

    @Test
    void testPythonScript() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(netWorkTrafficPath);
        processBuilder.redirectErrorStream(true);
        try {
            //启动进程
            Process start = processBuilder.start();
            //获取输入流
            InputStream inputStream = start.getInputStream();
            //转成字符输入流
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
            int len = -1;
            char[] c = new char[1024];
            StringBuffer outputString = new StringBuffer();
            //读取进程输入流中的内容
            while ((len = inputStreamReader.read(c)) != -1) {
                String s = new String(c, 0, len);
                outputString.append(s);
            }
            JSONObject jsonObject = JSON.parseObject(outputString.toString());
            System.out.println(jsonObject);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testUpdateUser() {
        User user = userMapper.getUserByUserName("刘备");

        user.setEmail("9074133022@qq.com");
        user.setPassWord("123456");
        user.setPhone("18170217027");
        int primaryKey = userMapper.updateUser(user);
        System.out.println(primaryKey);
        System.out.println(userMapper.getUserById(primaryKey));
    }

    @Test
    void testRecordForPages() {
//        User user = userMapper.getUserByUserName("刘备");
//        List<Record> records = recordMapper.getRecordsByUserNameForPages(user.getUserName(), 0, 10);
//        records.forEach(System.out::println);
//        records = recordMapper.getRecordsByUserNameForPages(user.getUserName(), 10, 10);
//        records.forEach(System.out::println);
    }

    @Test
    void testRecordForType() {

        Record record = new Record();
        record.setUserName("刘备");
        record.setType(1);
        recordMapper.insertRecord(record);


        List<Record> records = recordMapper.getRecordsByUserNameAndType("刘备", 1);
        records.forEach(System.out::println);
    }
}
