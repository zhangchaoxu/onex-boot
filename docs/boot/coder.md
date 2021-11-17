# 代码生成器

参考[人人开源renren-generator](https://gitee.com/renrenio/renren-generator) 工具生成entity、xml、dao、service、vue、sql代码 
结合[onex-boot](http://portal.onex.nb6868.com)和[onex-portal](https://github.com/zhangchaoxu/onex-portal)使用

* 注意：代码生成器本身不需要依赖booster和api，可以独立运行

### 部署使用

* 通过git下载源码
* 修改application.yml中的数据库连接信息
* 修改generator.properties中的代码生成策略
* 直接运行GeneratorApplication.java，则可启动项目
* 项目访问路径：[http://localhost:8083/generator](http://localhost:8083/generator)
* 选中需要生成的代码的表，生成代码后，将代码放到工程对应的位置

## TODO

\[\] 按策略生成代码 \[\] 生成android代码 \[\] 生成mobile代码

