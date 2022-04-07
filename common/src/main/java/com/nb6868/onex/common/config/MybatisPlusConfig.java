package com.nb6868.onex.common.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis-plus配置
 * see {https://baomidou.com/guide/interceptor.html}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Configuration
@ConditionalOnProperty(name = "onex.mybatisplus.enable", havingValue = "true")
public class MybatisPlusConfig extends BaseMybatisPlusConfig {

    /**
     * 配置插件
     *
     * see {https://baomidou.com/pages/2976a3/}
     *
     * 顺序:
     * 多租户,动态表名
     * 分页,乐观锁
     * sql 性能规范,防止全表更新与删除
     *
     * @return PaginationInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户插件
        // interceptor.addInnerInterceptor(iniTenantInterceptor());
        // 动态表名拦截器
        // interceptor.addInnerInterceptor(initDynamicTableNameInnerInterceptor());
        // 分页拦截器
        interceptor.addInnerInterceptor(initPaginationInterceptor());
        // 乐观锁插件
        // interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

}
