# 数据加密

## 接口加密

### 目的

为了防止数据在传输过程中被嗅探监听,应该避免数据,特别是敏感数据(比如登录密码)的明文传输。常见的方法有

* SSL,采用https方式,在传输层做数据加密
* 对接口传参,甚至是接口返回内容做加解密(比如AES)
* 加入签名

## 以登录过程为例子描述接口传参加密：

1. 前端加密例子

```javascript
this.dataFormSubmitParam = encodeURIComponent(aesEncrypt(JSON.stringify(this.dataForm)))
```

将明文(请求数据)
```json
{"username":"admin","password":"123456","uuid":"","captcha":"","type":10}
```
使用Utils.aesEncrypt做加密,得到密文
```text
QkNOJMWRfP+DQ0whS6zmXmYW0HkLLbeSzAEypciCpOVr+aoleWoKR2AGwJIF9htskXlPZvlvNiwvqYtJqopnYWnfPGFkWHhjafhYnUJ1lDI=
```
然后提交给接口(注意加密后数据会有特殊字段,需UrlEncode)

2. 后端解密 对提交的密文做解密,得到原始请求数据的明文,再做业务处理。 同样注意需要对接收到的密文做UrlDecode

```java
/**
* AES解密登录
* 支持帐号登录、短信登录
*/
@PostMapping("loginEncrypt")
@ApiOperation(value = "帐号登录AES加密")
public Result<?> loginEncrypt(HttpServletRequest request, @RequestBody String loginEncrypted) throws UnsupportedEncodingException {
 // 密文转json明文
 String loginRaw = AESUtils.decrypt(URLDecoder.decode(loginEncrypted, "utf-8"));
 // json明文转实体
 LoginRequestDTO login = JacksonUtils.jsonToPojo(loginRaw, LoginRequestDTO.class);
 // todo 业务逻辑
 return new Result<>().ok();
}
```

3. 注意 过程中加解密密钥一致、算法一致。

#### todo 优化

使用ApiEncrpty注解,标明是否加解密参数。然后使用Springboot的AOP做统一处理。 [参考](https://blog.csdn.net/weixin_44505962/article/details/101231330)

### 签名加密

除了上述将整个请求做AES加密以外,还可以额外增加一个sign参数,将所有请求参数按照某种规则(比如ASCII升序)排序拼接,有必要的话再加上时间戳做私钥加密。 在后端重新做一次加密,然后比对sign是否一致。

## ps. 是否有必要

因为加密密钥也是暴露在前端的,因此盗取者可以比较容易的通过密钥获取到明文,有点类似把门锁上,但是把钥匙放在门口。  
在这里不做更多讨论,有兴趣可以关注[Web 前端密码加密是否有意义?](https://www.zhihu.com/question/25539382)

