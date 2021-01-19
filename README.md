# OneX
![](https://cdn4.iconfinder.com/data/icons/space-and-astronomy-1/800/rocket-128.png)

OneX致力于搭建一套软件项目中经常遇到的一些常见需求,比如权限管理、消息管理、日志管理等。     
结合代码生成器工具,减轻开发人员的繁琐开发工作。

## 演示地址
* [OneX 管理后台](http://onex-admin.nb6868.com)

## 项目组成
项目前后端分离

* [boot](https://github.com/zhangchaoxu/onex-boot/tree/master/boot)  SpringBoot实现的Restful接口
* [portal](https://github.com/zhangchaoxu/onex-boot/tree/master/portal) Vue实现的后端管理页面
* [wiki](https://github.com/zhangchaoxu/onex-boot/tree/master/wiki) 文档说明
  
* [onex-coder](https://github.com/zhangchaoxu/onex-coder) 代码生成器

## 已实现功能模块
### 用户权限管理模块
包含用户管理、角色管理、部门管理、菜单/按钮/接口权限管理,支持基于角色的访问控制;

### 消息模块
包含短信、邮件的模板和发送记录管理,支持基于模板灵活配置下次内容,便于业务端直接调用;

### 日志模块
包含登录、操作、错误的日志记录,支持日志的查询,基于注解实现便捷的操作日志记录;

### 系统模块
包含阿里云/本地的存储管理、字典管理、系统参数管理、区域管理、日历管理等;

### 定时任务模块
包括定时任务管理，定时任务的启停和日志,可在后台启停任务,修改任务的执行周期,便于系统中定时任务需求的集成;

### 内容模块
包含文章分类管理、文章内容管理,实现了一个简易的CMS系统;

## 待实现功能模块
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
* [MCMS](https://github.com/ming-soft/MCMS)

## 数据来源
* [行政区域](https://github.com/xiangyuecn/AreaCity-JsSpider-StatsGov/)
* [万年历]()

## 文档 
更多详细内容见[gitbook文档](https://onex.nb6868.com)
