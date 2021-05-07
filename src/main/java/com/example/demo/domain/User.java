package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id; // 主键
    private String userName; // 用户名，即真实姓名
    private String passWord; //密码
    private String phone; //电话
    private String email; // 邮箱
    private String idCard; // 身份证.
    private String collegeNum; // 学号
    private String avatar; //头像的 url 地址
}
