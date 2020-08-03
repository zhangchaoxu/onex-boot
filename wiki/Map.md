# 地图的使用
地图使用的是[高德JSAPI](https://lbs.amap.com/api/javascript-api/summary)

## 使用方法
1. 在public.html中根据需要引入js库和UI库
```
<!-- 高德地图JSAPI -->
<script type="text/javascript" src='//webapi.amap.com/maps?v=1.4.15&key=&plugin=AMap.PlaceSearch'></script>
<!-- 高德UI组件库 -->
<script src="//webapi.amap.com/ui/1.1/main.js"></script>
```

2. 在需要的地方按照需求调用

## Tips
1. 初始化map所在的div,控制显示用v-show(控制是否显示),而不是v-if(控制是否存在) 
否则可能出现初始化map的时候不存在div id的问题,导致地图无法显示出来。

2. 在同一个页面中map的div id要有所区分   
比如在一个页面中既有全局地图展示,又有模态框中的地图点展示地图点选择,这个时候这三者的div id要有所区分。

