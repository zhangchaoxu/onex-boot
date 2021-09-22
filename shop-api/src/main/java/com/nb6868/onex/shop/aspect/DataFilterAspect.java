package com.nb6868.onex.shop.aspect;

import cn.hutool.core.util.StrUtil;
import com.nb6868.onex.common.annotation.DataSqlScope;
import com.nb6868.onex.common.exception.ErrorCode;
import com.nb6868.onex.common.exception.OnexException;
import com.nb6868.onex.common.pojo.Const;
import com.nb6868.onex.common.validator.AssertUtils;
import com.nb6868.onex.shop.shiro.SecurityUser;
import com.nb6868.onex.shop.shiro.UserDetail;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

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

    @Pointcut("@annotation(com.nb6868.onex.common.annotation.DataSqlScope)")
    public void dataFilterCut() {

    }

    @SuppressWarnings("unchecked")
    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) {
        AssertUtils.isEmpty(point.getArgs(), ErrorCode.DATA_SCOPE_PARAMS_ERROR);

        Object params = point.getArgs()[0];
        if (params instanceof Map) {
            UserDetail user = SecurityUser.getUser();

            try {
                //否则进行数据过滤
                Map map = (Map) params;
                String sqlFilter = getSqlFilter(user, point);
                map.put(Const.SQL_FILTER, sqlFilter);
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
        DataSqlScope dataFilter = method.getAnnotation(DataSqlScope.class);
        if (dataFilter.creatorFilter() || dataFilter.deptFilter() || dataFilter.userFilter()) {
            // 有开启的过滤器
            // 获取表的别名
            String tableAlias = dataFilter.tableAlias();
            if (StrUtil.isNotBlank(tableAlias)) {
                tableAlias += ".";
            }
            StringBuilder sqlFilter = new StringBuilder();
            //查询条件前缀
            String prefix = dataFilter.prefix();
            if (StrUtil.isNotBlank(prefix)) {
                sqlFilter.append(" ").append(prefix);
            }
            sqlFilter.append(" (");
            // 过滤用户
            if (dataFilter.userFilter()) {
                sqlFilter.append(tableAlias).append(dataFilter.userId()).append("=").append(user.getId());
            }
            // 过滤创建者
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
