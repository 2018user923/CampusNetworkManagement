package com.example.demo.service;

import com.example.demo.domain.Record;
import com.example.demo.domain.User;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {
    boolean login(User user, HttpServletRequest request);

    boolean logOut(HttpServletRequest request);

    ModelAndView form(HttpServletRequest request);

    String userRechargeAppHandler(HttpServletRequest request, Integer rechargeAmount);

    String userInfoUpdateHandler(HttpServletRequest request, User user);

    User getUserInfoHandler(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request);

    List<Record> getRecords(HttpServletRequest request, Integer page, Integer size);


}
