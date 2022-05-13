package com.nb6868.onex.sys.controller;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketServer
 * see https://mp.weixin.qq.com/s/UE_iyHZ4CWmDzx9dr6m-pQ
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketServer {

    @Value("${onex.websocket.max-idle-timeout:3600000}")
    private long maxIdleTimeout;

    // 线程安全Set，存放每个客户端对应的MyWebSocket对象
    private final static Map<String, WebSocketServer> webSockets = new ConcurrentHashMap<>();
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 当前连接session id
     */
    public List<String> getSidList() {
        List<String> sidList = new ArrayList<>();
        webSockets.forEach((sid, webSocketServer) -> sidList.add(sid));
        return sidList;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam(value = "sid") String sid, Session session) {
        session.setMaxIdleTimeout(maxIdleTimeout);
        this.session = session;
        webSockets.put(sid, this);
        log.info("[websocket], OnOpen, sid={}, total={}", sid, webSockets.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "sid") String sid) {
        webSockets.remove(sid);
        log.info("[websocket], OnClose, total={}", webSockets.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @ Param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(@PathParam(value = "sid") String sid, String message) {
        log.info("[websocket], OnMessage, sid={}, message={}", sid, message);
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
        webSockets.forEach((sid, webSocketServer) -> {
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
        try {
            webSockets.get(sid).session.getAsyncRemote().sendText(message);
            log.info("[websocket] sendOneMessage, sid={}, message={}, result={}", sid, message, "success");
            return true;
        } catch (Exception e) {
            log.error("[websocket] sendOneMessage, sid={}, message={}, result={}", sid, message, e.getMessage());
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
