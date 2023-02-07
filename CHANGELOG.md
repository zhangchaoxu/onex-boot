# CHANGELOG

## [3.7.10] 2023.02.03
- upgrade: upgrade huaweicloud obs to 3.22.12

## [3.7.9] 2023.01.30
- upgrade: add qrcode login

## [3.7.8] 2023.01.29
- upgrade: change cache define

## [3.7.7] 2023.01.28
- upgrade: upgrade springboot to 2.7.7

## [3.7.6] 2023.01.16
- upgrade: upgrade shiro to 1.11.0

## [3.7.5] 2023.01.10
- change: remove api-path define, add default oss path

## [3.7.4] 2023.01.10
- change: change login params define

## [3.7.3] 2023.01.09
- change: change role id from Long to String

## [3.7.2] 2023.01.07
- upgrade: replace logic delete method 

## [3.7.0] 2023.01.05
- release: release new version

## [3.6.74] 2023.01.04
- fix: fix region and user query issue

## [3.6.73] 2023.01.04
- fix: fix common yml auth define

## [3.6.72] 2023.01.03
- upgrade: upgrade hutool to 5.8.11
- upgrade: upgrade mybatisplus to 3.5.3.1

## [3.6.61] 2022.12.26
- upgrade: change RequiresPermissions in job/websocket/msg

## [3.6.58] 2022.12.23
- upgrade: upgrade dynamic-datasource to 3.6.1
- upgrade: upgrade druid to 1.2.15

## [3.6.57] 2022.12.23
- upgrade: upgrade springboot to 2.7.7
- upgrade: upgrade aliyun oss to 3.16.0

## [3.6.56] 2022.12.22
- new: add job module

## [3.6.54] 2022.12.09
- fix: simplify msg code

## [3.6.53] 2022.12.05
- fix: fix local file path issue

## [3.6.52] 2022.11.29
- upgrade: add auth whiteList

## [3.6.51] 2022.11.25
- upgrade: upgrade springboot to 2.7.6
- upgrade: upgrade hutool to 5.8.10
- upgrade: upgrade shiro to 1.10.1

## [3.6.50] 2022.11.08
- upgrade: replace captcha cache imp

## [3.6.49] 2022.11.08
- remove: remove SpringContextUtils
- upgrade: replace captcha cache imp

## [3.6.48] 2022.11.03
- fix: remove xss filter in filter config
- new: add XssStringDeserializer
- remove: change jsoup lib scope to provided

## [3.6.47] 2022.10.24
- downgrade: upgrade druid to 1.2.11
- upgrade: upgrade hutool to 5.8.9
- upgrade: upgrade shiro to 1.10.0

## [3.6.45] 2022.10.21
- upgrade: upgrade springboot to 2.7.5
- upgrade: upgrade druid to 1.2.13
- upgrade: upgrade bcprov to 1.72

## [3.6.44] 2022.10.06
- upgrade: upgrade hutool to 5.8.8
- upgrade: upgrade aliyun.oss to 3.15.2 

