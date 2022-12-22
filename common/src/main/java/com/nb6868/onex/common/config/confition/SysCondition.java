package com.nb6868.onex.common.config.confition;

import cn.hutool.core.util.ClassUtil;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SysCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return ClassUtil.scanPackage("com.nb6868.onex.sys.controller").size() > 0
                || ClassUtil.scanPackage("com.nb6868.onex.websocket.controller").size() > 0
                || ClassUtil.scanPackage("com.nb6868.onex.job.controller").size() > 0;
    }
}
