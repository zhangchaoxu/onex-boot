# CHANGELOG

## [4.0.33] 2024.01.30
- upgrade: upgrade poi-tpk to 1.12.2

## [4.0.32] 2024.01.24
- change: change auth role id from string to long
- new: add wx template msg support
- upgrade: upgrade entityService to latest version
- change: change userMenuScope param and response

## [4.0.28] 2024.01.22
- upgrade: upgrade springboot to 3.2.2

## [4.0.27] 2024.01.17
- fix: change roleId from String to Long

## [4.0.26] 2024.01.17
- fix: fix job cron issue

## [4.0.25] 2024.01.15
- new: add DateNoMillisecondTypeHandler to fix mysql data millisecond issue

## [4.0.24] 2024.01.12
- fix: fix sql json join issue
- enhance: add sms hwcloud and aliyun status callback
- upgrade: upgrade hutool to 5.8.25

## [4.0.22] 2024.01.09
- upgrade: upgrade knife4j to 4.5.0

## [4.0.21] 2024.01.05
- upgrade: upgrade dynamic-datasource to 4.3.0
- enhance: enhance role code control in RBAC
- enhance: add file name validate in file upload

## [4.0.18] 2024.01.03
- upgrade: upgrade druid to 1.2.21

## [4.0.17] 2023.12.30
- fix: fix job run issue
- upgrade: upgrade jsoup to 1.17.2

## [4.0.16] 2023.12.29
- upgrade: upgrade aliyun-oss to 3.17.4
- upgrade: upgrade weixin sdk to 4.6.0

## [4.0.15] 2023.12.27
- fix: fix swagger header token issue
- new: add tunnel

## [4.0.11] 2023.12.26
- upgrade: upgrade mybatis-plus to 3.5.5, change maven repo to mybatis-plus-spring-boot3-starter
- fix: change mybatis-plus order item params

## [4.0.9] 2023.12.25
- upgrade: upgrade springboot to 3.2.1
- upgrade: upgrade hutool to 5.8.24

## [4.0.8] 2023.12.11
- upgrade: upgrade knife4j to 4.4.0

## [4.0.7] 2023.12.10
- upgrade: upgrade aliyun-oss to 3.17.3
- enhance: merge doc to common.yml
- change: change filter config yml define 

## [4.0.2] 2023.12.06
- upgrade: upgrade knife4j, and upgrade opanapi2 to openapi3
- upgrade: upgrade mybatis-spring to 3.0.3

## [4.0.0] 2023.11.29
- upgrade: upgrade springboot to 3.2.0, require Java 17 at least
- remove: remove easypoi
- remove: remove sched module
- change: replace javax with jakarta
- new: add poi into pom

## [3.8.3] 2023.11.29
- upgrade: upgrade jsoup to 2.17.1
- upgrade: upgrade poi to 5.2.5

## [3.8.1] 2023.11.24
- upgrade: upgrade springboot to 2.7.18

## [3.8.0] 2023.11.20
- upgrade: upgrade knife4j to 4.3.0

## [3.7.88] 2023.11.16
- upgrade: upgrade bcprov-jdk15to18 to 1.77

## [3.7.87] 2023.11.15
- upgrade: upgrade hutool to 5.8.23

## [3.7.86] 2023.11.07
- upgrade: upgrade mybatisplus to 3.5.4.1

## [3.7.85] 2023.11.06
- upgrade: upgrade shiro to 1.13.0

## [3.7.84] 2023.11.01
- upgrade: upgrade aliyun-oss to 3.17.2

## [3.7.83] 2023.10.30
- upgrade: upgrade springboot to 2.7.17
- new: add commons-io into common
- upgrade: upgrade mybatisplus to 3.5.4
- upgrade: upgrade jsoup to 1.16.2
- upgrade: upgrade huaweicloud.obs to 3.23.9.1

## [3.7.82] 2023.10.13
- fix: remove useless code

## [3.7.81] 2023.10.13
- enhance: add sms code white list

## [3.7.80] 2023.10.10
- enhance: replace pig4cloud captcha with hutool

## [3.7.77] 2023.10.10
- fix: remove useless code
- upgrade: upgrade druid to 1.2.20

## [3.7.76] 2023.10.07
- upgrade: upgrade poi-ooxml to 5.2.4
- upgrade: upgrade esdk-obs-java-bundle to 3.23.9

## [3.7.75] 2023.09.16
- upgrade: upgrade springboot to 2.7.16

## [3.7.73] 2023.09.16
- upgrade: upgrade hutool to 5.8.22

