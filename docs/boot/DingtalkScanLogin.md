# 钉钉扫码登录

扫码登录的实现方式有很多,以下展示一种使用钉钉官方提供的功能实现,结合了[使用钉钉账号登录第三方网站](https://open.dingtalk.com/document/orgapp-server/use-dingtalk-account-to-log-on-to-third-party-websites)和[实现登录第三方网站](https://open.dingtalk.com/document/orgapp-server/tutorial-obtaining-user-personal-information)

## 准备工作
1. 登录[钉钉开放平台](https://open-dev.dingtalk.com)
2. 创建微应用(H5)应用
3. 在微应用的登录和分享中添加回调地址,注意回调地址可以填写多个

# 前端开发步骤
1. 使用iframe嵌入一个地址,该地址内容为一个二维码
2. 扫码授权后,会
