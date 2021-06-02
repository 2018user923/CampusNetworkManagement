package com.example.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述: 聊天信息记录
 *
 * @Author: <247702560@qq.com>
 * @Date: 2021/6/2 9:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    private Integer id;
    private String content;
    private Integer userId;
    private Date createTime;
    private String avatar;
    private String userName;

    public static List<Object> createResponseData(Chat chat, SimpleDateFormat simpleDateFormat) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(chat.getId());
        list.add(chat.getUserId());
        list.add(chat.getUserName());
        list.add(chat.getAvatar());
        String time = simpleDateFormat.format(chat.getCreateTime());
        list.add(chat.getUserName() + "," + time);
        list.add(chat.getContent());
        return list;
    }
}
