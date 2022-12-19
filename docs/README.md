# OneX
![img](./_media/icon.svg ':size=120x120')

> OneX致力于减轻开发人员的项目初始化搭建工作,减轻开发人员的繁琐开发工作。
> 
> 主要针对日常开发项目建设过程中遇到的常见需求,比如权限管理、消息管理、日志管理、定时任务等。
> 
> 项目无意于搭建一个低代码平台或者非常完善的代码生成工具，只是想为陷于日常繁琐工作的朋友们提供点能减轻工作量的途径。
> 
> 项目比较适用于业务功能相对比较简单，同时数据量(不需要考虑分库分表)和并发量(不需要考虑负载均衡)不大的业务场景。
> 
> 项目虽然考虑了多租户的使用场景，并且预留了租户信息字段，但是考虑不够完善，对于有多租户需求的，建议按照自己的业务需求定制多租户部分的代码。 

## 演示地址
* [管理后台](https://portal-onex.nb6868.com/)

## 项目组成
* [onex-boot](https://github.com/zhangchaoxu/onex-boot/)  OneX基础工程，包含了common、sys、uc等模块，引入依赖即可使用
* [onex-api](https://github.com/zhangchaoxu/onex-api/)  基于onex-boot实现的一套接口，作为一个工程模板，可以直接fork创建工程
* [portal](https://github.com/zhangchaoxu/onex-portal) Vue2与ElementUI实现的管理页面

## 功能模块
### 已实现
见左侧功能模块菜单

## 待实现
* [ ] 商城模块
* [ ] 微信模块
* [ ] 更加灵活的用户权限管理,目前RBAC0
* [ ] 工作流引擎
* [ ] 更加强大和使用的代码生成工具,不再支持批量生成,单表操作,可以勾选字段和类型

## 图标logo
* [登录背景生成网站](https://trianglify.io)
* [avatar](https://www.iconfinder.com/iconsets/business-avatar-1)
* [logo](https://www.iconfinder.com/icons/2120156/astronaut_astronomy_rocket_science_space_icon)
* [Icon](https://www.iconfont.cn/collections/detail?cid=9402)

## Thanks
* [renren.io](https://www.renren.io/)
* [Jeecg-Boot](http://www.jeecg.com/)
* [Bladex](https://bladex.vip/#/)
* [Avue](https://avuejs.com/)
* [litemall](https://github.com/linlinjava/litemall)
* [shopxx](https://www.shopxx.net/products/shopxx-b2b2c)
* [悟空CRM](https://gitee.com/wukongcrm/72crm-java)
* [vant](https://youzan.github.io/vant/)
* [UniApp](https://uniapp.dcloud.io/)
* [JeeCMS](http://www.jeecms.com/)
* [Docsify](https://docsify.js.org/)
* [vue-admin-better](https://github.com/chuzhixin/vue-admin-better)
* [geetest](https://docs.geetest.com/)

