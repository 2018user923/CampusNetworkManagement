package com.example.demo.service.impl;

import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.RecordMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.HttpService;
import com.example.demo.service.RecordsService;
import com.example.demo.util.EncryptionKey;
import com.example.demo.util.RecordTypeEnum;
import com.example.demo.util.RedisUtil;
import com.example.demo.util.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    public ResultResponse turnDownRecord(HttpServletRequest request, Integer id) {
        String ipAddress = httpService.getIpAddress(request);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
        recordDataService.updateRecordByIdForType(id, RecordTypeEnum.userRechargeSubmitTurnDown.getVal(), user.getUserName());
        return ResultResponse.createError(200, null);
    }

}
