package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 操作记录表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {
    private Integer id; //id

    private String userName;//谁创建的这条记录
    private String updateUserName;//修改者，谁审批了这条记录

    private Date signIn; //登入时间，这里指代用户登录和登出的时间。
    private Date signOut; //登出时间
    private BigDecimal costData;//使用的流量
    private Long balance; //当前的余额
    private BigDecimal costMoney;//消费金额，用户在登入登出期间消费的金额

    private Date createTime;//该记录的创建时间
    private Date updateTime;//该记录的更新

    private BigDecimal rechargeAmount;//充值的金额

    private Integer type;//该记录的类型
}