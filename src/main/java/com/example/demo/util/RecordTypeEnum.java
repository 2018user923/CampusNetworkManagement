package com.example.demo.util;

public enum RecordTypeEnum {
    userExpenses("用户消费记录", 0),

    userRechargeSubmit("用户提交充值申请中", 1),
    userRechargeSubmitCancel("用户提交充值申请取消", 2),
    userRechargeSubmitComplete("用户提交充值申请已通过", 3),
    userRechargeSubmitTurnDown("用户提交充值申请驳回", 4),

    turnDownUserRechargeSubmit("驳回用户充值申请", 5),
    PendingUserRechargeSubmit("待审批的充值申请", 6),
    checkUserInfo("查看用户信息", 7);

    private String key;
    private Integer val;

    RecordTypeEnum(String key, Integer val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public Integer getVal(RecordTypeEnum e) {
        Integer res = null;
        for (RecordTypeEnum type : RecordTypeEnum.values()) {
            if (type == e) return e.val;
        }
        return res;
    }

    public String getName(RecordTypeEnum e) {
        String res = null;
        for (RecordTypeEnum type : RecordTypeEnum.values()) {
            if (type == e) return e.key;
        }
        return res;
    }
}
