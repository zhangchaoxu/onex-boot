package com.nb6868.onexboot.api.common.websocket;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocketServer
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Log4j2
@Component
@ServerEndpoint("/webSocket/{userId}")
public class WebSocketServer {

    private Session session;

    private final static CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();
    private final static Map<Long, Session> sessionPool = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") Long userId) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(userId, session);
        log.debug("[websocket]" + userId + "接入,当前总数为:" + webSockets.size());
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.debug("[websocket]" + "连接断开,当前总数为:" + webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.debug("[websocket]" + "收到客户端消息:" + message);
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
