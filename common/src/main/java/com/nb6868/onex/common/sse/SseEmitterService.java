package com.nb6868.onex.common.sse;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.OnexException;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Sse Emitter
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
@Slf4j
public class SseEmitterService {

    /**
     * 容器，保存连接，用于输出返回
     */
    private static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

    /**
     * 创建连接
     */
    public SseEmitter createSseConnect(@NotEmpty String sid) {
        // 设置超时时间，0表示不过期。默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(sid));
        sseCache.put(sid, sseEmitter);
        log.info("创建新的sse连接，当前id：{}", sid);
        try {
            sseEmitter.send(SseEmitter.event().id("SID").data(sid));
        } catch (IOException e) {
            log.error("createSseConnect: 创建长链接异常，客户端ID:{}", sid, e);
            throw new OnexException("创建连接异常！", e);
        }
        return sseEmitter;
    }

    public void closeSseConnect(String sid) {
        SseEmitter sseEmitter = sseCache.get(sid);
        if (sseEmitter != null) {
            sseEmitter.complete();
            removeUser(sid);
        }
    }

    // 根据客户端id获取SseEmitter对象
    public SseEmitter getSseEmitterBySid(String sid) {
        return sseCache.get(sid);
    }

    /**
     * 发送单条消息
     */
    public void sendOneMessage(String sid, JSONObject msgBody) {
        if (CollectionUtil.isEmpty(sseCache)) {
            return;
        }
        SseEmitter sseEmitter = getSseEmitterBySid(sid);
        sendMsgToClientBySid(sid, msgBody, sseEmitter);
    }

    // 推送消息到客户端，此处结合业务代码，业务中需要推送消息处调用即可向客户端主动推送消息
    public void sendMsgToClient(JSONObject msgBody) {
        if (CollectionUtil.isEmpty(sseCache)) {
            return;
        }
        for (Map.Entry<String, SseEmitter> entry : sseCache.entrySet()) {
            sendMsgToClientBySid(entry.getKey(), msgBody, entry.getValue());
        }
    }

    /**
     * 推送消息到客户端
     * 此处做了推送失败后，重试推送机制，可根据自己业务进行修改
     **/
    public void sendMsgToClientBySid(String sid, JSONObject msgBody, SseEmitter sseEmitter) {
        if (sseEmitter == null) {
            log.error("sendMsgToClientBySid 推送消息失败：客户端{}未创建长链接,失败消息:{}", sid, msgBody.toString());
            return;
        }

        SseEmitter.SseEventBuilder sendData = SseEmitter.event().id("TASK_RESULT").data(msgBody, MediaType.APPLICATION_JSON);
        try {
            sseEmitter.send(sendData);
        } catch (IOException e) {
            // 推送消息失败，记录错误日志，进行重推
            log.error("sendMsgToClient: 推送消息失败：{},尝试进行重推", msgBody.toString(), e);
            boolean isSuccess = true;
            // 推送消息失败后，每隔10s推送一次，推送5次
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(10000);
                    sseEmitter = sseCache.get(sid);
                    if (sseEmitter == null) {
                        log.error("sendMsgToClient：{}的第{}次消息重推失败，未创建长链接", sid, i + 1);
                        continue;
                    }
                    sseEmitter.send(sendData);
                } catch (Exception ex) {
                    log.error("sendMsgToClient：{}的第{}次消息重推失败", sid, i + 1, ex);
                    continue;
                }
                log.info("sendMsgToClient：{}的第{}次消息重推成功,{}", sid, i + 1, msgBody);
                return;
            }
        }
    }

    /**
     * 长链接完成后回调接口(即关闭连接时调用)
     **/
    private Runnable completionCallBack(String sid) {
        return () -> {
            log.info("结束连接：{}", sid);
            removeUser(sid);
        };
    }

    /**
     * 连接超时时调用
     **/
    private Runnable timeoutCallBack(String sid) {
        return () -> {
            log.info("连接超时：{}", sid);
            removeUser(sid);
        };
    }

    /**
     * 推送消息异常时，回调方法
     **/
    private Consumer<Throwable> errorCallBack(String sid) {
        return throwable -> {
            log.error("errorCallBack：连接异常,客户端ID:{}", sid);

            // 推送消息失败后，每隔10s推送一次，推送5次
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(10000);
                    SseEmitter sseEmitter = sseCache.get(sid);
                    if (sseEmitter == null) {
                        log.error("errorCallBack：第{}次消息重推失败,未获取到 {} 对应的长链接", i + 1, sid);
                        continue;
                    }
                    sseEmitter.send("失败后重新推送");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * 移除用户连接
     **/
    private void removeUser(String sid) {
        sseCache.remove(sid);
        log.info("removeUser:移除用户：{}", sid);
    }

}
