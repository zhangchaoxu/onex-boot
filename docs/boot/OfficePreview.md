# 文档在线预览
业务当中有需求，需要使用到文档在线预览

## 选项1-WPS在线预览
在EasyPoi中实现了[WPS预览支持](http://doc.wupaas.com/docs/easypoi/easypoi-1c3ah4kmad4k1)

### 大致流程：
1. 访问easypoi-preview.html
2. 页面中ajax访问v1/3rd/file/getViewUrl,获得一个wpsUrl地址
3. 在页面中访问这个wpsUrl, wps会在服务器访问getDownLoadUrl中的地址获得文件，同时/v1/3rd/file/info获得文件元信息
4. wps在服务器端调用/v1/3rd/onnotify接口通知谁在使用
5. 展示文件

### 实际使用过程,需要注意：
1. 申请开发应用中回调地址为系统的地址,也就是开放回调接口/v1/3rd/file/info和/v1/3rd/onnotify的地址
2. 实现WpsService
2.1 将申请得到的AppId和AppSecret写入
2.2 getDownLoadUrl为WPS可访问到文件的地址
3. 获取文件元数据等接口返回数据的int还是string,一定要严格按照[文档](https://wwo.wps.cn/docs/server/callback-api-standard/get-file-metadata/)来,否则会提示获取文件信息失败
3.1 create_time和modify_time实际发现毫秒和秒都无所谓，但是的是json的number格式,在很多时候为了避免long的精度丢失,会对long做string处理，在这里会出错
4. 需要访问easypoi的部分接口和preview.html页面，注意授权和路径问题
5. *重点*: [太贵了](https://wwo.wps.cn/docs/introduce/billing-instructions/billing-method/),放弃


## ref
[用 Java 实现 word、excel 等文档在线预览](https://mp.weixin.qq.com/s/kIuWL_UtYw5eKKYTN1zF9Q)