## [3.6.43] 2022.09.24
- upgrade: upgrade springboot to 2.7.4
- 
## [3.6.42] 2022.09.16
- upgrade: upgrade hutool to 5.8.7
- upgrade: upgrade druid to 1.2.12
- 注意: druid在这个版本设置了默认超时10s,查询超过这个时间会报错Communications link failure，详见[文档](https://github.com/alibaba/druid/releases/tag/1.2.12)
可以通过修改配置避免  
```ymal
datasource:
  druid:
  # 建立连接时连接超时时间 默认：10s,单位ms
  socket-timeout: 1000000
  # 建立连接时连接超时时间 默认：10s,单位ms
  connect-timeout: 1000000
```

## [3.6.41] 2022.09.16
- new: DynamicTableParamHelper.setParamDataSingle

## [3.6.40] 2022.09.13
- new: add dingtalk notify

## [3.6.39] 2022.09.09
- upgrade: upgrade MP to 3.5.2
- upgrade: upgrade weixin to 4.4.0
 
## [3.6.38] 2022.09.06
- upgrade: upgrade shiro and jsoup
- upgrade: upgrade hutool to 5.8.6

- ## [3.6.36] 2022.08.26
- new: add dingtalk login

## [3.6.35] 2022.08.23
- new: add apply in Query
 
## [3.6.34] 2022.08.18
- upgrade: upgrade springboot 2.7.3
- 
## [3.6.33] 2022.07.22
- new: add user post
- upgrade: upgrade springboot 2.7.2
- upgrade: upgrade hutool to 5.8.5

## [3.6.30] 2022.07.13
- enhance: enhance params search
- new: add uc bill

## [3.6.27] 2022.07.06
- upgrade: upgrade shiro to 1.9.1
- upgrade: upgrade jsoup to 1.15.2

## [3.6.26] 2022.06.30
- upgrade: upgrade hutool to 5.8.4
- new: jackson utils json to pojo support byte[]

## [3.6.25] 2022.06.27
- upgrade: upgrade springboot to 2.7.1
- upgrade: upgrade druid to 1.2.11
- upgrade: upgrade aliyun oss to 3.15.1

## [3.6.24] 2022.06.23
- fix: fix NoClassDefFoundError issue in shiro config
- new: add cache config
- new: split msg and uc

## [3.6.22] 2022.06.20
- new: add sys relation table
- fix: fix user update password issue
- fix: fix user login param default issue
- add: excel export support export params

## [3.6.17] 2022.06.16
- new: login support no params
- new: add params update

## [3.6.16] 2022.06.13
- new: add other oss support
- new: add find in set in query helper 
- upgrade: upgrade hutool to 5.8.3 
- new: add oss path support
- new: add excel export

## [3.6.7] 2022.06.02
- fix: fix shirodao getMenuIdListByUserId issue

## [3.6.4] 2022.06.01
- new: add BaseParamsService

## [3.6.3] 2022.05.30
- upgrade: upgrade hutool to 5.8.2 

## [3.6.2] 2022.05.28
- new: add FileBase64Form

## [3.6.0] 2022.05.26
<del>
- upgrade: upgrade druid to 1.2.10
</del>

- new: add AccessControl allowToken

## [3.5.9] 2022.05.23
- fix: fix params content format issue 

## [3.5.7] 2022.05.20
- upgrade: upgrade spring boot to 2.7.0
- upgrade: upgrade hutool to 5.8.1
- new: set detail exception msg im onex.exception-handler.detail-msg: true
- new: disable allow-circular-references
- upgrade: upgrade jsoup to 1.15.1

## [3.5.6] 2022.05.05
- upgrade: add login params
- upgrade: upgrade hutool to 5.8.0
- merge: merge websocket/msg/sched into sys

## [3.5.2] 2022.04.29
- upgrade: cros add Access-Control-Expose-Headers

## [3.4.3] 2022.04.22
- upgrade: upgrade springboot to 2.6.7
- upgrade: upgrade aliyun oss to 3.14.1

## [3.4.3] 2022.04.21
- new: TreeNodeUtils add method

## [3.4.2] 2022.04.18
- fix: fix sort item issue
- new: add log operation scope
- fix: fix search param bug

## [3.3.7] 2022.04.14
- upgrade: upgrade hutool to 5.8.0_M3
- upgrade: upgrade druid to 1.2.9
- upgrade: upgrade weixin to 4.3.0
- fix: fix exception print issue

## [3.3.6] 2022.04.12
- new: add sys module
- fix: fix schedule log without tenant code issue

## [3.3.1] 2022.04.10
- fix: fix user lock issue
- enhance: log exception state

## [3.3.0] 2022.04.08
- add: user add area_code
- enhance: simplify shiro code

## [3.2.5] 2022.04.06
- add: add page validator
- add: Query type in IdForm and IdsForm

## [3.2.0] 2022.04.06
- fix: coder issue
- add: tenant params
- add: getContent in params
- fix: menu scope issue

## [3.1.5] 2022.04.02
- fix: fix shiro filter issue
- fix: replace captcha to fix jdk version issue
- upgrade: upgrade hutool to 5.8.0.M2

## [3.1.3] 2022.04.01
- upgrade: upgrade springboot to 2.6.6

## [3.1.2] 2022.03.31
- fix: fix sched package name issue
- enhance: add api sort

## [3.1.0] 2022.03.29
- new: add uc module
- upgrade: upgrade huaweicloud obs to 3.22.3
- add: add AutoFillMetaObjectHandler
- add: add OnexExceptionHandler

## [3.0.9] 2022.03.29
- upgrade: upgrade springboot to 2.6.5
- enhance: shiro enhance

## [3.0.8] 2022.03.28
- new: add shiro into common
- enhance: optimize AccessControl shiro filter, support empty value
- upgrade: upgrade hutool to 5.8.0.M1
- remove: JwtShiroFilter/JwtTokenFilter

## [3.0.7] 2022.03.25
- enhance: remove WxErrorException from exception handler
- upgrade: upgrade shiro to 1.9.0

## [3.0.5] 2022.03.16
- enhance: enhance QueryWrapperHelper

## [3.0.4] 2022.03.15
- new: add QueryWrapperHelper
- new：change page fmt

## [3.0.3] 2022.03.14
- new: add app info

## [3.0.2] 2022.03.09
- fix: add contentType to logBody

## [3.0.0] 2022.03.08
- new: split WebSocket
- new: add OnexHttpServletRequestWrapper
- new: add BaseExceptionHandler
- fix: LogOperationAspect log get params

## [2.2.3] 2022.03.01
- upgrade: springboot to 2.6.4
- upgrade: hutool to 5.7.22

## [2.2.2] 2022.02.15
- upgrade: hutool to 5.7.21
- optimize: optimize file upload
- new: add upload base64
- new: add Base64FileUtils 

## [2.2.1] 2022.01.25
- upgrade: MybatisPlus to 3.5.1

## [2.2.0] 2022.01.24
- upgrade: aliyun oss to 3.14.0
- upgrade: springboot to 2.6.3
- upgrade: hutool to 5.7.20

## [2.1.39] 2022.01.18
- fix: 消息模板引擎指定问题
- fix: 阿里云/华为云短信发送问题
- new: 增加websocket长连接时间设置

## [2.1.34] 2022.01.10
- fix: AccessControl和LogOperation注解冲突,导致LogOperation切片过了两次的bug
- upgrade: hutool to 5.7.19
- upgrade: MybatisPlus to 3.5.1
