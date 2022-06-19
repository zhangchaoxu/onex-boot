# CHANGELOG

## [3.6.18] 2022.06.16
- new: add sys relation table

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
