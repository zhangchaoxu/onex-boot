## 第三方授权和信息同步
在应用开发中，不可避免的会涉及到诸如微信、钉钉、QQ，或者是用户自建Oauth平台的登录和信息同步。      
为此在user表预留oauthUserid存第三方用户id，在dept表渔鸥oauthDeptid存第三方部门id。

### 数据同步
数据同步的过程中，逻辑上的坑很多，如果第三方的部门和用户等是唯一来源，并且只提供第三方第oauth登录还好一点，如果第三方的数据和自己的数据交叉在一起的，问题很多。      
列举一些可能存在的问题，有很多需要结合实际业务需求来判断和决定。
1. 从第三方同步过来的基本信息(比如部门、用户)，尽量不提供修改功能，否则每次同步的时候信息会被覆盖掉。
2. 同步的时候要校验必要的信息是否存在，第三方数据因为数据质量问题，很可能会缺少业务关键信息。
3. 同步的时候要对其它关联信息做校验，比如用户同步的时候，要检查用户相关的组织是否已存在；部门同步的时候，要检查上级部门是否已存在。
4. 钉钉傻逼接口没办法一次性获取部门或者用户的信息，需要自己按逻辑遍历，另外会有用户挂在根部门(deptId=1,实际是一个不存在的部门)的情况，需要小心处理，具体见DingtalkApi。
5. 第三方删除的数据，再次同步的时候往往无法再次拿到，业务逻辑需要做相应的逻辑处理，考虑在自己系统中做删除或者停用等操作处理。
6. 因为同步频率问题，业务系统的信息和第三方信息会有数据时差，比如通过code做oauth登录的时候，对应用户在第三方系统中存在，但在业务系统中不存在；因此需要在登录过长中做判断和信息补全。

<del>
## 微信小程序登录
小程序登录具体流程见[微信文档](https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html)
使用方法:
接口提供2个接口
`UserOauthController.oauthWxMaLoginByCodeAndUserInfo` 这样做的目的是为了避免SessionKey的传输和存储。
`UserOauthController.oauthWxMaLoginByCode`

小程序:
可以用户无感知的在页面中先使用[wx.login](https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html)获得code      
然后调用UserOauthController.oauthWxMaLoginByCode做登录,返回登录成功信息,或者用户信息不全/用户未绑定错误。          
若返回错误,则需要跳转到登录页面,在该登录页面先使用[wx.login],再由需用户手动触发getUserInfo类型的按钮，使用[wx.getUserInfo](https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html)获得微信用户详情，将code和用户信息一起作为参数调用UserOauthController.oauthWxMaLoginByCodeAndUserInfo。

## 钉钉扫码登录

扫码登录的实现方式有很多,以下展示一种使用钉钉官方提供的功能实现,结合了[使用钉钉账号登录第三方网站](https://open.dingtalk.com/document/orgapp-server/use-dingtalk-account-to-log-on-to-third-party-websites)和[实现登录第三方网站](https://open.dingtalk.com/document/orgapp-server/tutorial-obtaining-user-personal-information)

## 准备工作
1. 登录[钉钉开放平台](https://open-dev.dingtalk.com)
2. 创建微应用(H5)应用
3. 在微应用的登录和分享中添加回调地址,注意回调地址可以填写多个

# 前端开发步骤
1. 使用iframe嵌入一个地址,该地址内容为一个二维码
2. 扫码授权后,会
</del>

## 扫码登录

ref1. [IM 的扫码登录功能如何实现？一文搞懂主流的扫码登录技术原理](https://my.oschina.net/u/4231722/blog/3154805)




