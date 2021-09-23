package com.nb6868.onex.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.nb6868.onex.common.jpa.injector.MySqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus配置
 * see {https://baomidou.com/guide/interceptor.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
public class MybatisPlusConfig {

    /***
     * 使用ThreadLocal将表名传进来
     */
    // public static ThreadLocal<String> tableNameLocal = new ThreadLocal<>();

    /**
     * 配置分页
     * 新的分页插件,一缓和二缓遵循mybatis的规则
     * 需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题
     * see {https://mybatis.plus/guide/page.html}
     *
     * @return PaginationInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 动态表名拦截器
        // interceptor.addInnerInterceptor(initDynamicTableNameInnerInterceptor());
        // 分页拦截器
        interceptor.addInnerInterceptor(initPaginationInterceptor());
        return interceptor;
    }

    /**
     * 初始化分页拦截器
     */
    private InnerInterceptor initPaginationInterceptor() {
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
    private InnerInterceptor initDynamicTableNameInnerInterceptor() {
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

    /**
     * sql注入器
     *
     * @return inject
     */
    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }

}
