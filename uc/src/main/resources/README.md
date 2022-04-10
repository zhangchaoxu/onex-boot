# 用户管理模块

## 登录流程

1. 调用[登录参数]获得登录配置 传参为当前页面的url地址

```json
{
  "url": "https://xxx.nb6868.com"
}
```

返回信息为

```json
{
  "url": "https://xxx.nb6868.com",
  "type": "ADMIN_USERNAME_PASSWORD",
  "title": "XXX系统",
  "captcha": false,
  "register": false,
  "background": "",
  "tenantCode": "default",
  "forgetPassword": false
}
```

2. 若[登录参数]返回信息中captcha为true,调用[图形验证码(base64)]获得验证码base64和uuid

3. 调用[用户登录]获取用户信息和token

传参

```json
{
  "type": "ADMIN_USERNAME_PASSWORD",
  "password": "admin",
  "username": "admin",
  "tenantCode": "default"
}
```

其中type和tenantCode为[租户参数信息(通过URL)]返回信息中的type和tenantCode

4. 若使用[用户登录(登录)],则需要将将[登录]中的信息做AES加密，key为1234567890adbcde
如上述3中json作为字符串({"password":"admin","tenantCode": "xxx","type":"ADMIN_USERNAME_PASSWORD","username":"admin"}),加密后参数作为body

传参

```json
{
  "body": "bqJOFdO/IlOCMHJ6V+BDpyVlY1N5opy5uOrww4u/6huTIK7XB5WVAGiYflVn5AmzeLCpaiXQxUorBW3P05kexppCz3Y8uSi7W7NpWWWc7wY3OOT4aLKLZwylNiorEz5S"
}
```

返回信息中包含token和用户信息




