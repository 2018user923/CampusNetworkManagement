package com.example.demo.service;

import com.example.demo.util.ResultResponse;

import javax.servlet.http.HttpServletRequest;

public interface RecordsService {
    ResultResponse cancelRecordHandler(Integer id);

    ResultResponse repeatedSubmitHandler(Integer id);

    ResultResponse deleteSubmitHandler(Integer id);

    ResultResponse agreeRecordHandler(HttpServletRequest request, Integer id);

    ResultResponse turnDownRecord(HttpServletRequest request, Integer id);
}

//重复提交