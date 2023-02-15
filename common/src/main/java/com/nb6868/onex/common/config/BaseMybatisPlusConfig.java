package com.nb6868.onex.common.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.nb6868.onex.common.jpa.injector.MySqlInjector;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.context.annotation.Bean;

/**
 * mybatis-plus 基础 配置
 * see {https://baomidou.com/guide/interceptor.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class BaseMybatisPlusConfig {

    /**
     * sql注入器
     *
     * @return inject
     */
    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }

    /**
     * 初始化分页拦截器
     */
    protected InnerInterceptor initPaginationInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页分页条数限制，默认不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        // 溢出总页数后是否进行处理
        paginationInnerInterceptor.setOverflow(false);
        return paginationInnerInterceptor;
    }

    /**
     * 初始化动态表名拦截器
     */
    protected InnerInterceptor initDynamicTableNameInnerInterceptor() {
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        dynamicTableNameInnerInterceptor.setTableNameHandler((sql, tableName) -> {
            switch (tableName) {
                /*case xx:
                    String batch_code = DynamicTableParamHelper.getParamData("batch_code", String.class);
                    return tableName + "_" + batch_code;*/
                default:
                    return tableName;
            }
        });
        return dynamicTableNameInnerInterceptor;
    }

    /**
     * 多租户插件
     * 谨慎使用
     */
    protected InnerInterceptor iniTenantInterceptor() {
        return new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                String tenantCode = DynamicTableParamHelper.getParamData("tenantCode", String.class);
                if (StrUtil.isBlank(tenantCode)) {
                    return null;
                }
                return new StringValue(tenantCode);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_code";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                String tenantCode = DynamicTableParamHelper.getParamData("tenantCode", String.class);
                return StrUtil.isBlank(tenantCode) && "uc_tenant".equalsIgnoreCase(tableName);
            }
        });
    }

}
