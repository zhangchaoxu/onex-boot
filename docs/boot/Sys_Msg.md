## 消息
适用于需要发送消息，包括短信、电子邮件、钉钉通知等场景。

## 设计思路
提供消息模板(msg_tpl)和消息记录(msg_log)两张表    
模板表中定义消息的类型、发送参数等信息    
消息日志中记录每次消息发送的内容等信息

## 使用
若需要使用电子邮件，需要引入mail依赖
```xml
<!-- mail -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

## 消息模板配置参数
### 公共参数
| 字段 | 类型                  | 描述                               |
| ---- |---------------------|----------------------------------|
|verifyUserExist| boolean,默认false | 发送短信之前是否先检查用户是否存在                |
|sendTimeLimit| int,默认0,单位秒         | 该限制时间内该模板同一个收件人的发送时间限制           |
|validTimeLimit| int,默认0,单位秒         | 消息本身的有效期                         |
|codeKey| String,默认code       | 只对验证码类型模板有效,表示验证码在发送内容中的json key |
|codeBaseString| String,默认0123456789 | 只对验证码类型模板有效,表示验证码的允许范围           |
|codeLength| int,默认4             | 只对验证码类型模板有效,表示验证码的长度             |

### 阿里云短信
AppKeyId: 应用ID
AppKeySecret: 应用密钥
SignName: 签名
TemplateId: 消息模板ID
RegionId: 区域，默认cn-hangzhou

### 华为云短信
AppKeyId: 应用ID
AppKeySecret: 应用密钥
SignName: 签名
TemplateId: 消息模板ID
ChannelId: 渠道
RegionId: 区域

### 聚合短信
AppKeyId: 应用KEY ID
TemplateId: 消息模板ID

### 电子邮件-原生
host: 发件人host
port: 发件端port
username: 发件邮箱
password: 发件密码

### 钉钉-工作通知
AppKeyId: 应用ID
AppKeySecret: 应用密钥
AgentId: 发送消息时使用的微应用的AgentID

### 钉钉-机器人
access_token: 机器人token
keywords: 加密关键词
