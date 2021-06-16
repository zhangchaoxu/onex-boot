package com.nb6868.onexboot.api.modules.msg.push;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.fasterxml.jackson.core.type.TypeReference;
import com.nb6868.onexboot.api.modules.msg.MsgConst;
import com.nb6868.onexboot.api.modules.msg.entity.PushLogEntity;
import com.nb6868.onexboot.api.modules.msg.service.PushLogService;
import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.JacksonUtils;
import com.nb6868.onexboot.common.util.SpringContextUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

/**
 * JPushService 极光推送服务
 * see {https://docs.jiguang.cn/jpush/server/sdk/java_sdk/}
 * see {https://github.com/jpush/jpush-api-java-client}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Log4j2
public class JPushService extends AbstractPushService {

    @Override
    public void send(PushProps props, int pushType, String alias, String tags, String title, String content, String extras, Boolean apnsProd) {
        JPushClient jpushClient = new JPushClient(props.getMasterSecret(), props.getAppKey(), null, ClientConfig.getInstance());
        Map<String, String> extraMap = null;
        if (StrUtil.isNotEmpty(extras)) {
            extraMap = JacksonUtils.jsonToPojoByTypeReference(extras, new TypeReference<Map<String, String>>(){});
        }
        PushPayload payload = buildNotificationPushPayloadByAliases(pushType, StrUtil.split(alias, ","),  StrUtil.split(tags, ","), title, content, extraMap, apnsProd);

        // 保存记录
        PushLogService logService = SpringContextUtils.getBean(PushLogService.class);
        PushLogEntity pushLog = new PushLogEntity();
        // 最后发送结果
        Const.ResultEnum state = Const.ResultEnum.FAIL;
        try {
            PushResult result = jpushClient.sendPush(payload);
            pushLog.setResult(result.toString());
            state = result.statusCode == 0 ? Const.ResultEnum.SUCCESS : Const.ResultEnum.FAIL;
        } catch (APIConnectionException e) {
            log.error(e.getMessage());
            pushLog.setResult(e.toString());
        } catch (APIRequestException e) {
            // Should review the error, and fix the request
            log.error("Should review the error, and fix the request", e);
            log.info("HTTP Status: " + e.getStatus());
            log.info("Error Code: " + e.getErrorCode());
            log.info("Error Message: " + e.getErrorMessage());
            pushLog.setResult(e.toString());
        }

        pushLog.setType(pushType);
        pushLog.setAlias(alias);
        pushLog.setTags(tags);
        pushLog.setParams(extras);
        pushLog.setState(state.value());
        pushLog.setTitle(title);
        pushLog.setContent(content);
        logService.save(pushLog);
        if (state == Const.ResultEnum.FAIL) {
            throw new OnexException(ErrorCode.SEND_PUSH_ERROR);
        }
    }

    /**
     * 构建推送内容
     */
    private static PushPayload buildNotificationPushPayloadByAliases(int pushType, String[] aliases, String[] tags, String title, String alert, Map<String, String> extras, Boolean apnsProd) {
        Audience.Builder audienceBuilder = Audience.newBuilder();
        audienceBuilder.setAll(pushType == MsgConst.PushTypeEnum.ALL.value());
        if (pushType == MsgConst.PushTypeEnum.ALIAS.value()) {
            if (ObjectUtil.isNotEmpty(aliases)) {
                audienceBuilder.addAudienceTarget(AudienceTarget.alias(aliases));
            } else {
                throw new OnexException("aliases不能为空");
            }
        } else if (pushType == MsgConst.PushTypeEnum.TAGS.value()) {
            if (ObjectUtil.isNotEmpty(tags)) {
                audienceBuilder.addAudienceTarget(AudienceTarget.tag(tags));
            } else {
                throw new OnexException("tags不能为空");
            }
        } if (pushType == MsgConst.PushTypeEnum.ALIAS_AND_TAGS.value()) {
            if (ObjectUtil.isNotEmpty(aliases)) {
                audienceBuilder.addAudienceTarget(AudienceTarget.alias(aliases));
            }
            if (ObjectUtil.isNotEmpty(tags)) {
                audienceBuilder.addAudienceTarget(AudienceTarget.tag(tags));
            }
        }
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(audienceBuilder.build())
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(alert)
                                .addExtras(extras)
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setAlert(alert)
                                .setTitle(title)
                                .addExtras(extras)
                                .build())
                        .build())
                // 指定Apns生产环境
                .setOptions(Options.newBuilder()
                        .setApnsProduction(apnsProd)
                        .build())
                .build();
    }

}
