package com.example.demo.service;

import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import com.example.demo.util.ResultResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {
    boolean loginHandler(User user, HttpServletRequest request);

    boolean logOutHandler(HttpServletRequest request);

    boolean loginByEmailHandler(HttpServletRequest request, String email, String code);

    ModelAndView form(HttpServletRequest request);

    String userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount);

    String userInfoUpdateHandler(HttpServletRequest request, User user);

    User getUserInfoHandler(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request, Integer page, Integer size);

    List<Record> getRecords(HttpServletRequest request, Integer page, Integer size, Integer type);

    boolean userRegisterHandler(HttpServletRequest request, User user, String code);

    Set<Integer> getUserAuthorityListHandler(HttpServletRequest request);

    ResultResponse loginUserLoginHandler(HttpServletRequest request, @RequestBody User user);

    ResultResponse userRegister(HttpServletRequest request, User user, String coe);
}
