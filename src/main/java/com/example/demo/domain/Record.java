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
    private String userName;//账号
    private Date signIn; //登入时间
    private Date signOut; //登出时间
    private BigDecimal costData;//使用的流量
    private Long balance; //当前的余额
    private String costMoney;//消费金额
    private Integer status;//审批状态，审批中、通过
    private Date createTime;//该记录的创建时间
    private Integer operatorResult;//审批之后的结果
    private Integer type;

    public static Record create(User user) {
        Record record = new Record();
        record.setUserName(user.getUserName());
//        record.setSignIn(json.getDate("signIn"));
        record.setSignOut(new Date());



        return record;
    }
}