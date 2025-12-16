package com.nb6868.onex.common.sse;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.exception.OnexException;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
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
    private final Map<String, SseEmitter> sseMap = new ConcurrentHashMap<>();
    /**
     * 保存每个连接的心跳任务，方便清理
     */
    private final Map<String, ScheduledFuture<?>> heartbeatsMap = new ConcurrentHashMap<>();
    /**
     * 单独的调度器（也可以复用你项目里的 TaskScheduler）
     */
    private final ScheduledExecutorService heartbeatsScheduler = Executors.newScheduledThreadPool(2);

    /**
     * 创建连接
     */
    public SseEmitter createSseConnect(@NotEmpty String sid) {
        // 设置超时时间,0表示不过期,默认30秒
        // 超过时间未完成会抛出AsyncRequestTimeoutException
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调
        sseEmitter.onCompletion(onSseCompletionCallback(sid));
        sseEmitter.onError(onSseErrorCallback(sid));
        sseEmitter.onTimeout(onSseTimeoutCallback(sid));
        sseMap.put(sid, sseEmitter);
        // 关键：心跳间隔 < 45s
        ScheduledFuture<?> heartbeatsScheduleFuture = heartbeatsScheduler.scheduleAtFixedRate(() -> {
            try {
                // comment 不会触发浏览器默认 onmessage
                sseEmitter.send(SseEmitter.event().comment("hb=>" + sid));
            } catch (Exception ex) {
                // 发送失败通常说明连接已断，触发清理
                onSseTimeoutCallback(sid).run();
            }
        }, 30, 30, TimeUnit.SECONDS);
        heartbeatsMap.put(sid, heartbeatsScheduleFuture);
        log.info("创建新的sse连接，当前id：{}", sid);
        try {
            // 连接成功后马上发一个心跳comment
            sseEmitter.send(SseEmitter.event().comment("hb=>" + sid));
        } catch (IOException e) {
            log.error("createSseConnect: 创建长链接异常，客户端ID:{}", sid, e);
            throw new OnexException("创建SSE连接异常！", e);
        }
        return sseEmitter;
    }

    /**
     * 按前缀关闭连接
     * @param sidPrefix
     */
    public void closeBySidPrefix(String sidPrefix) {
        if (CollectionUtil.isEmpty(sseMap)) {
            return;
        }
        sseMap.forEach((sid, sseEmitter) -> {
            if (StrUtil.startWithAnyIgnoreCase(sid, sidPrefix)) {
                removeClient(sid);
            }
        });
    }

    // 根据客户端id获取SseEmitter对象
    public SseEmitter getSseEmitterBySid(String sid) {
        return sseMap.get(sid);
    }

    /**
     * 通过sid前缀来发送消息
     */
    public void sendMessageBySidPrefix(String sidPrefix, String messageId, JSONObject messageBody) {
        if (CollectionUtil.isEmpty(sseMap)) {
            return;
        }
        sseMap.forEach((sid, sseEmitter) -> {
            if (StrUtil.startWithAnyIgnoreCase(sid, sidPrefix)) {
                sendMessageByClient(sid, messageId, messageBody, sseEmitter);
            }
        });
    }

    /**
     * 通过sid来发送消息
     */
    public void sendMessageBySid(String sid, String messageId, JSONObject messageBody) {
        if (CollectionUtil.isEmpty(sseMap)) {
            return;
        }
        SseEmitter sseEmitter = getSseEmitterBySid(sid);
        sendMessageByClient(sid, messageId, messageBody, sseEmitter);
    }

    /**
     * 通过消息给所有client
     */
    public void sendMessageToAll(String messageId, JSONObject messageBody) {
        if (CollectionUtil.isEmpty(sseMap)) {
            return;
        }
        for (Map.Entry<String, SseEmitter> entry : sseMap.entrySet()) {
            sendMessageByClient(entry.getKey(), messageId, messageBody, entry.getValue());
        }
    }

    /**
     * 推送消息到客户端
     * 此处做了推送失败后，重试推送机制，可根据自己业务进行修改
     **/
    public void sendMessageByClient(String sid, String messageId, JSONObject messageBody, SseEmitter sseEmitter) {
        if (sseEmitter == null) {
            log.error("sendMsgToClientBySid 推送消息失败：客户端{}未创建长链接,失败消息:{}", sid, messageBody.toString());
            return;
        }
        SseEmitter.SseEventBuilder sendData = SseEmitter.event().id(messageId).data(messageBody, MediaType.APPLICATION_JSON);
        try {
            sseEmitter.send(sendData);
        } catch (IOException e) {
            // 推送消息失败，记录错误日志，进行重推
            log.error("sendMsgToClient: 推送消息失败：{},尝试进行重推", messageBody, e);
            // 推送消息失败后，每隔10s推送一次，推送3次
            boolean sendSuccess = false;
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(10000);
                    sseEmitter = sseMap.get(sid);
                    if (sseEmitter == null) {
                        log.error("sendMsgToClient：{}的第{}次消息重推失败，未创建长链接", sid, i + 1);
                        continue;
                    }
                    sseEmitter.send(sendData);
                } catch (Exception ex) {
                    log.error("sendMsgToClient：{}的第{}次消息重推失败", sid, i + 1, ex);
                    continue;
                }
                log.info("sendMsgToClient：{}的第{}次消息重推成功,{}", sid, i + 1, messageBody);
                sendSuccess = true;
                break;
            }
            // 发送失败，就将sid做删除
            if (!sendSuccess) {
                removeClient(sid);
            }
        }
    }

    /**
     * 长链接完成后回调接口(即关闭连接时调用)
     **/
    private Runnable onSseCompletionCallback(String sid) {
        return () -> {
            log.info("结束连接：{}", sid);
            removeClient(sid);
        };
    }

    /**
     * 连接超时时调用
     **/
    private Runnable onSseTimeoutCallback(String sid) {
        return () -> {
            log.info("连接超时：{}", sid);
            removeClient(sid);
        };
    }

    /**
     * 推送消息异常时，回调方法
     **/
    private Consumer<Throwable> onSseErrorCallback(String sid) {
        return throwable -> {
            log.error("errorCallBack：连接异常,客户端ID:{}", sid, throwable);
            // 推送消息失败后，每隔10s推送一次，推送3次
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(10000);
                    SseEmitter sseEmitter = sseMap.get(sid);
                    if (sseEmitter == null) {
                        log.error("errorCallBack：第{}次消息重推失败,未获取到 {} 对应的长链接", i + 1, sid);
                        continue;
                    }
                    // sseEmitter.send("失败后重新推送");
                } catch (Exception e) {
                    log.error("errorCallBack: 错误回调，客户端ID:{}", sid, e);
                }
            }
        };
    }

    /**
     * 移除用户连接
     **/
    private void removeClient(String sid) {
        // 删除定时任务心跳
        ScheduledFuture<?> hb = heartbeatsMap.remove(sid);
        if (hb != null) {
            hb.cancel(true);
        }
        // 删除sse客户端
        SseEmitter emitter = sseMap.remove(sid);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception ignore) {
                // 忽略异常
            }
        }
        log.info("removeClient:{}", sid);
    }

}

