package com.nb6868.onex.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.nb6868.onex.common.jpa.injector.MySqlInjector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                    return tableName + "_" + tableNameLocal.get();*/
                default:
                    return tableName;
            }
        });
        return dynamicTableNameInnerInterceptor;
    }

}
