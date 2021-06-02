package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.Chat;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.ChatMapper;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.DBInputInfo;
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
import java.util.Collections;
import java.util.List;
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
    private RecordMapper recordMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private ChatMapper chatMapper;

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
        DBInputInfo info = new DBInputInfo();

        info.setUserName("zhangsan");
        info.setTypes(Collections.singletonList(0));


        List<Record> records = recordMapper.getRecordsByUserNameAndTypes(info);

        System.out.println("size: " + records.size());

        records.forEach(System.out::println);

//        User user = userMapper.getUserByUserName("刘备");
//        List<Record> records = recordMapper.getRecordsByUserNameForPages(user.getUserName(), 0, 10);
//        records.forEach(System.out::println);
//        records = recordMapper.getRecordsByUserNameForPages(user.getUserName(), 10, 10);
//        records.forEach(System.out::println);
    }

    @Test
    void testRecordForType() {

        Chat chat = new Chat();
        chat.setContent("666");

        int insert = chatMapper.insert(chat);
        System.out.println("primaryKey: " + insert);



//        List<Record> records = recordMapper.getRecordsByUserNameAndTypesWithDate("李四", Arrays.asList(0, 1), "2021-5-20", "2021-5-29", null, null);
//        records.forEach(System.out::println);


//        Record record = new Record();
//        record.setUserName("刘备");
//        record.setType(1);
//        recordMapper.insertRecord(record);
//
//        List<Record> records = recordMapper.getRecordsByUserNameAndTypes("刘备", Arrays.asList(0, 1));
//        records.forEach(System.out::println);
    }
}
/*
数据查询信息
 */