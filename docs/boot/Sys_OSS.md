# 文件存储

系统支持本地上传、阿里云OSS、华为云obs存储两种方式

## 本地上传
1. 将文件上传到应用服务器的某个路径
2. 将该路径保留在tomcat或者nginx中可以访问

## 阿里云OSS

### 应用服务器中转
1. 将文件上传到应用服务器
2. 由应用服务器将文件上传到阿里云OSS
2.1 方法1: 将OSS挂载到应用服务器所在linux,需要在同一区域的内网,实现方式同本地上传
2.2 方法2：调用OSS的接口,将文件上传到OSS,如果应用服务器和OSS在同一个区域,可以使用内网地址

### 阿里云OSS直传[STS方式](https://help.aliyun.com/document_detail/56286.html))
考虑使用应用服务器中转,需要耗费应用服务器的带宽,而且文件有两次传输过程,延时比较大,因此考虑在应用端直接将文件上传到OSS,这样直接消耗OSS的流量。    
如果直接将accessKeyId和accessKeySecuret暴露给应用端,非常不安全,一次采用STS临时授权的方式。

#### STS临时授权步骤
1. 应用端从应用服务器获取STS临时授权
2. 应用服务器从阿里云接口获取STS临时授权,然后返回给应用端
3. 应用端将临时授权保存本地,在有效期范围内不在重复获取STS
4. 应用端利用STS临时授权上传文件到OSS服务器

#### STS临时授权方式
1. 按照[STS临时授权访问OSS](https://help.aliyun.com/document_detail/100624.html))添加角色和授权
2. 按照[OSS Bucket设置](https://help.aliyun.com/document_detail/32069.html))在bucket的跨域设置中创建跨域规则：
```
-   将allowed origins设置成  `*`
-   将allowed methods设置成  `PUT, GET, POST, DELETE, HEAD`
-   将allowed headers设置成  `*`
-   将expose headers设置成  `etag`  `x-oss-request-id`
```
3. 服务器接口提供一个getSts接口
4. 前端使用阿里云[上传组件Browser.js](http://help.aliyun.com/document_detail/64041.html)上传文件

## 阿里云OSS的访问限制
对于上传到OSS的文件，往往需要对读取做一定的限制，常用方式如下：
1. OSS设置为公共读,限制允许访问的域名,只有在特定的域名下可以访问该文件,适用于文章方面、文章图片等对于私密性要求不高的文件    
做允许访问域名限制还有一个用途是阿里云的OSS访问是按流量付费的,防盗链。
2. OSS设置为私有读,在访问之前先从阿里云获取带有时效的访问路径,适用于用户上传的合同、身份证等对于私密性要求高的文件

## 业务接口上传文件
在
