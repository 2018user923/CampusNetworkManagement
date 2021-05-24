package com.example.demo.service;

import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface UserService {
    boolean loginHandler(User user, HttpServletRequest request);

    boolean logOutHandler(HttpServletRequest request);

    ModelAndView form(HttpServletRequest request);

    String userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount);

    String userInfoUpdateHandler(HttpServletRequest request, User user);

    User getUserInfoHandler(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request, Integer page, Integer size);

    boolean userRegisterHandler(HttpServletRequest request, User user, String code);

    Set<Integer> getUserAuthorityListHandler(HttpServletRequest request);
}
