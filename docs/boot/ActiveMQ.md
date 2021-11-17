# ActiveMQ

注意点
1. 需要依赖线程池,否则每个报文都会生成一个连接,会导致MQ服务端连接数爆炸
2. org.apache.activemq:activemq-pool支持springboot低版本，高版本需要org.messaginghub:pooled-jms
3. org.messaginghub:pooled-jms v2.0.0支持jdk10,v1.2.2支持jdk8,需要合理选择版本
4. `'org.springframework.jms.core.JmsMessagingTemplate' that could not be found`很可能是pooled不当版本引起的

### 1. 引入依赖
注意: pooled-jms 2.0.0需要jdk10,1.2.2支持jdk8
```
<!-- activemq -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
</dependency>
<!-- pooled -->
<dependency>
    <groupId>org.messaginghub</groupId>
    <artifactId>pooled-jms</artifactId>
    <version>1.2.2</version>
</dependency>
```

### 2. 定义配置文件
```
spring:
  ## jms
  jms:
    ## 订阅模式需要设置
    pub-sub-domain: true
  ## activemq
  activemq:
    broker-url: tcp://127.0.0.1:61616
    user: username
    password: password
    pool:
      # 是否启用线程池
      enabled: true
      # 最大连接数
      max-connections: 100
```

### 3. 定义MQService
```
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Topic;

@Service
public class MqService {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    public boolean sendMsg(String topicName, String msg) {
        try {
            this.jmsMessagingTemplate.convertAndSend(new ActiveMQTopic(topicName), msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
```
