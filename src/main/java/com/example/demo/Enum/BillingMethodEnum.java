package com.example.demo.Enum;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述: 计费方式枚举
 *
 * @Author: <247702560@qq.com>
 * @Date: 2021/6/13 17:19
 */
public enum BillingMethodEnum {
    //时间计费
    timeBilling("时间计费", 0),
    //流量计费
    trafficBilling("流量计费", 1),

    notFind("未找到", -1);

    private String key;
    private Integer val;

    BillingMethodEnum(String key, Integer val) {
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

    public static List<Integer> getBillingMethods() {
        return Arrays.stream(BillingMethodEnum.values()).map(BillingMethodEnum::getVal).collect(Collectors.toList());
    }

    public static BillingMethodEnum getEnumByVal(Integer val) {
        for (BillingMethodEnum billMethod : BillingMethodEnum.values()) {
            if (billMethod.getVal().equals(val)) return billMethod;
        }
        return notFind;
    }
}
