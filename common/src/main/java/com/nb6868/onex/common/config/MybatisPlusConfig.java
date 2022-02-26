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
        // 乐观锁插件
        // interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 动态表名拦截器
        // interceptor.addInnerInterceptor(initDynamicTableNameInnerInterceptor());
        // 分页拦截器
        interceptor.addInnerInterceptor(initPaginationInterceptor());
        return interceptor;
    }

}
