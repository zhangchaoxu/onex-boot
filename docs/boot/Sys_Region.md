# 地理区域相关功能
在系统开发中,有许多功能都与地理区域相关,包括所在省市区镇四级信息以及经纬度等信息。

## 行政区域
国内的行政区域数据比较复杂,公开数据会存在不准确和不及时等问题,目前使用[AreaCity-JsSpider-StatsGov](https://github.com/xiangyuecn/AreaCity-JsSpider-StatsGov/)提供的四级区域数据与三级区域边界数据。
注意1. 未找到公开免费的四级乡镇的经纬度数据
注意2. 行政区域会存在变更等情况

### ps
1.搜索周边范围mysql实现
ROUND(6378.138*2*ASIN(SQRT(POW(SIN((${lat}*PI()/180-lat*PI()/180)/2),2)+COS(${lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((${lng}*PI()/180-lng*PI()/180)/2),2)))*1000) as distance
