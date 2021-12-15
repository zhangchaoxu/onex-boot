package com.nb6868.onex.common.websocket;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
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
@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    // 线程安全Set，存放每个客户端对应的MyWebSocket对象
    private final static CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();
    private final static Map<String, Session> sessionPool = new HashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 当前连接session id
     */
    public List<String> getSidList() {
        List<String> sidList = new ArrayList<>();
        sessionPool.forEach((s, session) -> sidList.add(s));
        return sidList;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "sid") String sid) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(sid, session);
        log.info("[websocket], OnOpen, sid={}, total={}", sid, webSockets.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.info("[websocket], OnClose, total={}", webSockets.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @ Param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("[websocket], OnMessage, message={}", message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[websocket], OnError", error);
    }

    /**
     * 发送广播消息
     */
    public void sendAllMessage(String message) {
        log.info("[websocket] sendAllMessage, message={}", message);
        webSockets.forEach(webSocketServer -> {
            try {
                webSocketServer.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 发送单点消息
     */
    public boolean sendOneMessage(String sid, String message) {
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(message);
                log.info("[websocket] sendOneMessage, sid={}, message={}, result={}", sid, message, "success");
                return true;
            } catch (Exception e) {
                log.error("[websocket] sendOneMessage, sid={}, message={}, result={}", sid, message, e.getMessage());
                return false;
            }
        } else {
            log.info("[websocket] sendOneMessage, sid={}, message={}, result={}", sid, message, "sid不存在或已关闭");
            return false;
        }
    }

    /**
     * 发送多点消息
     */
    public void sendMultiMessage(List<String> sidList, String message) {
        log.info("[websocket] sendMultiMessage, sidList={}, message={}", CollUtil.join(sidList, ","), message);
        sidList.forEach(sid -> sendOneMessage(sid, message));
    }

}
