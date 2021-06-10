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
    private Integer type;//按照类型来创建消息 0 是正常发送的消息，1是用户上线消息、2是管理员发送的公告


    /*
    0: chatId
    1: userId
    2: userName
    3: avatar
    4: "userName-userName
    5: content
    6: type
     */

    public static List<Object> createResponseData(Chat chat, SimpleDateFormat simpleDateFormat) {
        int type = chat.getType();

//        switch (type) {
//            case 0:
//                break;
//            case 1:
//                break;
//            case 2:
//                break;
//        }

        ArrayList<Object> list = new ArrayList<>();
        list.add(chat.getId());
        list.add(chat.getUserId());
        list.add(chat.getUserName());
        list.add(chat.getAvatar());
        String time = null;
        if (simpleDateFormat != null) {
            time = simpleDateFormat.format(chat.getCreateTime());
        }
        list.add(chat.getUserName() + "-" + time);
        list.add(chat.getContent());
        list.add(chat.getType());
        return list;
    }
}
