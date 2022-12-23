## 日历
在日常业务需求中，会遇到判断日期是否法定工作日，以及计算当日薪资倍数的情况。
在实际需求中,经常需要判断该日期是否工作日/休息日的需求,需要结合该年的节假日安排与调休。       
因此设置了calendar模块，保存该日期是否节假日，甚至薪资倍数。

## 设计思路
提供日历(calendar)表    
日历表中记录日期、星期、等信息    

## 使用
1. 数据库中导入[sys_calendar.sql](https://onex.nb6868.com/sql/sys_calendar.sql)
   
2. 日历模块集成在sys模块中，若需使用，引入sys依赖
```xml
<dependency>
    <groupId>com.nb6868.onex</groupId>
    <artifactId>sys</artifactId>
    <version>${onex.version}</version>
</dependency>
```

3. 按业务需求使用相关功能，如判断某一日是否工作日CalendarService.isWorkday()

## 数据来源
节假日数据来源[免费节假日 API](http://timor.tech/api/holiday/)
农历数据，TODO

## 数据范围
2010年-2023年
