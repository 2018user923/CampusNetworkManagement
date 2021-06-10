package com.example.demo.util;

import com.example.demo.domain.MappingTitleAndButtons;
import com.example.demo.domain.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 这个用来响应 dataController 的结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private static SimpleDateFormat simpleDateFormat;

    static {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 状态码 200 表示返回结果正确.
     */
    Integer code;
    /**
     * 错误的数据
     */
    Error error;
    /**
     * 成功之后的数据
     */
    Success success;

    @Data
    public static class Error {
        /**
         * 输出错误的提示信息
         */
        String message;
    }

    @Data
    public static class Success {
        /**
         * 响应成功之后，应该跳转的页面
         */
        String url;

        /**
         * 响应成功之后应该获得的数据,这里的数据必须与 title 一一对应！
         */
        Object data;

        /**
         * 标题
         */
        Object titles;

        /**
         * 取消、删除、再次提交、同意、驳回
         */
        Object buttons;
    }

    public static ResultResponse createError(Integer code, String message) {
        ResultResponse response = new ResultResponse();
        response.setCode(code);

        Error error = new Error();
        error.setMessage(message);

        response.setError(error);
        return response;
    }

    public static ResultResponse createSimpleSuccess(String url, Object data) {
        ResultResponse response = new ResultResponse();
        response.setCode(200);

        Success success = new Success();
        response.setSuccess(success);

        success.setUrl(url);
        success.setData(data);
        return response;
    }

    public static ResultResponse createSuccessForTypeAndRecords(String url, Integer type, List<Record> list, Map<Integer, MappingTitleAndButtons> map, MyUtil util) {
        ResultResponse response = createSimpleSuccess(url, null);


        ArrayList<List<Object>> data = new ArrayList<>(list.size());
        switch (type) {
            case 0 -> list.forEach(r -> {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(r.getId());
                objects.add(simpleDateFormat.format(r.getSignIn()));
                objects.add(simpleDateFormat.format(r.getSignOut()));
                objects.add(r.getCostData());
                objects.add(util.calcMinute(r.getSignIn(), r.getSignOut()));
                objects.add(r.getBalance().divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP));
                objects.add(r.getCostMoney().divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP));
                data.add(objects);
            });
            case 1 -> list.forEach(r -> {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(r.getId());
                objects.add(simpleDateFormat.format(r.getCreateTime()));
                objects.add(r.getRechargeAmount());
                data.add(objects);
            });
            case 2 -> list.forEach(r -> {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(r.getId());
                objects.add(simpleDateFormat.format(r.getCreateTime()));
                objects.add(simpleDateFormat.format(r.getUpdateTime()));
                objects.add(r.getRechargeAmount());
                data.add(objects);
            });
            case 3, 4 -> list.forEach(r -> {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(r.getId());
                objects.add(simpleDateFormat.format(r.getCreateTime()));
                objects.add(simpleDateFormat.format(r.getUpdateTime()));
                objects.add(r.getUpdateUserName());
                objects.add(r.getRechargeAmount());
                data.add(objects);
            });
            case 5 -> list.forEach(r -> {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(r.getId());
                objects.add(simpleDateFormat.format(r.getCreateTime()));
                objects.add(simpleDateFormat.format(r.getUpdateTime()));
                objects.add(r.getUserName());
                objects.add(r.getUpdateUserName());
                objects.add(r.getRechargeAmount());
                data.add(objects);
            });
            case 6 -> list.forEach(r -> {
                ArrayList<Object> objects = new ArrayList<>();
                objects.add(r.getId());
                objects.add(simpleDateFormat.format(r.getCreateTime()));
                objects.add(r.getUserName());
                objects.add(r.getRechargeAmount());
                data.add(objects);
            });
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        response.getSuccess().setData(data);
        response.getSuccess().setTitles(map.get(type).getTitle());
        response.getSuccess().setButtons(map.get(type).getButtons());
        return response;
    }

}
