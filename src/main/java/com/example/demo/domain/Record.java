package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {
    private Integer id; // id
    private String collegeNum; // 学号
    private String signIn; // 登入时间
    private String signOut; // 登出时间
    private String costData;//使用的流量
    private Long balance; //当前的余额
    private String costMoney;//消费金额
}