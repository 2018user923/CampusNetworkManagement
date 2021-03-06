package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;


/**
 * 用户的基本信息表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 *
 * @Description 用户类
 * @Author <247702560@qq.com>
 * @Date 2021/5/31 22:59
 */
public class User {
    private Integer id; // 主键
    private String userName; // 用户名，即真实姓名
    private String passWord; //密码
    private String phone; //电话
    private String email; // 邮箱
    private String idCard; // 身份证.
    private String avatar; //头像的 url 地址
    private BigDecimal balance;//当前余额
    private Integer type;//账户类型,type 为 0,即为管理员账户，否则为普通用户
    private String authority;//权限，使用 list 存储之后再使用 json.parse 解析
    private Date createTime;
    private Set<Integer> authorityToSet;//将 authority 转化为 set 类型
    private Integer billingMethod;//用户选择的计费方式。默认选择 0，即按照时长计费
}