## [3.7.72] 2023.09.06
- upgrade: upgrade druid to 1.2.19

## [3.7.69] 2023.08.25
- upgrade: upgrade maven build jar to latest version
- enhance: add job config code prefix 

## [3.7.67] 2023.08.21
- upgrade: upgrade esdk-obs-java-bundle 3.23.5

## [3.7.66] 2023.08.17
- fix: fix oss upload issue 
- enhance: add uploadTemp method 

## [3.7.64] 2023.08.15
- enhance: add mobile to shiro user

## [3.7.63] 2023.08.14
- upgrade: upgrade mybatisplus to 3.5.3.2
- enhance: enhance ExcelExportParams

## [3.7.58] 2023.08.08
- enhance: add ExcelExportParams

## [3.7.57] 2023.08.04
- upgrade: upgrade hutool to 5.8.21
- upgrade: upgrade bcprov to 1.76

## [3.7.54] 2023.07.27
- enhance: enhance querywrapper, fix sort item camel case issue

## [3.7.53] 2023.07.24
- upgrade: upgrade springboot to 2.7.14
- upgrade: upgrade shiro to 1.12.0
- upgrade: upgrade aliyun-oss to 3.17.1
- change: change easypoi maven scope to provided 
- change: change druid connect and socket timout to 10000000ms    

## [3.7.52] 2023.06.30
- enhance: enhance querywrapper

## [3.7.51] 2023.06.25
- upgrade: upgrade springboot to 2.7.13
- upgrade: upgrade aliyun oss to 3.17.0
- upgrade: upgrade bcprov to 1.75

## [3.7.50] 2023.06.20
- upgrade: upgrade druid to 1.2.18
- upgrade: upgrade hutool to 5.8.20
- upgrade: upgrade bcprov to 1.74

## [3.7.46] 2023.05.08
- upgrade: upgrade esdk-obs-java-bundle 3.23.3
- upgrade: upgrade aliyun-sdk-oss 3.16.3

## [3.7.44] 2023.05.04
- upgrade: upgrade jsoup to 1.16.1

## [3.7.43] 2023.04.28
- fix: change AmapClient to AmapUtils, and change method to static

## [3.7.41] 2023.04.24
- upgrade: upgrade springboot to 2.7.11
- change: change GpsUtils

## [3.7.37] 2023.04.19
- upgrade: upgrade hutool to 5.8.18
- upgrade: upgrade bcprov-jdk15to18 to 1.73
- fix: fix swagger token header key issue

## [3.7.36] 2023.04.13
- upgrade: upgrade hutool to 5.8.17
- fix: add header Bearer prefix
- fix: JobRunResult add default storetype

- ## [3.7.28] 2023.03.28
- upgrade: upgrade aliyun-sdk-oss to 3.16.2

## [3.7.26] 2023.03.28
- fix: change role id to Long

## [3.7.25] 2023.03.27
- fix: fix jwt token verify issue

## [3.7.23] 2023.03.27
- upgrade: upgrade hutool to 5.8.16
- upgrade: upgrade springboot to 2.7.10

## [3.7.22] 2023.03.1
- new: add saveOrUpdateById in EntityService 
- new: add sse  

## [3.7.20] 2023.03.10
- upgrade: upgrade hutool to 5.8.15

## [3.7.19] 2023.03.06
- upgrade: upgrade hutool to 5.8.14

## [3.7.18] 2023.02.27
- upgrade: upgrade springboot to 2.7.9

## [3.7.17] 2023.02.20
- upgrade: upgrade aliyun-sdk-oss to 3.16.1
- upgrade: upgrade jsoup to 1.15.4
- upgrade: upgrade druid to 1.2.16

## [3.7.14] 2023.02.16
- upgrade: upgrade file-upload to 1.5
- upgrade: add log trace

## [3.7.12] 2023.02.14
- upgrade: upgrade hutool to 5.8.12
- enhance: enhance page method

## [3.7.10] 2023.02.03
- upgrade: upgrade huaweicloud obs to 3.22.12

## [3.7.9] 2023.01.30
- upgrade: add qrcode login

## [3.7.8] 2023.01.29
- upgrade: change cache define

## [3.7.7] 2023.01.28
- upgrade: upgrade springboot to 2.7.8

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

## [3.6.36] 2022.08.26
- new: add dingtalk login

## [3.6.35] 2022.08.23
- new: add apply in Query
 
## [3.6.34] 2022.08.18
- upgrade: upgrade springboot 2.7.3

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
