package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.demo.Enum.TypeEnum;
import com.example.demo.Enum.UserType;
import com.example.demo.domain.Chat;
import com.example.demo.domain.MappingTitleAndButtons;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.mapper.ChatMapper;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private SimpleDateFormat simpleDateFormat;


    @Autowired
    private ChatMapper ChatDataService;

    /**
     * 将该申请取消
     */
    @Override
    public ResultResponse cancelRecordHandler(Integer id) {
        recordDataService.updateRecordByIdForType(id, TypeEnum.userRechargeSubmitCancel.getVal(), null);
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse repeatedSubmitHandler(Integer id) {
        recordDataService.updateRecordByIdForType(id, TypeEnum.userRechargeSubmit.getVal(), null);
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse deleteSubmitHandler(Integer id, Integer type) {
        if (type == 11) {
            ChatDataService.delete(id);
        } else {
            recordDataService.deleteRecordById(id);
        }
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse agreeRecordHandler(HttpServletRequest request, Integer id) {
        //这是管理员的信息
        String ipAddress = httpService.getIpAddress(request);
        User administrator = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);

        DBInputInfo dbInputInfo = DBInputInfo.builder().id(id).build();

        //申请信息
        Record record = recordDataService.getRecordById(id);
        //申请者
        User user = userDataService.getUserByUserName(record.getUserName());
        user.setBalance(user.getBalance().add(record.getRechargeAmount()));
        userDataService.updateUser(user);

        //判断user对象当前是否在线。
        String userIpAddress = (String) cache.hget(EncryptionKey.loginIpAddress, user.getUserName());
        if (userIpAddress != null) {
            cache.hset(EncryptionKey.userLoginInfo, userIpAddress, user);
            user.setAuthorityToSet(JSON.parseObject(user.getAuthority(), new TypeReference<>() {
            }));
        }

        recordDataService.updateRecordByIdForType(id, TypeEnum.userRechargeSubmitComplete.getVal(), administrator.getUserName());
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse turnDownRecordHandler(HttpServletRequest request, Integer id) {
        User user = userService.getUserInfoHandler(request);
        recordDataService.updateRecordByIdForType(id, TypeEnum.userRechargeSubmitTurnDown.getVal(), user.getUserName());
        return ResultResponse.createError(200, null);
    }

    @Override
    public ResultResponse getListDataHandler(HttpServletRequest request, DBInputInfo dbInputInfo) {
        List<Record> records = null;
        if (dbInputInfo.getUserName() == null) {
            User user = userService.getUserInfoHandler(request);
            dbInputInfo.setUserName(user.getUserName());
        }
        if (dbInputInfo.getUserName().equals("all")) {
            dbInputInfo.setUserName(null);
        }
        Integer type = dbInputInfo.getTypes().get(0);
        if (dbInputInfo.getTypes() != null && !dbInputInfo.getTypes().isEmpty()) {
            switch (type) {
                case 5 -> {
                    dbInputInfo.getTypes().set(0, 4);
                    records = recordDataService.getRecords(dbInputInfo);
                    return ResultResponse.createSuccessForTypeAndRecords(null, 5, records, map, util);
                }
                case 6 -> {
                    dbInputInfo.getTypes().set(0, 1);
                    records = recordDataService.getRecords(dbInputInfo);
                    return ResultResponse.createSuccessForTypeAndRecords(null, 6, records, map, util);
                }
                case 7 -> {
                    //查询每个普通用户的信息
                    // Arrays.asList("编号", "用户名", "电话", "邮箱", "身份证", "当前余额", "操作"),
                    ResultResponse response = ResultResponse.createSimpleSuccess(null, null);
//                    List<User> user = userDataService.getUserByType(UserType.normalUser.getVal());
                    dbInputInfo.setTypes(Collections.singletonList(UserType.normalUser.getVal()));
                    List<User> user = userDataService.getUser(dbInputInfo);

                    ArrayList<List<Object>> data = new ArrayList<>(user.size());
                    user.forEach(o -> {
                        ArrayList<Object> objects = new ArrayList<>();
                        objects.add(o.getId());
                        objects.add(o.getUserName());
                        objects.add(simpleDateFormat.format(o.getCreateTime()));
                        objects.add(o.getPhone());
                        objects.add(o.getEmail());
                        objects.add(o.getIdCard());
                        objects.add(o.getBalance().divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP));
                        data.add(objects);
                    });
                    response.getSuccess().setData(data);
                    response.getSuccess().setTitles(map.get(type).getTitle());
                    response.getSuccess().setButtons(map.get(type).getButtons());
                    return response;
                }
                case 8 -> {
                    ResultResponse response = ResultResponse.createSimpleSuccess(null, null);
//                    List<User> user = userDataService.getUserByType(UserType.blacklistUser.getVal());
                    dbInputInfo.setTypes(Collections.singletonList(UserType.blacklistUser.getVal()));
                    List<User> user = userDataService.getUser(dbInputInfo);
                    ArrayList<List<Object>> data = new ArrayList<>(user.size());
                    user.forEach(o -> {
                        ArrayList<Object> objects = new ArrayList<>();
                        objects.add(o.getId());
                        objects.add(o.getUserName());
                        objects.add(simpleDateFormat.format(o.getCreateTime()));
                        objects.add(o.getPhone());
                        objects.add(o.getEmail());
                        objects.add(o.getIdCard());
                        objects.add(o.getBalance().divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP));
                        data.add(objects);
                    });
                    response.getSuccess().setData(data);
                    response.getSuccess().setTitles(map.get(type).getTitle());
                    response.getSuccess().setButtons(map.get(type).getButtons());
                    return response;
                }
                case 9 -> {
                    ResultResponse response = ResultResponse.createSimpleSuccess(null, null);
                    DBInputInfo info = DBInputInfo.builder().types(Arrays.asList(UserType.administrator.getVal(), UserType.normalUser.getVal())).build();
                    List<User> user = userDataService.getUser(info);
                    ArrayList<List<Object>> data = new ArrayList<>(user.size());
                    user.forEach(o -> {
                        ArrayList<Object> objects = new ArrayList<>();
                        objects.add(o.getId());
                        objects.add(o.getUserName());
                        data.add(objects);
                    });
                    response.getSuccess().setData(data);
                    //注意，这里没有title 和 buttons ，单纯返回两种用户的信息。
                    return response;
                }
                case 10 -> {
                    dbInputInfo.getTypes().set(0, 0);
                    ResultResponse response = ResultResponse.createSimpleSuccess(null, null);
                    records = recordDataService.getRecords(dbInputInfo);
                    ArrayList<List<Object>> data = new ArrayList<>(1);
                    //Arrays.asList("使用流量（字节）", "使用流量（MB）", "使用时长（分钟）", "消费金额"),
                    BigDecimal costData = new BigDecimal("0");
                    BigDecimal costMinute = new BigDecimal("0");
                    BigDecimal costMoney = new BigDecimal("0");
                    ArrayList<Object> objects = new ArrayList<>();
                    for (Record o : records) {
                        costData = costData.add(o.getCostData());
                        costMinute = costMinute.add(new BigDecimal(util.calcMinute(o.getSignIn(), o.getSignOut())));
                        costMoney = costMoney.add(o.getCostMoney());
                    }
                    objects.add(costData);
                    objects.add(costMinute);
                    objects.add(costMoney.divide(new BigDecimal(1000), 3, RoundingMode.HALF_UP));
                    data.add(objects);
                    response.getSuccess().setData(data);
                    response.getSuccess().setTitles(map.get(type).getTitle());
                    response.getSuccess().setButtons(map.get(type).getButtons());
                    return response;
                }
                case 11 -> {
                    //Arrays.asList("编号", "创建时间", "创建者", "内容"),
                    dbInputInfo.getTypes().set(0, 2);
                    ResultResponse response = ResultResponse.createSimpleSuccess(null, null);
                    List<Chat> chats = ChatDataService.getChats(dbInputInfo);
                    ArrayList<List<Object>> data = new ArrayList<>(chats.size());
                    chats.forEach(o -> {
                        ArrayList<Object> objects = new ArrayList<>();
                        objects.add(o.getId());
                        objects.add(simpleDateFormat.format(o.getCreateTime()));
                        objects.add(o.getUserName());
                        objects.add(o.getContent());
                        data.add(objects);
                    });
                    response.getSuccess().setData(data);
                    response.getSuccess().setTitles(map.get(type).getTitle());
                    response.getSuccess().setButtons(map.get(type).getButtons());
                    return response;
                }
            }
        }
        records = recordDataService.getRecords(dbInputInfo);
        return ResultResponse.createSuccessForTypeAndRecords(null, type, records, map, util);
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
