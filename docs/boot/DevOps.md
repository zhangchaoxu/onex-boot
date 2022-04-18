# 编译部署
后台管理功能前后端分离,前端为vue工程,后端为springboot工程,需要分开编译部署。

## 前端

### 环境配置

前端需要配置所连接的接口地址,public/index.html中对应环境的apiURL,打包部署环境为prod

### 编译

在portal目录中执行build.cmd(```cnpm run build:prod```)          
编译结果为dist文件夹,文件夹中为一个index.html页面以及若干css/js等静态文件。

### 部署

将编译所得的dist目录中所有的文件复制到前端服务器(比如Nginx、Apache、Tomcat),甚至是阿里云的oss即可。

### Vue Router Mode

router/index.js中的的mode  
如果为hash模式,访问路径会出现`#`,比如`http://127.0.0.1/#/home`  
如果为history模式,则可以避免出现`#`,但是因为没有实际的路径文件,因此访问会出现404,需要在配置文件中加入对应的配置。  
如nginx为

```text
# 解决404
location / {
    try_files /$uri /$uri/ /index.html$args;
}
```

## 接口

### 环境配置

接口的运行环境有多种方式可以指定    
1. 打包的时候用`-P`指定环境,环境由代码中的application_profile.yml文件配置         
2. 运行的时候通过参数`-Dspring.config.activate.on-profile`指定环境,合作和用`--server.port`,`-server.servlet.context-path`指定具体的参数        
3. 将配置文件放在jar同目录下,也可以指定该配置文件作为运行环境,这样做的好处是修改配置(如数据库)不需要重新打包

### 编译

直接使用`mvn clean package -Dmaven.test.skip=true -P prod`即可得到所需的jar或者war包      

#### 单jar包(spring-boot-maven-plugin)
编译结果只有一个jar包,方便管理,但是jar包比较大,而且修改配置或者静态资源需要重新打包部署       

```xml
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <includeSystemScope>true</includeSystemScope>
            </configuration>
        </plugin>
        <!-- 跳过测试 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <skipTests>true</skipTests>
            </configuration>
        </plugin>
    </plugins>
</build>
```

#### 分离jar/lib/resource(maven-jar-plugin)
编译结果只有一个小jar包、lib、resource,便于后面有热修改,只需要修改覆盖对应的内容即可          
注意lib若有修改,要清空重新上传,否则可能出现不同版本的相同依赖jar。
注意两点：
1. copy-lib中的includeScope决定了将哪些依赖jar包复制过去,比如compile就不会将scope为runtime的jar包复制过去
2. 如果存在自定义的本地jar，maven-jar-plugin中的Class-Path需要将jar用空格分隔加进去，比如`./resources/ lib/abc-1.0.0.jar`

```xml
<build>
    <finalName>${project.artifactId}</finalName>
    <plugins>
        <!-- 打包jar,排除lib -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <configuration>
                <archive>
                    <addMavenDescriptor>false</addMavenDescriptor>
                    <manifest>
                        <addClasspath>true</addClasspath>
                        <classpathPrefix>lib/</classpathPrefix>
                        <mainClass>apiApplication</mainClass>
                    </manifest>
                    <manifestEntries>
                        <!-- 若有其他比如自定义jar,空格隔开加后面 -->
                        <Class-Path>./resources/</Class-Path>
                    </manifestEntries>
                </archive>
            </configuration>
        </plugin>
        <!-- 分离lib -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-lib</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>${project.build.directory}/lib</outputDirectory>
                        <excludeTransitive>false</excludeTransitive>
                        <stripVersion>false</stripVersion>
                        <includeScope>compile</includeScope>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <!-- 分离resources -->
        <plugin>
            <artifactId>maven-resources-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-resources</id>
                    <phase>package</phase>
                    <goals>
                        <goal>copy-resources</goal>
                    </goals>
                    <configuration>
                        <resources>
                            <resource>
                                <directory>src/main/resources</directory>
                                <includes>
                                    <include>*.*</include>
                                    <include>static/**</include>
                                    <include>templates/**</include>
                                </includes>
                            </resource>
                        </resources>
                        <outputDirectory>${project.build.directory}/resources</outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <!-- 跳过测试 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <skip>true</skip>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### jar运行

_Spring Boot项目，推荐打成jar包的方式，部署到服务器上_

Spring Boot内置了Tomcat，可配置Tomcat的端口号、初始化线程数、最大线程数、连接超时时长、https等等,配置文件是application.yml

#### windows部署

`java -jar api.jar --spring.config.activate.on-profile=prod`

#### linux部署

建议使用shell执行,可以指定运行环境、端口、context等 
```
nohup java -Dspring.config.activate.on-profile=prod -jar api.jar --server.port=8080 --server.servlet.context-path=/api 2>&1 | cronolog log.%Y-%m-%d.out >> /dev/null &
```

如果使用cronolog做日志分割，可能需要先安装cronolog`yum install -y cronolog httpd`

优化脚本如下

```text
process=`ps -fe|grep "one.jar" |grep -ivE "grep|cron" |awk '{print $2}'`
if [ !$process ];
then
echo "stop erp process $process ....."
kill -9 $process
sleep 1
fi
echo "start erp process....."
nohup java -Dspring.config.activate.on-profile=prod -jar api.jar --server.port=8080 --server.se
rvlet.context-path=/api 2>&1 | cronolog log.%Y-%m-%d.out >> /dev/null &
echo "start erp success!"
```

### tomcat部署

1. 将Application对应的pom文件中的packaging改为war `<packaging>jar</packaging>`
2. 排除tomcat的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-tomcat</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.tomcat.embed</groupId>
        <artifactId>tomcat-embed-jasper</artifactId>
    <scope>provided</scope>
</dependency>
```

