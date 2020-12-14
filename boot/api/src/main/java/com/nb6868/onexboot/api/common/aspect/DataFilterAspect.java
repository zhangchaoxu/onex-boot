package com.nb6868.onexboot.api.common.aspect;

import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.Const;
import com.nb6868.onexboot.common.util.StringUtils;
import com.nb6868.onexboot.api.common.annotation.DataFilter;
import com.nb6868.onexboot.api.common.interceptor.DataScope;
import com.nb6868.onexboot.api.modules.uc.UcConst;
import com.nb6868.onexboot.api.modules.uc.user.SecurityUser;
import com.nb6868.onexboot.api.modules.uc.user.UserDetail;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 数据过滤，切面处理类
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Aspect
@Component
public class DataFilterAspect {

    @Pointcut("@annotation(com.nb6868.onexboot.common.annotation.DataFilter)")
    public void dataFilterCut() {

    }

    @SuppressWarnings("unchecked")
    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) {
        if (ObjectUtils.isEmpty(point.getArgs())) {
            throw new OnexException(ErrorCode.DATA_SCOPE_PARAMS_ERROR);
        }
        Object params = point.getArgs()[0];
        if (params instanceof Map) {
            UserDetail user = SecurityUser.getUser();

            // 如果是超级管理员，则不进行数据过滤
            if (user.getType() <= UcConst.UserTypeEnum.SYSADMIN.value()) {
                return;
            }

            try {
                //否则进行数据过滤
                Map map = (Map) params;
                String sqlFilter = getSqlFilter(user, point);
                map.put(Const.SQL_FILTER, new DataScope(sqlFilter));
            } catch (Exception e) {

            }

            return;
        }

        throw new OnexException(ErrorCode.DATA_SCOPE_PARAMS_ERROR);
    }

    /**
     * 获取数据过滤的SQL
     */
    private String getSqlFilter(UserDetail user, JoinPoint point) throws Exception {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = point.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
        DataFilter dataFilter = method.getAnnotation(DataFilter.class);
        if (dataFilter.creatorFilter() || dataFilter.tenantFilter() || dataFilter.deptFilter() || dataFilter.userFilter()) {
            // 有开启的过滤器
            // 获取表的别名
            String tableAlias = dataFilter.tableAlias();
            if (StringUtils.isNotBlank(tableAlias)) {
                tableAlias += ".";
            }

            StringBuilder sqlFilter = new StringBuilder();

            //查询条件前缀
            String prefix = dataFilter.prefix();
            if (StringUtils.isNotBlank(prefix)) {
                sqlFilter.append(" ").append(prefix);
            }

            sqlFilter.append(" (");
            // 过滤租户
            if (dataFilter.tenantFilter() && !ObjectUtils.isEmpty(user.getTenantId())) {
                sqlFilter.append(tableAlias).append(dataFilter.tenantId()).append("=").append(user.getTenantId());
            }
            // 过滤用户
            if (dataFilter.userFilter()) {
                sqlFilter.append(tableAlias).append(dataFilter.userId()).append("=").append(user.getId());
            }
            // 过滤用户
            if (dataFilter.creatorFilter()) {
                sqlFilter.append(tableAlias).append(dataFilter.creatorId()).append("=").append(user.getId());
            }

            sqlFilter.append(")");

            return sqlFilter.toString();
        } else {
            return null;
        }
    }
}
