package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
@Slf4j
public class MyUtil {
    @Resource
    private Random random;

    @Resource
    private JavaMailSenderImpl mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${user.spend.proportion}")
    private Double proportion;

    @Value(("${python.netWorkTraffic}"))
    private String netWorkTrafficPath;

    @Resource
    private DateTimeFormatter dateTimeFormatter;

    /**
     * 发送邮件。
     *
     * @param address 发送邮件的地址
     * @param Subject 邮件的主题
     */
    public void sendMail(String address, String Subject) {
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; ++i) {
            builder.append(random.nextInt(10));
        }
        SimpleMailMessage message = new SimpleMailMessage();
        // 邮件发送者
        message.setFrom(from);
        // 邮件接受者
        message.setTo(address);
        // 设置主题
        message.setSubject(Subject);
        // 设置正文内容
        message.setText("验证码为 : " + builder.toString());
        mailSender.send(message);
    }

    /**
     * 获取当前时间
     */
    public String getCureTime() {
        //将时间戳转化为字符串。
        return dateTimeFormatter.format(LocalDateTime.now());
    }

    /**
     * 将字符串转化为时间戳，返回单位是 ms，转化成 s 需要 / 1000
     */
    public Long stringTimeToTimeMillis(String time) {
        return LocalDateTime.from(LocalDateTime.parse(time, dateTimeFormatter)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 计算 2 段时间之间的花费。
     */
    public double calcSpend(String signIn, String signOut) {
        // difference 的单位是毫秒, 差值
        long difference = stringTimeToTimeMillis(signOut) - stringTimeToTimeMillis(signIn);
        double spend = difference * 1.0 / 1000 / 3600 * proportion;
        log.info(signIn + " ----> " + signOut + "花费的金额为 :" + spend);
        return spend;
    }

    /**
     * 调用python 脚本获取流量数据
     *
     * @return
     */
    public JSONObject getNetInfo() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        StringBuffer outputString = new StringBuffer();
        processBuilder.command(netWorkTrafficPath);
        processBuilder.redirectErrorStream(true);
        JSONObject res = new JSONObject();
        try {
            Process start = processBuilder.start();
            InputStream inputStream = start.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "gbk");
            int len = -1;
            char[] c = new char[1024];
            //读取进程输入流中的内容
            while ((len = inputStreamReader.read(c)) != -1) {
                String s = new String(c, 0, len);
                outputString.append(s);
            }
            res = JSON.parseObject(outputString.toString());
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
