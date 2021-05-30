package com.example.demo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.domain.MappingTitleAndButtons;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.RecordsService;
import com.example.demo.service.UserService;
import com.example.demo.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class RecordsServiceImpl implements RecordsService {
    @Autowired
    private RecordMapper recordDataService;

    /*redis缓存*/
    @Resource
    private RedisUtil cache;

    @Resource
    private HttpService httpService;

    @Autowired
    private UserMapper userDataService;

    @Autowired
    private Map<Integer, MappingTitleAndButtons> map;

    @Autowired
    private MyUtil util;

    @Autowired
    private UserService userService;

    @Autowired
    private MyUtil myUtil;

    /**
     * 将该申请取消
     */
    @Override
    public ResultResponse cancelRecordHandler(Integer id) {
        recordDataService.updateRecordByIdForType(id, RecordTypeEnum.userRechargeSubmitCancel.getVal(), null);
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse repeatedSubmitHandler(Integer id) {
        recordDataService.updateRecordByIdForType(id, RecordTypeEnum.userRechargeSubmit.getVal(), null);
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse deleteSubmitHandler(Integer id) {
        recordDataService.deleteRecordById(id);
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse agreeRecordHandler(HttpServletRequest request, Integer id) {
        //这是管理员的信息
        String ipAddress = httpService.getIpAddress(request);
        User updateUser = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);

        DBInputInfo dbInputInfo = DBInputInfo.builder().id(id).build();

        //申请信息
        Record record = recordDataService.getRecordById(id);
        //申请者
        User submitUser = userDataService.getUserByUserName(record.getUserName());
        submitUser.setBalance(submitUser.getBalance().add(record.getRechargeAmount()));
        userDataService.updateUser(submitUser);

        recordDataService.updateRecordByIdForType(id, RecordTypeEnum.userRechargeSubmitComplete.getVal(), updateUser.getUserName());
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse turnDownRecordHandler(HttpServletRequest request, Integer id) {
        User user = userService.getUserInfoHandler(request);
        recordDataService.updateRecordByIdForType(id, RecordTypeEnum.userRechargeSubmitTurnDown.getVal(), user.getUserName());
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse getRecordsHandler(HttpServletRequest request, DBInputInfo dbInputInfo) {
        List<Record> records = null;
        if (dbInputInfo.getUserName() == null || dbInputInfo.equals("null") || dbInputInfo.equals("")) {
            User user = userService.getUserInfoHandler(request);
            dbInputInfo.setUserName(user.getUserName());
        }
        if (dbInputInfo.getTypes() != null && !dbInputInfo.getTypes().isEmpty()) {
            switch (dbInputInfo.getTypes().get(0)) {
                case 5 -> {
                    dbInputInfo.setUserName(null);
                    dbInputInfo.getTypes().set(0, 4);
                    records = recordDataService.getRecords(dbInputInfo);
                    return ResultResponse.createSuccessForTypeAndRecords(null, 5, records, map, util);
                }
                case 6 -> {
                    dbInputInfo.setUserName(null);
                    dbInputInfo.getTypes().set(0, 1);
                    records = recordDataService.getRecords(dbInputInfo);
                    return ResultResponse.createSuccessForTypeAndRecords(null, 6, records, map, util);
                }
            }
        }
        records = recordDataService.getRecords(dbInputInfo);
        return ResultResponse.createSuccessForTypeAndRecords(null, dbInputInfo.getTypes().get(0), records, map, util);
    }

    @Override
    public ResultResponse getRecordsForLogInHandler(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);
        JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
        DBInputInfo dbInputInfo = DBInputInfo.builder()
                .createTime(json.getDate("signIn"))
                .userName(json.getString("userName"))
                .types(Collections.singletonList(1))
                .build();
        List<Record> records = recordDataService.getRecords(dbInputInfo);
        return ResultResponse.createSuccessForTypeAndRecords(null, dbInputInfo.getTypes().get(0), records, map, util);
    }
}
