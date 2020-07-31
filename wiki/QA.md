# QA

1. 为什么大多数请求都会请求两次?

由于前后端分离,往往会带来跨域的问题,对于非简单请求,浏览器会先发一个options请求判断服务是否连通,再发送真正的请求。 表现出来就是所有的请求都会发生两次,一次options,一次get\(put post patch delete\)。 避免的方法是所有的请求都改造成简单请求,但这往往是不实际的。 为了一定程度提高性能,我们增加一个CrosFilter,对所有的OPTIONS请求直接放行,好处是OPTIONS请求的时间会提高,缺点也很明显,失去了OPTIONS请求预检的意义。

1. `java.io.IOException: The temporary upload location [/tmp/tomcat.x.x/work/Tomcat/localhost/xxx] is not valid`

对于Multipart上传内容,Springboot会先把文件缓存在tmp文件夹中,然而在CentOS有一个定期清空tmp的机制,因此会导致文件上传功能长时间不用的时候缓存文件夹被清空掉,然后上传失败的问题。 注意该问题暴露在前端是错误`No 'Access-Control-Allow-Origin' header is present on the requested resource.`,比较容易误导人。 最简单的解决办法是配置文件中加上tomcat.basedir的路径 [参考](https://blog.csdn.net/qq_21383435/article/details/91891664)

1. 如何在vue中使用jQuery?

虽然不推荐在vue中还是用jQuery,但是实际项目中往往有类似需求。 解决思路参考[vue-cli 3.0+ \#\# 链式操作 \(高级\)](https://cli.vuejs.org/zh/guide/webpack.html#%E9%93%BE%E5%BC%8F%E6%93%8D%E4%BD%9C-%E9%AB%98%E7%BA%A7) 3.1 index.html中添加jquery引用`<script src="//cdn.bootcss.com/jquery/3.4.1/jquery.min.js"></script>` 3.2 vue.config.js中添加添加externals定义 `chainWebpack: config => {    
// 按需引入jquery    
config.externals({ jquery: 'jQuery' })    
// ...` 3.3 需要使用jquery的页面中加入`import $ from 'jquery'`,就可以在方法中使用$了,注意时序

1. 如何解决类命名冲突的问题    

在实现过程中经常出现不同包中同名类的情况,比如不同功能模块都有IndexController,比如系统中有pay\_order和shop\_order两个去除前缀后的同名表\(表设计应该尽量避免\)。  
4.1 对于Spring中不同包下同名类,可以通过namespace`@ComponentScan(nameGenerator = SpringBeanNameGenerator.class)`解决 4.2 对于Mybatis中去除前缀后的同名表,Mapper\(Dao\)也一样通过namespace`@MapperScan(basePackages="com.nb6868.onex.modules.*.dao", nameGenerator = SpringBeanNameGenerator.class)`解决 4.3 对于Mybatis中去除前缀后的同名表,Entity可以通过Alias`@Alias("ShopOrderEntity")`解决

1. Maven项目如何引入外部jar包

在开发过程中不可避免会出现有依赖包不存在maven库的情况,除了将该jar包上传到maven库以外,我们还可以手动配置添加jar的依赖 5.1 在src/libs/中加入依赖的jar包 5.2 在对应module的pom文件中加入依赖配置

```text
<dependency>
    <groupId>com.xyz</groupId>
    <artifactId>abc</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${pom.basedir}/src/libs/abc.1.0.0.jar</systemPath>
</dependency>
```
