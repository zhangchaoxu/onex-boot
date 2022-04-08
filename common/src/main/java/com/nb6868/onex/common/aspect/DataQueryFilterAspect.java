package com.nb6868.onex.common.aspect;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.annotation.QueryDataScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.shiro.ShiroUtils;
import com.nb6868.onex.common.validator.AssertUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 查询数据过滤，切面处理类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Aspect
@Component
@ConditionalOnProperty(name = "onex.data-query-filter.enable", havingValue = "true")
@Order(150)
public class DataQueryFilterAspect {

    @Pointcut("@annotation(com.nb6868.onex.common.annotation.QueryDataScope)")
    public void dataFilterCut() {

    }

    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) {
        if (ObjectUtil.isEmpty(point.getArgs())) {
            return;
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method;
        try {
            method = point.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
        } catch (Exception e) {
            return;
        }
        QueryDataScope queryDataScope = method.getAnnotation(QueryDataScope.class);
        if (queryDataScope != null) {
            Object params = point.getArgs()[0];
            if (queryDataScope.tenantFilter()) {
                // 租户过滤
                String tenantCode = ShiroUtils.getUserTenantCode();
                AssertUtils.isEmpty(tenantCode, ErrorCode.TENANT_EMPTY);
                try {
                    ReflectUtil.invoke(params, "set" + StrUtil.upperFirst(queryDataScope.tenantCode()), tenantCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (queryDataScope.userFilter()) {
                // 用户过滤
                Long userId = ShiroUtils.getUserId();
                AssertUtils.isNull(userId, ErrorCode.ACCOUNT_NOT_EXIST);
                try {
                    ReflectUtil.invoke(params, "set" + StrUtil.upperFirst(queryDataScope.userId()), userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (queryDataScope.areaFilter()) {
                // 区域过滤
                String areaCode = ShiroUtils.getUserAreaCode();
                if (StrUtil.isNotBlank(areaCode)) {
                    try {
                        ReflectUtil.invoke(params, "set" + StrUtil.upperFirst(queryDataScope.areaCode()), areaCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (queryDataScope.deptFilter()) {
                // 组织过滤
                String deptCode = ShiroUtils.getUserDeptCode();
                if (StrUtil.isNotBlank(deptCode)) {
                    try {
                        ReflectUtil.invoke(params, "set" + StrUtil.upperFirst(queryDataScope.deptCode()), deptCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
