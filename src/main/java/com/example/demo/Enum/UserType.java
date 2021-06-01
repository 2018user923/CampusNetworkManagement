package com.example.demo.Enum;

/**
 * 描述: 用户类型枚举
 *
 * @Author: <247702560@qq.com>
 * @Date: 2021/6/1 15:12
 */
public enum UserType {
    administrator("管理员用户", 0),
    normalUser("普通用户", 1),
    blacklistUser("黑名单用户", 2),
    notFind("未找到", -99);

    private String key;
    private Integer val;

    UserType(String key, Integer val) {
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

    public static UserType getKey(Integer val) {
        for (UserType key : UserType.values()) {
            if (key.getVal().equals(val)) {
                return key;
            }
        }
        return UserType.notFind;
    }
}
