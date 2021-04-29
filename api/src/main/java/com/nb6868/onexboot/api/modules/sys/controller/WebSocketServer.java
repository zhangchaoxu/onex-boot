package com.nb6868.onexboot.api.modules.sys.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocketServer
 * see {https://mp.weixin.qq.com/s/UE_iyHZ4CWmDzx9dr6m-pQ}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Log4j2
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {


    // 线程安全Set，存放每个客户端对应的MyWebSocket对象
    private final static CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();
    private final static Map<Long, Session> sessionPool = new HashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") Long sid) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(sid, session);
        log.debug("[websocket]" + sid + "接入,当前总数为:" + webSockets.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.debug("[websocket]" + "连接断开,当前总数为:" + webSockets.size());
    }

    /**
     * 收到客户端消息后调用的方法
     * @ Param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.debug("[websocket]" + "收到客户端消息:" + message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }


    /**
     * 发送广播消息
     * @param message
     */
    public void sendAllMessage(String message) {
        for (WebSocketServer webSocket : webSockets) {
            log.debug("[websocket]" + "广播消息:" + message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送单点消息
     * @param userId
     * @param message
     */
    public void sendOneMessage(Long userId, String message) {
        log.debug("[websocket]" + "单点消息:" + message);
        Session session = sessionPool.get(userId);
        if (session != null) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送多点消息
     * @param userIdList
     * @param message
     */
    public void sendMultiMessage(List<Long> userIdList, String message) {
        log.debug("[websocket]" + "单点消息:" + message);
        for (Long userId : userIdList) {
            Session session = sessionPool.get(userId);
            if (session != null) {
                try {
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
