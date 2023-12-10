## Office支持
* 在管理系统中不可避免的会遇到各种Office类文件操作的需求，包括Excel导入、Excel导出、Word导出、PDF导出、文档预览等。
* 下面针对上述需求分别做实现方式的讨论。

## Excel的导入和导出
* 目前Java对于Excel的导入导出等操作，比较常见的是以下三个类库基于[Apache POI](https://poi.apache.org/)的二次封装的类库。
* [EasyPOI](https://gitee.com/lemur/easypoi): 更新不活跃，除了Excel,还支持Word等操作,功能接口丰富
* [EasyExcel](https://github.com/alibaba/easyexcel)：更新活跃，宣传处理效率快，内存占用少，实际测试并无明显区别，只支持Excel，功能接口一般
* [HutoolPOI](https://doc.hutool.cn/pages/poi/)：总体活跃度、功能完善度一般，但对于Excel的支持足够日常使用，并且接口封装简单，加上OneX大量使用Hutool，因此使用Hutool作为Excel的处理支持。

### Excel的导出


## Word模板导出
按照规定的模板内容格式，导出Word文件，比如一个订单信息，是业务中比较常见的一个需求。       
常见的解决思路有
1. 使用Html渲染出页面,然后使用html转word方法导出；   
- 优点：页面版式灵活
- 缺点：Word文件内容很容易变形

2. 设计好Word模板文件,使用占位符填入需要填入的内容,支持占位符、表格、图片、循环表格等。
- 优点：实现简单、可以较好的还原模板样式
- 缺点：模板文件很容易出诡异的问题,比如图片出不来,字体与预设的不符合等。对于复杂的文档，可以考虑文档拆分后合并的方式来处理。详见WordTplTest

## Excel导出

## Excel导入

## 导出Excel加序号
首先设置导出参数new ExportParams().setAddIndex(true);       
然后对Excel实体的注解加上orderNum为负数
参考[issue 关于增加序号列报错](https://gitee.com/lemur/easypoi/issues/I107KP?from=project-issue)

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

## Ref.
1. [EasyPOI Doc](http://doc.wupaas.com/docs/easypoi/)
2. [用 Java 实现 word、excel 等文档在线预览](https://mp.weixin.qq.com/s/kIuWL_UtYw5eKKYTN1zF9Q)

