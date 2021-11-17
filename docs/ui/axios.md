# 接口调用

以下描述都是在html中如果调用后台接口数据,使用[axios](https://github.com/axios/axios实现),具体实现代码为utils/http.js

## 配置接口地址

在public/index.html中配置各个运行环境中连接的接口地址

## 创建axios实例

`axios.create`并且指定接口地址和超时时间

## 请求拦截

使用`axios.interceptors.request.use`对请求先做拦截，做一些比如加入token等操作

## 想要拦截

使用`axios.interceptors.response.use`对获得的相应数据做处理,比如判断401

