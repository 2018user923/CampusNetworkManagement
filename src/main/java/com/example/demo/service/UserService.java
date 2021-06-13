package com.example.demo.service;

import com.example.demo.domain.Chat;
import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.util.DBInputInfo;
import com.example.demo.util.ResultResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface UserService {
    ResultResponse logOutHandler(HttpServletRequest request);

    ResultResponse loginByEmailHandler(HttpServletRequest request, String email, String code, String userName);

    ResultResponse userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount);

    ResultResponse userInfoUpdateHandler(HttpServletRequest request, User user, MultipartFile file);

    User getUserInfoHandler(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request);

    ResultResponse getRecords(HttpServletRequest request, Integer start, Integer limit);

    ResultResponse getRecords(HttpServletRequest request, Integer start, Integer limit, Integer type);

    Set<Integer> getUserAuthorityListHandler(HttpServletRequest request);

    ResultResponse loginUserHandler(HttpServletRequest request, @RequestBody User user);

    ResultResponse userRegisterHandler(HttpServletRequest request, User user, String coe);

    ResultResponse getUserInfoByTypeHandler(HttpServletRequest request,Integer type);

    ResultResponse addBlackListHandler(HttpServletRequest request, Integer id);

    ResultResponse rmBlackListHandler(HttpServletRequest request, Integer id);

    ResultResponse userSendMessageHandler(HttpServletRequest request, Chat chat);

    ResultResponse getMessageHandler(HttpServletRequest request, DBInputInfo dbInputInfo);

    void clearOtherUser(HttpServletRequest request, String userName);

    ResultResponse saveAnnouncement(HttpServletRequest request, Chat chat);

    ResultResponse getBillingMethodsHandler(HttpServletRequest request);
}
