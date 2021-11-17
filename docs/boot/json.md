# json解析器

json解析器使用的是jackson

## js中Long型数据精度丢失

JavaScript 无法处理 Java 的长整型 Long 导致精度丢失，具体表现为主键最后两位永远为 0 解决思路： Long 转为 String 返回 见WebMvcConfig.jackson2HttpMessageConverter [问题说明](https://mybatis.plus/guide/faq.html#id-worker-生成主键太长导致-js-精度丢失)

## jackson驼峰前面只有一个字母

