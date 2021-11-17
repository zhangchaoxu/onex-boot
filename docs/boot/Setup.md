# How to Set Up

## 接口

## 技术准备
* [SpringBoot](https://spring.io/projects/spring-boot/) 
* [MyBatis-Plus](https://mybatis.plus/)    
* [EasyPoi](https://gitee.com/lemur/easypoi)    
* [Shiro](http://shiro.apache.org/)    
* [Java Mail Sender](https://docs.spring.io/spring-boot/docs/{bootVersion}/reference/htmlsingle/#boot-features-email)

## 开发准备
1. 建议使用JDK1.8+
2. 建议使用[Intellij IDEA](https://www.jetbrains.com/idea/)开发
3. 在IDEA中安装[Lombok](https://projectlombok.org/)插件

## 开发步骤
1. 检出代码
```shell
git clone https://github.com/zhangchaoxu/onex-boot.git
```
2. 使用IDEA打开检出代码文件夹
3. 创建MySQL数据库,并导入初始数据
4. 修改onex-boot\api\src\main\resources\application-dev.yml中数据库的地址、帐号和密码
5. 运行onex-boot\api\src\main\java\com\nb6868\onexboot\api\ApiApplication.java即可启动接口
6. 访问`http://127.0.0.1:18181/onex-boot-api`即可得到api success的提示
7. 项目集成了Swagger,访问`http://127.0.0.1:18181/onex-boot-api/swagger-ui/index.html`即可访问和调试接口
8. 上述接口中端口18080和路径onex-boot-api,在onex-boot\api\src\main\resources\application.yml中配置

### java开发规范
详见[阿里巴巴Java开发手册](https://github.com/alibaba/p3c)

## 前端

### 技术准备
* [VUE](https://cn.vuejs.org/)
* [ElementUI](https://element.eleme.cn/)

### 开发准备
1. 安装[node 8.11+](https://nodejs.org/en/download/),建议安装最新的LTS版
2. 安装IDE,比如Intellij IDEA,其它IDE也可以安装对应的插件
3. 建议使用[cnpm](http://npm.taobao.org/)作为npm仓库

## 开发步骤
1. 检出代码
```shell
git clone https://github.com/zhangchaoxu/onex-portal.git
```
2. 使用IDEA或者WebStorm等工具打开检出代码文件夹
3. 初始化安装`cnpm install`,该步骤只需要初次执行即可
4. 编译和开发热部署`cnpm run serve`
5. 编译和打包生产环境`cnpm run build`
6. Lints检查`cnpm run lint`

### vue开发规范
详见[Vant风格指南](https://youzan.github.io/vant/#/zh-CN/style-guide)

### Tips
1. JavaScript版本使用ES6
2. 打开ESLint用以修复格式检查

