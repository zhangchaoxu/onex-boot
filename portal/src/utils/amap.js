/**
 * 高德地图工具
 * see {https://elemefe.github.io/vue-amap/}
 */
import VueAMap from 'vue-amap'

/**
 * 初始化amap
 */
export function initAmap () {
  VueAMap.initAMapApiLoader({
    // 高德的key
    key: 'ddb5879c856aa44a3d93c677e0523769',
    // 插件集合
    plugin: ['AMap.Autocomplete', 'AMap.PlaceSearch', 'AMap.Scale', 'AMap.OverView', 'AMap.ToolBar', 'AMap.MapType'],
    // 高德 sdk 版本，默认为 1.4.4
    v: '1.4.15',
    // ui库版本，不配置不加载
    uiVersion: '1.0'
  })
}
