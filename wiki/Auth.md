# 访问控制
使用[shiro]控制用户的访问控制

## 调用顺序
首先需要明确一点,Spring中访问一个接口,执行的顺序
1. 过滤器Filter(见FilterConfig),定义了cros->shiro->xss三个过滤器,权限控制就是在这个shiro过滤器中实现        
2. 拦截器Interceptors(见WebMvcConfig.addInterceptors)
3. 切片Aspect(如LogOperationAspect)

## Shiro过滤器
规则见ShiroConfig,除了anon以外,其它接口都需要经过shiro拦截
