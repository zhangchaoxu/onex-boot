# 访问控制
使用[shiro]控制用户的访问控制

## 调用顺序
首先需要明确一点,Spring中访问一个接口,执行的顺序
1. 过滤器Filter(见FilterConfig),定义了cros->shiro->xss三个过滤器,权限控制就是在这个shiro过滤器中实现        
2. 拦截器Interceptors(见WebMvcConfig.addInterceptors)
3. 切片Aspect(如LogOperationAspect)

## Shiro过滤器
规则见ShiroConfig,除了anon以外,其它接口都需要经过shiro拦截
注意加入到anon的接口,即使请求带有token也获取不到用户信息

## Spring过滤器,拦截器,切面的区别

1.过滤器 -> 通过集成Filter实现   
可以获取原始的http请求与响应    
但是无法获取请求要访问的类与方法,以及参数       
(例如：拿不到你请求的控制器和请求控制器中的方法的信息)

2.拦截器 -> 基于SpringMVC提供的拦截器接口,自定义实现      
可以获取请求访问的类与方法   
但是无法获取请求参数的值        
(例如：可以拿到你请求的控制器和方法,却拿不到请求方法的参数),具体可根据dispatcherServlet跟踪源码

3.切面 -> 基于Spring,通过Aspect注解实现     
可以获取访问的类、方法以及参数值。   
但是无法获取http原始的请求与响应的对象

4.执行顺序
1 请求处理顺序
过滤器 -> 拦截器 -> 切面
2 报错处理顺序
切面 -> ControllerAdvice -> 拦截器 -> 过滤器 -> 服务
3 使用场景--->AOP切面和拦截器通常结合注解一起使用
