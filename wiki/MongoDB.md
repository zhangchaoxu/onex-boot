# MongoDB
对于需要读取MongoDB数据的情况。

## 使用方法：
1. SpringData自带对[MongoDB的支持](https://spring.io/projects/spring-data-mongodb)，只需要在pom中声明依赖即可
```xml
<!-- mongodb -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

2. 配置文件application-profile.yml中加入Mongo的连接地址
确保该地址可以连接上,否则可能在启动时候项目会报错
```yaml
spring:
  data:
    # MongoDB
    mongodb:
      host: 127.0.0.1
      database: test
```

3. 使用MongoTemplate操作数据
如果轻度使用,可以直接使用MongoTemplate类似JDBC一样读取数据
```java
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Autowired
private MongoTemplate mongoTemplate;

/**
* 通过条件查询符合条件的内容列表
*/
public List<ModelEntity> list(Date time, String code) {
    Criteria criteria = Criteria.where("time").gte(time).and("code").is(code);
    Query query = Query.query(criteria).with(Sort.by("date").descending()).limit(2000);
    List<ModelEntity> list = mongoTemplate.find(query, ModelEntity.class);
    return list;
}
```

4. 更多使用方法见[文档](https://spring.io/projects/spring-data-mongodb)
