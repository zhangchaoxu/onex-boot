## 日志管理
日志记录是系统最常见的需求，包括登录、操作、错误的日志记录。      
OneX内置了日志处理模块，支持日志的查询，基于注解实现便捷的操作日志记录。

## 设计思路
提供日志(log)表    
日志表中包含日志类型、时间、请求方、请求参数等基本信息

## 使用
1. 数据库中导入[sys_log.sql](https://onex.nb6868.com/sql/sys_log.sql)

2. 日历模块集成在sys模块中，若需使用，引入sys依赖
```xml
<dependency>
    <groupId>com.nb6868.onex</groupId>
    <artifactId>sys</artifactId>
    <version>${onex.version}</version>
</dependency>
```

3. 打开日志存储的配置参数
```yaml
onex:
  log:
    enable: true
```

4. 在需要记录日志的接口中添加注解
```java
 @LogOperation("接口名称")
```
