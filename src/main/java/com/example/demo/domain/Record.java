package com.example.demo.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {
    private Integer id; // id
    private String userName; // 学号
    private Date signIn; // 登入时间
    private Date signOut; // 登出时间
    private BigDecimal costData;//使用的流量
    private Long balance; //当前的余额
    private String costMoney;//消费金额
}