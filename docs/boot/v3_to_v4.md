# v3升级v4
为了分别支持Spring2+和Spring3.2+，将onex分成v3和v4两个分支。

## 区别
| 区别项        |  v3   |   v4 |
|:-----------|:-----|:-----|
| SpringBoot |  2+   | 3.2+ |
| JDK        | 1.8+  |  17+ |
| Java扩展包    | javax |  jakarta |
| 接口规范       | springfox 2.10.5 |  OpenAPI3 |

## OneX具体改动
首先，v3升级v4所有的改动都是因为SpringBoot的升级引起的。
1. 将依赖的SpringBoot版本升级到的了3.2+
2. 因SpringBoot3.2+最低要求JDK17，因此将编译的JDK版本改到17+
3. 因JDK9之后,jdk移除了javax的包，一次，需要手动将javax替换成jakarta，主要是jakarta.servlet, jakarta.annotation
4. 部分依赖的第三方需要更新为主要是jakarta版本，比如shiro
5. 将knife4j升级到最新的支持版本，本质是将接口规范从springfox 2升级到了OpenAPI3，原先的@API替换为@Tag，原先的@ApiModel和@ApiModelProperty替换为@Schema

## 使用者需要改动
1. 将Onex的版本依赖从v3升级到v4
```xml
<dependency>
    <groupId>com.nb6868.onex</groupId>
    <artifactId>common</artifactId>
    <version>4.0.0</version>
</dependency>
```
2. 检查第三方依赖是否有需要做修改的内容
3. 检查代码中是否有javax的调用，替换成jakarta
4. 检查Controller、DTO中中对于@API替换为@Tag，@ApiModel和@ApiModelProperty替换为@Schema
5. onex.yml中可以删除onex.swagger中其它内容的配置，配置定义从SwaggerConfig迁移到yml配置文档中
