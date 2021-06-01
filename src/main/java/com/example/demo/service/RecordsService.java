package com.example.demo.service;

import com.example.demo.util.DBInputInfo;
import com.example.demo.util.ResultResponse;

import javax.servlet.http.HttpServletRequest;

public interface RecordsService {
    ResultResponse cancelRecordHandler(Integer id);

    ResultResponse repeatedSubmitHandler(Integer id);

    ResultResponse deleteSubmitHandler(Integer id);

    ResultResponse agreeRecordHandler(HttpServletRequest request, Integer id);

    ResultResponse turnDownRecordHandler(HttpServletRequest request, Integer id);

    ResultResponse getListDataHandler(HttpServletRequest request, DBInputInfo dbInputInfo);

    ResultResponse getRecordsForLogInHandler(HttpServletRequest request);
}

//重复提交