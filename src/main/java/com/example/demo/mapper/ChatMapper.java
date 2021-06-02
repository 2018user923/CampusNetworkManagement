package com.example.demo.mapper;

import com.example.demo.domain.Chat;
import com.example.demo.util.DBInputInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {
    /**
     * 描述: 插入聊天信息
     *
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/2 10:35
     * @param: chat
     */
    int insert(Chat chat);

    List<Chat> getChatByTime(DBInputInfo dbInputInfo);
}
