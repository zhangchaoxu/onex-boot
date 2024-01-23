## 消息
适用于需要发送消息，包括短信、电子邮件、钉钉通知等场景,支持基于模板灵活配置下次内容,便于业务端直接调用;

## 设计思路
提供消息模板(msg_tpl)和消息记录(msg_log)两张表    
模板表中定义消息的类型、发送参数等信息    
消息日志中记录每次消息发送的内容等信息

## 使用
1. 数据库中导入[sys_msg.sql](https://onex.nb6868.com/sql/sys_msg.sql)

2. 引入依赖
```xml
<dependency>
    <groupId>com.nb6868.onex</groupId>
    <artifactId>msg</artifactId>
    <version>${onex.version}</version>
</dependency>
```

3. 引入部分第三方依赖
3.1 若需要使用电子邮件,引入mail依赖
```xml
<!-- mail -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```
3.2 若需要使用微信公众号模板消息,引入java-weixin依赖
```xml
 <!-- wechat mp -->
<dependency>
    <groupId>com.github.binarywang</groupId>
    <artifactId>weixin-java-mp</artifactId>
    <version>${weixin.version}</version>
</dependency>
```
3.3 若需要使用微信小程序订阅消息,引入java-weixin依赖
```xml
 <!-- wechat mp -->
<dependency>
    <groupId>com.github.binarywang</groupId>
    <artifactId>weixin-java-miniapp</artifactId>
    <version>${weixin.version}</version>
</dependency>
```

4. 添加消息模板

5. 使用消息模板发送消息

## 消息模板配置参数
### 公共参数
| 字段 | 类型             | 描述                               |
| ---- |----------------|----------------------------------|
|verifyUserExist| boolean,默认false | 发送短信之前是否先检查用户是否存在                |
|sendTimeLimit| int,默认0,单位秒    | 该限制时间内该模板同一个收件人的发送时间限制,<=0表示不限制  |
|validTimeLimit| int,默认0,单位秒    | 消息本身的有效期,<=0表示不限制(1个世纪)         |
|codeKey| String,默认code  | 只对验证码类型模板有效,表示验证码在发送内容中的json key |
|codeBaseString| String,默认0123456789 | 只对验证码类型模板有效,表示验证码的允许范围           |
|codeLength| int,默认4        | 只对验证码类型模板有效,表示验证码的长度             |

### 阿里云短信
| 字段 | 描述   |
| ---- |------|
| AppKeyId | 应用ID |
| AppKeySecret | 应用密钥 |
| SignName | 短信签名 |
| TemplateId | 消息模板ID   |
| RegionId | 区域，默认cn-hangzhou   |

具体参数定义见[文档](https://help.aliyun.com/zh/sms/getting-started/use-sms-api-or-sdks)

### 华为云短信
| 字段 | 描述         |
| ---- |------------|
| AppKeyId | 应用ID       |
| AppKeySecret | 应用密钥       |
| SignName | 短信签名       |
| TemplateId | 消息模板ID     |
| ChannelId | 渠道         |
| RegionId | 区域，发送的地址前缀 |

具体参数定义见[文档](https://support.huaweicloud.com/api-msgsms/sms_05_0001.html)

### 聚合短信
| 字段 | 描述         |
| ---- |------------|
| AppKeyId | 应用ID       |
| TemplateId | 消息模板ID     |

### 电子邮件-原生
| 字段          | 描述                   |
|-------------|----------------------|
| Host        | 发件人host              |
| Port        | 发件端port,默认25         |
| Username    | 发件邮箱                 |
| Password    | 发件密码                 |
| SMTPAuth    | smtp auth,默认false    |
| SMTPTimeout | smtp timeout,默认10000 |

### 钉钉-工作通知
具体参数定义见[文档](https://open.dingtalk.com/document/orgapp-server/asynchronous-sending-of-enterprise-session-messages)

| 字段 | 描述           |
| ---- |--------------|
| AppKeyId | 应用ID      |
| AppKeySecret | 应用密钥 |
| AgentId | 发送消息时使用的微应用的AgentID |

### 钉钉-机器人
具体参数定义见[文档](https://developers.dingtalk.com/document/robots/custom-robot-access)

| 字段          | 描述           |
|-------------|--------------|
| AccessToken | 机器人token      |
| Keywords    | 加密关键词 |


### 微信公众号-模板消息
具体参数定义见[文档](https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Template_Message_Interface.html)

| 字段         | 描述        |
|------------|-----------|
| AppId      | 公众号AppId  |
| AppSecret  | 公众号Secret |
| TemplateId | 模板消息ID    |
```sql
INSERT INTO `sys_msg_tpl`(`code`, `name`, `type`, `channel`, `platform`, `title`, `content`, `params`,
                          `create_id`, `create_time`, `update_id`, `update_time`)
VALUES ('WX_TEMPLATE_NOTIFY', '微信模板通知', 2, 'wx', 'mpTemplate', '消息名称(无作用)', NULL,
        '{\"AppId\": \"wx1234\", \"AppSecret\": \"abcd\", \"TemplateId\": \"ABCDEF\"}',
        NULL, now(), NULL, now());
```
