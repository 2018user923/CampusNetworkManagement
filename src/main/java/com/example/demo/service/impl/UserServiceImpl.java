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
import com.example.demo.service.UserService;
import com.example.demo.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    /*用户持久层服务*/
    @Resource
    private UserMapper userDataService;

    /*消费记录服务*/
    @Resource
    private RecordMapper recordService;

    @Resource
    private ChatMapper chatService;

    /*redis 缓存*/
    @Resource
    private RedisUtil cache;

    /*工具类*/
    @Resource
    private MyUtil myUtil;

    @Autowired
    private HttpService httpService;

    @Value("${user.newUserAuthority}")
    private String newUserAuthority;

    @Value("${user.images.path}")
    private String baseImageUrl;

    @Value("${user.images.default}")
    private String defaultImage;

    @Autowired
    private SimpleDateFormat simpleDateFormat;


    @Autowired
    private Map<Integer, MappingTitleAndButtons> map;

    /**
     * 用户登录服务
     */
    @Override
    public boolean loginHandler(User user, HttpServletRequest request) {
        clearOtherUser(request);
        String userName = user.getUserName(), passWord = user.getPassWord();
        //todo 中级步骤需要校验是否登录成功,待编写。
        user = userDataService.getUserByUserName(userName);
        //todo 用户名或密码不正确,后续需要区分是用户名不存在还是密码不正确
        if (user == null || !user.getPassWord().equals(passWord)) {
            return false;
        }
        user.setAuthorityToSet(JSON.parseObject(user.getAuthority(), new TypeReference<>() {
        }));
        //将ip 地址作为主键，将流量数据存入缓存,
        String ipAddress = httpService.getIpAddress(request);
        JSONObject netInfo = myUtil.getNetInfo();
        netInfo.put("signIn", new Date());
        netInfo.put("userName", userName);
        return cache.hset(EncryptionKey.netData, ipAddress, netInfo) && cache.hset(EncryptionKey.userLoginInfo, ipAddress, user);
    }

    /**
     * 用户退出服务
     */
    @Override
    public ResultResponse logOutHandler(HttpServletRequest request) {
        ResultResponse response = null;
        try {
            String ipAddress = httpService.getIpAddress(request);
            JSONObject json = (JSONObject) cache.hget(EncryptionKey.netData, ipAddress);
            User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
            user.setAuthority(JSON.toJSONString(user.getAuthorityToSet()));
            JSONObject curNetInfo = myUtil.getNetInfo();
            BigDecimal curData = curNetInfo.getBigDecimal("getData");
            //之前的流量
            BigDecimal preNetData = json.getBigDecimal("getData");
            //花费的流量
            BigDecimal costData = curData.subtract(preNetData);

            Date signIn = json.getDate("signIn");
            Date signOut = new Date();

            BigDecimal costMoney = myUtil.calcSpend(signIn, signOut);
            BigDecimal curBalance = user.getBalance().subtract(costMoney);
            user.setBalance(curBalance);

            Record record = Record.builder()
                    .userName(user.getUserName())
                    .signIn(signIn)
                    .signOut(signOut)
                    .costData(costData)
                    .type(TypeEnum.userExpenses.getVal())
                    //这里的金额需要除以 1000
                    .costMoney(costMoney)
                    .balance(curBalance)
                    .build();

            userDataService.updateUser(user);
            recordService.insertRecord(record);

            //移除该用户的信息
            cache.hdel(EncryptionKey.netData, ipAddress);
            cache.hdel(EncryptionKey.userLoginInfo, ipAddress);
            response = ResultResponse.createSimpleSuccess("http://localhost:8080/", null);
        } catch (Exception e) {
            response = ResultResponse.createError(-1, "退出失败！");
        }
        return response;
    }

    @Override
    public ResultResponse loginByEmailHandler(HttpServletRequest request, String email, String code, String userName) {
        clearOtherUser(request);
        User user = userDataService.getUserByUserNameAndEmail(userName, email);
        //数据库中没有查询到这个用户
        if (user == null) {
            return ResultResponse.createError(-200, "用戶名不存在！");
        }

        //校验邮箱验证码
        String ipAddress = httpService.getIpAddress(request);
        String emailCode = (String) cache.hget(EncryptionKey.loginEmail, ipAddress);

        //验证码不正确
        if (emailCode == null || !emailCode.equals(code)) {
            return ResultResponse.createError(-200, "邮箱验证码不正确！");
        }

        //注册用户登录
        if (loginHandler(user, request)) {
            return ResultResponse.createSimpleSuccess("http://localhost:8080/form", null);
        }
        return null;
    }

    /**
     * @param request 用户请求的 form
     */
    @Override
    public ModelAndView form(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/form");
        return modelAndView;
    }

    /**
     * 用户充值处理程序
     */
    @Override
    public ResultResponse userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount) {
        String ipAddress = httpService.getIpAddress(request);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);

        Record record = Record.builder()
                .userName(user.getUserName())
                .createTime(new Date())
                .type(TypeEnum.userRechargeSubmit.getVal())
                .rechargeAmount(new BigDecimal(rechargeAmount))
                .build();
        int primaryKey = recordService.insertRecord(record);
        return ResultResponse.createSuccessForTypeAndRecords(null, 1, Collections.singletonList(record), map, myUtil);
    }

    @Override
    public ResultResponse userInfoUpdateHandler(HttpServletRequest request, User user, MultipartFile file) {
        if (file != null) {
            try {
                log.info("[文件类型] - [{}]", file.getContentType());
                log.info("[文件名称] - [{}]", file.getOriginalFilename());
                log.info("[文件大小] - [{}]", file.getSize());
                String suffix = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".") + 1);
                String avatar = user.getUserName() + "_" + UUID.randomUUID() + "." + suffix;
                user.setAvatar(baseImageUrl + File.separator + avatar);
                String uploadDirPath = request.getServletContext().getRealPath("./") + baseImageUrl;
                String uploadImagePath = uploadDirPath + File.separator + avatar;
                file.transferTo(new File(uploadImagePath));
            } catch (Exception e) {
                log.info("文件写入本地异常！", e);
            }
        }
        if (user.getBalance() == null) {
            user.setBalance(new BigDecimal(0));
        }
        userDataService.updateUser(user);
        String ipAddress = httpService.getIpAddress(request);
        int primaryKey = ((User) cache.hget(EncryptionKey.userLoginInfo, ipAddress)).getId();
        user = userDataService.getUserById(primaryKey);
        cache.hset(EncryptionKey.userLoginInfo, ipAddress, user);
        request.getSession().setAttribute("user", user);
        return ResultResponse.createSimpleSuccess(null, null);
    }

    @Override
    public User getUserInfoHandler(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);
        return (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
    }

    @Override
    public List<Record> getRecords(HttpServletRequest request) {
        User user = getUserInfoHandler(request);
        DBInputInfo inputInfo = DBInputInfo.builder().userName(user.getUserName()).build();
        return recordService.getRecords(inputInfo);
    }

    @Override
    public ResultResponse getRecords(HttpServletRequest request, Integer page, Integer size) {
        User user = getUserInfoHandler(request);

        List<Record> records = recordService.getRecordsByUserNameForPages(user.getUserName(), (page - 1) * size, size, TypeEnum.userExpenses.getVal());

        if (records == null) {
            return ResultResponse.createError(-1, "查询数据库异常！");
        }

        ResultResponse response = new ResultResponse();
        response.setCode(200);

        ResultResponse.Success success = new ResultResponse.Success();
        success.setData(records);

        response.setSuccess(success);
        return response;
    }

    @Override
    public ResultResponse getRecords(HttpServletRequest request, Integer start, Integer limit, Integer type) {
        User user = getUserInfoHandler(request);

        List<Record> records = null;

        switch (type) {
            case 0, 1, 2, 3, 4 -> records = recordService.getRecordsByUserNameForPages(user.getUserName(), (start - 1) * limit, limit, type);
            case 5 -> records = recordService.getRecordsByUserNameForPages(null, (start - 1) * limit, limit, 4);
            case 6 -> records = recordService.getRecordsByUserNameForPages(null, (start - 1) * limit, limit, 1);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }

        if (records == null) {
            return ResultResponse.createError(-1, "查询数据库异常！");
        }

        return ResultResponse.createSuccessForTypeAndRecords(null, type, records, map, myUtil);
    }

    /**
     * 得到用户权限
     */
    @Override
    public Set<Integer> getUserAuthorityListHandler(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);
        User user = (User) cache.hget(EncryptionKey.userLoginInfo, ipAddress);
        return user.getAuthorityToSet();
    }

    @Override
    public ResultResponse loginUserLoginHandler(HttpServletRequest request, User user) {
        clearOtherUser(request);
        ResultResponse res = new ResultResponse();

        String userName = user.getUserName(), passWord = user.getPassWord();

        //todo 中级步骤需要校验是否登录成功,待编写。
        user = userDataService.getUserByUserName(userName);

        if (user == null) {
            return ResultResponse.createError(-1, "用户名不存在!");
        } else if (!user.getPassWord().equals(passWord)) {
            return ResultResponse.createError(-1, "密码错误!");
        }
        //将用户的权限转化为 set 集合类型
        user.setAuthorityToSet(JSON.parseObject(user.getAuthority(), new TypeReference<>() {
        }));

        //将ip 地址作为key，将流量数据及其对象信息存入缓存,
        String ipAddress = httpService.getIpAddress(request);
        JSONObject netInfo = myUtil.getNetInfo();
        netInfo.put("signIn", new Date());
        netInfo.put("userName", user.getUserName());
        boolean save = cache.hset(EncryptionKey.netData, ipAddress, netInfo) && cache.hset(EncryptionKey.userLoginInfo, ipAddress, user);
        if (!save) {
            ResultResponse.createError(-1, "内部错误，数据存入缓存异常!");
        } else {
            //登录成功
            res.setCode(200);
            ResultResponse.Success success = new ResultResponse.Success();

            //设置成功之后的跳转页面
            success.setUrl("http://localhost:8080/form");

            res.setSuccess(success);
        }
        log.info("UserServiceImpl#loginUserLoginHandler:用户成功登录之后的信息：{}", JSON.toJSON(user));
        return res;
    }

    /**
     * 描述: 用户注册处理器
     *
     * @return ResultResponse
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 12:15
     * @param: request 请求
     * @param: user 信息
     * @param: code 注册发送而来的邮箱验证码
     */
    @Override
    public ResultResponse userRegisterHandler(HttpServletRequest request, User user, String code) {
        //用户名已经存在，这里后续可以优化
        if (userDataService.getUserByUserName(user.getUserName()) != null) {
            return ResultResponse.createError(-1, "用户名已经存在！");
        }

        //校验邮箱验证码
        String ipAddress = httpService.getIpAddress(request);
        String emailCode = (String) cache.hget(EncryptionKey.registerEmail, ipAddress);
        //验证码不正确
        if (emailCode == null || !emailCode.equals(code)) {
            return ResultResponse.createError(-1, "邮箱验证码已过期！");
        }

        //为该用户添加权限
        user.setAuthority(newUserAuthority);
        user.setBalance(new BigDecimal(0));
        //设置默认的头像
        user.setAvatar(baseImageUrl + File.separator + defaultImage);
        //普通用户设置为 1,管理员为 0
        user.setType(UserType.normalUser.getVal());
        //注册用户成功过，插入数据。
        int primaryKey = userDataService.insertUser(user);
        user.setId(primaryKey);

        log.info("UserServiceImpl#userRegisterHandler:用户注册成功，userInfo:{}", JSON.toJSONString(user));
        //返回登录处理结果。
        return loginUserLoginHandler(request, user);
    }

    /**
     * 描述:
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/1 14:44
     * @param: request
     */
    @Override
    public ResultResponse getUserInfoByTypeHandler(HttpServletRequest request, Integer type) {
        UserType userType = UserType.getKey(type);
        if (userType == UserType.notFind) {
            return ResultResponse.createError(-1, "该类型不存在！");
        } else {
            List<User> user = userDataService.getUserByType(type);
            return ResultResponse.createSimpleSuccess(null, user);
        }
    }

    @Override
    public ResultResponse addBlackListHandler(HttpServletRequest request, Integer id) {
        User user = User.builder()
                .id(id)
                .type(UserType.blacklistUser.getVal())
                .build();
        userDataService.updateUserById(user);
        return ResultResponse.createSimpleSuccess(null, null);
    }

    @Override
    public ResultResponse rmBlackListHandler(HttpServletRequest request, Integer id) {
        User user = User.builder()
                .id(id)
                .type(UserType.normalUser.getVal())
                .build();
        userDataService.updateUserById(user);
        return ResultResponse.createSimpleSuccess(null, null);
    }

    @Override
    public ResultResponse userSendMessageHandler(HttpServletRequest request, Chat chat) {
        chatService.insert(chat);
        chat.setCreateTime(new Date());
        return ResultResponse.createSimpleSuccess(null, Chat.createResponseData(chat, simpleDateFormat));
    }

    @Override
    public ResultResponse getMessageHandler(HttpServletRequest request, DBInputInfo dbInputInfo) {
        List<Chat> chats = chatService.getChats(dbInputInfo);
        List<List<Object>> res = new ArrayList<>(chats.size());
        chats.forEach(chat -> {
            res.add(Chat.createResponseData(chat, simpleDateFormat));
        });
        return ResultResponse.createSimpleSuccess(null, res);
    }

    /*
     * 描述:  如果该ip地址存在其他用户登录，那么使其下线。
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/10 11:21
     * @param: request
     */
    @Override
    public void clearOtherUser(HttpServletRequest request) {
        String ipAddress = httpService.getIpAddress(request);
        Object user = cache.hget(EncryptionKey.userLoginInfo, ipAddress);
        if (user != null) {
            logOutHandler(request);
        }
    }

    /**
     * 描述: 存储管理员发送的系统公告。
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/10 19:49
     * @param: request
     * @param: chat
     */
    @Override
    public ResultResponse saveAnnouncement(HttpServletRequest request, Chat chat) {
        chatService.insert(chat);

        DBInputInfo build = DBInputInfo.builder().id(chat.getId()).build();
        List<Chat> chats = chatService.getChats(build);
        if (chats == null || chats.isEmpty()) {
            return ResultResponse.createError(-200, "发送信息失败，插入数据库失败！");
        }
        return ResultResponse.createSimpleSuccess(null, Chat.createResponseData(chats.get(0), simpleDateFormat));
    }
}
