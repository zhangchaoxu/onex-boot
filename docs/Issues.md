# Issues

## 1. 为什么很多接口都会调用两次?

由于前后端分离,往往会带来跨域的问题,对于[非简单请求](http://www.ruanyifeng.com/blog/2016/04/cors.html),浏览器会先发一个options请求判断服务是否连通,等options请求成功后,再发送真正的请求。  
具体表现出来就是所有的请求在Chrome的Network中都会发生两次,一次options,一次get/put/post/patch/delete。
* 避免方法一:    
  将所有的请求都改造成简单请求,但这往往是不实际的。
* 避免方法二:    
  OPTIONS请求直接放行,好处是OPTIONS请求的时间会提高,缺点也很明显,失去了OPTIONS请求预检的意义。
  具体实现见CrosFilter。

## 2. 如何在js中调用vue方法

实际开发中往往会嵌入部分html和js代码,比如[amap infowindow](https://lbs.amap.com/api/javascript-api/guide/overlays/infowindow)中显示内容
1. 在vue的钩子函数中将需要调用的函数赋值给window。
```vue
mounted() {
    //将Vue方法传到全局对象window中
    window.methName = this.methName
}
```
2. js直接使用即可
```javascript
<script type="text/javascript">
　　methName();
</script>
```

## 3. 如何在vue中使用jQuery?

虽然不推荐在vue中还是用jQuery,但是实际项目中往往有类似需求。
解决思路参考[vue-cli 3.0+ 链式操作 (高级)](https://cli.vuejs.org/zh/guide/webpack.html#%E9%93%BE%E5%BC%8F%E6%93%8D%E4%BD%9C-%E9%AB%98%E7%BA%A7)

* 3.1 index.html中添加jquery引用
```html
<script src="//cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>
``` 

* 3.2 vue.config.js中添加添加externals定义
```
chainWebpack: config => {    
// 按需引入jquery    
config.externals({ jquery: 'jQuery' })    
// ...
``` 

* 3.3 需要使用jquery的页面中加入`import $ from 'jquery'`,就可以在方法中使用$了,注意时序

## 4. 如何解决类命名冲突的问题

在实现过程中经常出现不同包中同名类的情况,比如不同功能模块都有IndexController,比如系统中有pay\_order和shop\_order两个去除前缀后的同名表\(表设计应该尽量避免\)。
* 4.1 对于Spring中不同包下同名类,可以通过namespace解决
```java
@ComponentScan(nameGenerator = SpringBeanNameGenerator.class)
```     
* 4.2 对于Mybatis中去除前缀后的同名表,Mapper\(Dao\)也一样通过namespace解决
```java
@MapperScan(basePackages="com.nb6868.onexboot.api.modules.*.dao", nameGenerator = SpringBeanNameGenerator.class)
``` 
* 4.3 对于Mybatis中去除前缀后的同名表,Entity可以通过Alias`@Alias("ShopOrderEntity")`解决

## 5. Maven项目如何引入外部jar包

在开发过程中不可避免会出现有依赖包不存在maven库的情况,除了将该jar包上传到maven库以外,我们还可以手动配置添加jar的依赖
* 5.1 在src/libs/中加入依赖的jar包
* 5.2 在对应module的pom文件中加入依赖配置
```xml
<dependency>
    <groupId>com.foo</groupId>
    <artifactId>boo</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${pom.basedir}/src/libs/boo.1.0.0.jar</systemPath>
</dependency>
```

## 6. 报错`java.io.IOException: The temporary upload location [/tmp/tomcat.x.x/work/Tomcat/localhost/xxx] is not valid`

对于Multipart上传内容,Springboot会先把文件缓存在tmp文件夹中,然而在CentOS有一个定期清空tmp的机制,因此会导致文件上传功能长时间不用的时候缓存文件夹被清空掉,然后上传失败的问题。    
注意该问题暴露在前端是错误`No 'Access-Control-Allow-Origin' header is present on the requested resource.`,比较容易误导人。   
最简单的解决办法是配置文件中加上tomcat.basedir的路径 [参考](https://blog.csdn.net/qq_21383435/article/details/91891664)

## 7. 启动报错
```
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
```

除了检查yaml中数据库配置是否正确,格式是否正确外,检查启动Application,如果@SpringBootApplication的exclude中加了DruidDataSourceAutoConfigure.class会导致这个问题         
exclude中加了DruidDataSourceAutoConfigure.class是为了适配[Dynamic Datasource](boot/DynamicDatasource.md),如果使用单个数据库的方式不需要添加。

## 8. 启动时候提示很多类找不到

除了检查类是否存在,依赖包是否正确以外,可以尝试IDEA->File->Invalidate Cache/Restart试试

## 9. 本地挂代理后,java请求还是没从代理走
- 起因: 在本地用EasyConnect开启一个VPN代理以后，在IDEA运行java程序，网络请求没有从代理走。
- 解决: 在java启动参数中加上`-Djava.net.preferIPv4Stack=true`
  ![img](../_media/issue-ipv4-stack.png)

## 10. 宝塔面板跳过强制登录
- v7.8.0版本已失效
- 起因: 宝塔面板新版本，强制要求登录
- 解决: 
```shell
mv /www/server/panel/data/bind.pl /www/server/panel/data/bind.pl.bak
```

## 11. 远程调试
在部分开发时候,需要使用服务器的环境,然后服务器上无法debug,可以使用使用Intellij IDEA的远程调试功能

1. 在IDEA中添加一个远程调试
   ![img](../_media/remote_debug.png)
2. 在服务端开启运行的参数加入
`-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8890`
3. 开启debug,加入断点即可

- 注意: 
1. 该端口为调试通信用端口,不是工程运行端口,端口需开通防火墙
2. 本地代码与远程代码需要保持一致
3. console不会打印出信息,只能在断点寻找需要的信息

## 12. 在DAO中使用<script>报错org.xml.sax.SAXParseException; lineNumber: 1; columnNumbe
- 原因：这是由于在sql中含有< >等符号，编译出现错误
- 解决：`<`替换为`&lt;` `>`替换为`&gt;`  
