package com.example.demo.service;

import com.example.demo.util.DBInputInfo;
import com.example.demo.util.ResultResponse;

import javax.servlet.http.HttpServletRequest;

public interface RecordsService {
    ResultResponse cancelRecordHandler(Integer id);

    ResultResponse repeatedSubmitHandler(Integer id);

    /**
     * 描述: 根据传入的 type 及其 id 删除指定表中的信息
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/11 10:52
     * @param: id
     * @param: type
     */
    ResultResponse deleteSubmitHandler(Integer id, Integer type);

    ResultResponse agreeRecordHandler(HttpServletRequest request, Integer id);

    ResultResponse turnDownRecordHandler(HttpServletRequest request, Integer id);

    ResultResponse getListDataHandler(HttpServletRequest request, DBInputInfo dbInputInfo);

    ResultResponse getRecordsForLogInHandler(HttpServletRequest request);
}

//重复提交