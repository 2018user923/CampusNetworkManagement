package com.example.demo.service;

import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.util.ResultResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface UserService {
    boolean loginHandler(User user, HttpServletRequest request);

    ResultResponse logOutHandler(HttpServletRequest request);

    boolean loginByEmailHandler(HttpServletRequest request, String email, String code, String userName);

    ModelAndView form(HttpServletRequest request);

    String userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount);

    String userInfoUpdateHandler(HttpServletRequest request, User user);

    User getUserInfoHandler(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request);

    ResultResponse getRecords(HttpServletRequest request, Integer page, Integer size);

    ResultResponse getRecords(HttpServletRequest request, Integer page, Integer size, Integer type);

    boolean userRegisterHandler(HttpServletRequest request, User user, String code);

    Set<Integer> getUserAuthorityListHandler(HttpServletRequest request);

    ResultResponse loginUserLoginHandler(HttpServletRequest request, @RequestBody User user);

    ResultResponse userRegister(HttpServletRequest request, User user, String coe);
}
