package com.example.demo.ws;

import com.alibaba.fastjson.JSON;
import com.example.demo.domain.Chat;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.util.ResultResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * webSocket
 *
 * @author <247702560@qq.com>
 * @since 2021/6/8 15:06
 */
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfiguration.class)
@Component
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatEndPoint {
    //用来存储每一个客户都对应的 ChatEndPoint 对象
    private static Map<String, ChatEndPoint> onlineUsers = new ConcurrentHashMap<>();

    static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private Session session;
    private HttpSession httpSession;

    @OnOpen
    /**
     * 描述: WebSocket 连接建立的时候调用
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/8 15:09
     * @param: session
     * @param: config
     */
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        User user = (User) httpSession.getAttribute("user");
        onlineUsers.put(user.getUserName(), this);

        //推送一条系统消息
        Chat chat = Chat.builder()
                .type(1)
                .userName(user.getUserName())
                .avatar(user.getAvatar())
                .content(user.getUserName() + "已经上线！")
                .createTime(new Date())
                .build();
        ResultResponse response = ResultResponse.createSimpleSuccess(null, Chat.createResponseData(chat, null));
        broadcastAllUsers(response);
    }

    /**
     * 描述: 将消息推送给所有的客户
     *
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/8 15:43
     * @param: message
     */
    private void broadcastAllUsers(Object message) {
        for (ChatEndPoint chatEndPoint : onlineUsers.values()) {
            try {
                chatEndPoint.session.getBasicRemote().sendText(JSON.toJSONString(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @OnMessage
    /**
     * 描述:  接收到客户都发送的数据时被调用
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/8 15:09
     * @param: message
     * @param: session
     */
    public void onMessage(String message, Session session) {
        log.info("ChatEndPoint#onMessage message{}", message);
        Chat chat = JSON.parseObject(message, Chat.class);
        ResultResponse response = userService.userSendMessageHandler(null, chat);
        log.info("ChatEndPoint#onMessage response:{}", JSON.toJSONString(response));
        broadcastAllUsers(response);
    }

    @OnClose
    /**
     * 描述: 连接关闭时被调用。
     *
     * @return
     * @Author: <247702560@qq.com>
     * @Date: 2021/6/8 15:09
     * @param: session
     */
    public void onClose(Session session) {
        User user = (User) httpSession.getAttribute("user");
        onlineUsers.remove(user.getUserName());
    }
}
