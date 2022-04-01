# 第三方登录
在应用开发中，不可避免的会涉及到诸如微信、钉钉、QQ等第三方平台的登录。            
增加uc_user_oauth表存储第三方用户信息，表中user_id与用户表做关联,用户可以有多个第三方用户信息绑定。
注意uc_user_oauth中的用户不可以直接使用,登录授权等都需要通过user表去做关联。

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

 
