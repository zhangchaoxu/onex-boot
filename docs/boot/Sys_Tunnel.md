# 隧道
在实际项目实践中，经常遇到需要调用第三方接口、读取第三方数据库、获取服务器系统信息等情况，而这部分功能往往限制在限定网络环境(项目部署服务器在该限定网络内)下才能访问到访问。     
上述资源的调用，在开始调试阶段服务器不方便调试，所以可以使用隧道将上述资源暴露出来。
Onex基于Hutool的[Db数据库](https://doc.hutool.cn/module/db/)、[HTTP客户端](https://doc.hutool.cn/module/http/)和[系统调用](https://doc.hutool.cn/module/system/)等模块实现了相关的功能穿透。

## 注意事项
这是一种偷懒和无奈的解决思路，不到万不得已不建议投入生产环境使用。
若要投入使用，需要谨慎处理好接口权限的控制

## 使用
1. 引入依赖
```xml
<dependency>
    <groupId>com.nb6868.onex</groupId>
    <artifactId>tunnel</artifactId>
    <version>${onex.version}</version>
</dependency>
```

2. 调用

