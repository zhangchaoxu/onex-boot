# 地图的使用
地图引擎[高德JS API](https://lbs.amap.com/api/javascript-api/summary)
UI库[AMap UI组件库](https://lbs.amap.com/api/amap-ui/intro)

## 使用方法
1. 在public.html中根据需要引入js库和UI库
```
<!-- 高德地图JSAPI -->
<script type="text/javascript" src='//webapi.amap.com/maps?v=1.4.5&key=yourkey'></script>
<!-- 高德UI组件库 -->
<script src="//webapi.amap.com/ui/1.1/main.js"></script>
```

2. 在需要的地方按照需求调用

## 组件
1. 地图点查看器

2. 地图位置选择器

## Tips
0. JSAPI没有使用[JSAPI2.0](https://lbs.amap.com/api/jsapi-v2/summary)是因为相同加载方式下,2.0有非常明显的加载延迟。(20200804)

1. 初始化map所在的div,控制显示用v-show(控制是否显示),而不是v-if(控制是否存在) 
否则可能出现初始化map的时候不存在div id的问题,导致地图无法显示出来。

2. 在同一个页面中map的div id要有所区分   
比如在一个页面中既有全局地图展示,又有模态框中的地图点展示地图点选择,这个时候这三者的div id要有所区分。

