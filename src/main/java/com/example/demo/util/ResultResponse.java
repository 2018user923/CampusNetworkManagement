package com.example.demo.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 这个用来响应 dataController 的结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
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
         * 响应成功之后应该获得的数据
         */
        Object data;
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


}
