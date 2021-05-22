package com.example.demo.util;

public enum RecordTypeEnum {
    userInfoUpdate("用户个人信息更新", 1),
    userRechargeSubmit("用户提交充值申请中", 2),
    userRechargeSubmitCancel("用户提交充值申请取消", 2);

    private String name;
    private Integer val;

    RecordTypeEnum(String name, Integer val) {
        this.name = name;
        this.val = val;
    }

}
