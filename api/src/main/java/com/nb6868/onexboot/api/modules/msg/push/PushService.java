package com.nb6868.onexboot.api.modules.msg.push;

import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.api.modules.sys.service.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 推送服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Service
public class PushService {

    @Autowired
    ParamService paramsService;

    public void send(int pushType, String alias, String tags, String title, String content, String extras, Boolean apnsProd) {
        PushProps config = paramsService.getContentObject(Const.PUSH_CONFIG_KEY, PushProps.class);
        if (config == null) {
            throw new OnexException("未找到对应的推送配置");
        }
        send(config, pushType, alias, tags, title, content, extras, apnsProd);
    }

    public void send(PushProps config, int pushType, String alias, String tags, String title, String content, String extras, Boolean apnsProd) {
        // 获取推送服务
        AbstractPushService service = PushFactory.build(config);
        // 发送推送
        service.send(config, pushType, alias, tags, title, content, extras, apnsProd);
    }
}