3. 编译打包 `mvn clean package -Dmaven.test.skip=true -P prod`

### tomcat7部署

对于tomcat，按照上述方式直接部署可能出现错误`java.lang.NoClassDefFoundError: javax/el/ELManager`,这是由于tomcat7内置的el包版本太低。 解决办法是手动下载[el3.0](https://mvnrepository.com/artifact/javax.el/javax.el-api/3.0.0),放到tomcat的lib包中

## 其它

### 内置tomcat加入证书

一般建议通过nginx作为前置服务器做代理，但有时候遇到直接将tomcat作为前置应用的情况，同时又要求支持ssl协议，就需要将证书加入到SpringBoot内置Tomcat。

1. 申请证书: 适用于Tomcat的Https证书
2. 证书放到classpath: 将证书文件，比如xquick.idogfooding.com.pfx放到resources文件夹中，最后会打包到classpath中
3. 配置端口：在application.yml文件中配置http和https的端口

```yaml
#https port
port: 8089 
#http port
http:
port: 8088
```

4. 配置application: 在启动Application,比如AdminApplication中加入以下配置

```java
@Value("${server.port}") private Integer httpsPort;   @Value("${server.http.port}") private Integer httpPort;   @Bean public TomcatServletWebServerFactory servletContainer() {
TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {    @Override
protected void postProcessContext(Context context) {
SecurityConstraint securityConstraint = new SecurityConstraint();
securityConstraint.setUserConstraint("CONFIDENTIAL");
SecurityCollection collection = new SecurityCollection();
collection.addPattern("/*");
securityConstraint.addCollection(collection);
context.addConstraint(securityConstraint);
}
};
tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
return tomcat; }   private Connector initiateHttpConnector() {
Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
connector.setPort(httpPort);
connector.setScheme("http");
// 不强制跳转为https
connector.setSecure(true);
// 访问http跳转到https
// connector.setRedirectPort(httpsPort);  return connector; }
```

1. 检查防火墙: 注意检查两个端口是否都在防火墙和云服务器安全策略中

### 跨域配置

跨域一般通过CORS解决，通过Nginx配置即可，CORS需要浏览器和服务器同时支持。目前，主流浏览器都支持该功能，Nginx配置如下所示：

```text
server {
   listen 80;
   server_name xquick.nb6868.com;
   location /xquick {
       alias /data/xquick-admin;
           index index.html;
       }
       location / {
           if ($request_method = 'OPTIONS') {
               add_header 'Access-Control-Allow-Origin' '$http_origin';
               add_header 'Access-Control-Allow-Credentials' 'true';
               add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';
               add_header 'Access-Control-Allow-Headers' 'DNT,X-CustomHeader,Keep-Alive,User-Age
               nt,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,token';
               add_header 'Access-Control-Max-Age' 1728000;
               add_header 'Content-Type' 'text/plain charset=UTF-8';
               add_header 'Content-Length' 0;
               return 204;
           }
           add_header 'Access-Control-Allow-Origin' '$http_origin';
           add_header 'Access-Control-Allow-Credentials' 'true';
           add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';
           add_header 'Access-Control-Allow-Headers' 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X
           -Requested-With,If-Modified-Since,Cache-Control,Content-Type,token';
           proxy_pass http://localhost:8080;
           client_max_body_size 1024m;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header Host $host;
           proxy_redirect off;
   }
}
```

## 运维实践
以下为一种比较推荐的[实践方式](DevOps_foo.md)

## ref
1. [SpringBoot 项目打包分开lib,配置和资源文件](https://blog.csdn.net/qq_30220585/article/details/90201643)

