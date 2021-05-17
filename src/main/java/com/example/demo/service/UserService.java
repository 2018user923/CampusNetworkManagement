package com.example.demo.service;

import com.example.demo.domain.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService {
    boolean login(User user, HttpServletRequest request);

    boolean logOut(HttpServletRequest request);
}
