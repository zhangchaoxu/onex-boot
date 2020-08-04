<template>
    <el-button icon="el-icon-map-location vue-map" @click="visible = true">
        <el-dialog :title="title" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen"
                   custom-class="el-dialog-no-padding">
            <div slot="title">
                <span class="el-dialog__title">{{ title }}</span>
                <button type="button" class="el-dialog__headerbtn" style="right: 50px;" @click="fullscreen = !fullscreen"><i class="el-dialog__close el-icon el-icon-full-screen"/></button>
            </div>
            <div class="vue-map__content" v-if="visible">
                <!-- 搜索框 -->
                <el-input id="map__input" size="small" v-model="searchText" clearable placeholder="输入关键字选取地点"/>
                <div class="vue-map__content-box">
                    <!-- 地图 -->
                    <div id="map__container__loc-pick" class="vue-map__content-container" tabindex="0"/>
                    <!-- 搜索结果 -->
                    <div id="map__result" class="vue-map__content-result"/>
                </div>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
            </div>
        </el-dialog>
    </el-button>
</template>

<script>
export default {
  // 参考https://gitee.com/smallweigit/avue-plugin-map
  name: 'amap-loc-pick',
  // 参数
  props: {
    // 请求码
    requestCode: {
      type: String,
      default: null
    },
    poi: {
      type: Object,
      default: () => {
        return {
          lng: 0,
          lat: 0
        }
      }
    }
  },
  data () {
    return {
      // 是否可见
      visible: false,
      title: '位置选择',
      // 全屏
      fullscreen: false,
      // 搜索关键词
      searchText: '',
      // 地图与标记点
      marker: null,
      map: null,
      // poi 结果
      poiResult: {}
    }
  },
  watch: {
    // 监控visible
    visible: {
      handler () {
        if (this.visible) {
          this.$nextTick(() =>
            this.init(() => {
              if (this.isValidLngLat()) {
                // 存在经纬度,添加marker点
                this.addMarker(this.poi.lng, this.poi.lat)
                // 解析marker点地址
                this.getAddress(this.poi.lng, this.poi.lat)
              }
            })
          )
        }
      },
      immediate: true
    }
  },
  methods: {
    isValidLngLat () {
      return this.poi && this.poi.lat && this.poi.lng && this.poi.lat > 0 && this.poi.lng > 0
    },
    // 新增坐标
    addMarker (lng, lat) {
      // 清空poi picker
      if (window.poiPicker) {
        window.poiPicker.clearSearchResults()
      }
      this.clearMarker()
      this.marker = new window.AMap.Marker({ position: [lng, lat] })
      this.marker.setMap(this.map)
    },
    // 清空坐标
    clearMarker () {
      if (this.marker) {
        this.marker.setMap(null)
        this.marker = null
      }
    },
    // 获取坐标
    getAddress (lng, lat) {
      let self = this
      window.AMap.plugin('AMap.Geocoder', () => {
        new window.AMap.Geocoder({}).getAddress([lng, lat], (status, result) => {
          if (status === 'complete' && result.info === 'OK' && result.regeocode && result.regeocode.addressComponent) {
            let adcode = result.regeocode.addressComponent.adcode
            let provinceCode = adcode.substring(0, 2) + '0000'
            let citycode = adcode.substring(0, 4) + '00'
            // 赋值poiResult
            self.poiResult = {
              lat: lat,
              lng: lng,
              regionName: result.regeocode.addressComponent.province + ',' + result.regeocode.addressComponent.city + ',' + result.regeocode.addressComponent.district,
              regionCode: provinceCode + ',' + citycode + ',' + adcode,
              citycode: result.regeocode.addressComponent.citycode,
              address: result.regeocode.addressComponent.township + result.regeocode.addressComponent.street + result.regeocode.addressComponent.streetNumber
            }
            // 自定义点标记内容
            let markerContent = document.createElement('div')
            // 点标记中的图标
            let markerImg = document.createElement('img')
            markerImg.src = '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png'
            markerContent.appendChild(markerImg)

            // 点标记中的文本
            let markerSpan = document.createElement('span')
            markerSpan.className = 'vue-map__marker'
            // markerSpan.innerHTML = result.regeocode.formattedAddress
            markerSpan.innerHTML = this.poiResult.address
            markerContent.appendChild(markerSpan)
            self.marker.setContent(markerContent) // 更新点标记内容
          } else {
            self.clearMarker()
            self.poiResult = {}
            self.$message.error('地址解析失败')
          }
        })
      })
    },
    // 关闭时的回调
    closeHandle () {
      this.searchText = ''
      window.poiPicker.clearSearchResults()
      this.poiResult = {}
    },
    dataFormSubmitHandle () {
      // 验证通过,提交表单
      this.$emit('onLocPicked', this.poiResult, this.requestCode)
      this.poiResult = {}
      this.visible = false
    },
    // 地图点击事件
    addMapClickEvent () {
      this.map.on('click', e => {
        if (e.lnglat) {
          this.addMarker(e.lnglat.lng, e.lnglat.lat)
          this.getAddress(e.lnglat.lng, e.lnglat.lat)
        }
      })
    },
    // 初始化
    init (callback) {
      // 定义地图
      this.map = new window.AMap.Map('map__container__loc-pick', {
        zoom: 13,
        center: (() => {
          if (this.isValidLngLat()) {
            return [this.poi.lng, this.poi.lat]
          }
        })()
      })
      // 初始化poi选择器
      this.initPoiPicker()
      // 添加点击事件
      this.addMapClickEvent()
      callback()
    },
    // 初始化poi选择器
    initPoiPicker () {
      window.AMapUI.loadUI(['misc/PoiPicker'], PoiPicker => {
        const poiPicker = new PoiPicker({
          input: 'map__input',
          placeSearchOptions: {
            map: this.map,
            pageSize: 10
          },
          searchResultsContainer: 'map__result'
        })
        // 初始化poiPicker
        window.poiPicker = poiPicker
        // 选取了某个POI
        poiPicker.on('poiPicked', result => {
          this.clearMarker()
          this.searchText = result.item.name
          // poiResult.source 为suggest或者search
          if (result.source !== 'search') {
            poiPicker.searchByKeyword(this.searchText)
          }
          if (result.item && result.item.location) {
            let adcode = result.item.adcode
            let provinceCode = adcode.substring(0, 2) + '0000'
            let citycode = adcode.substring(0, 4) + '00'
            this.poiResult = {
              lat: result.item.location.lat,
              lng: result.item.location.lng,
              regionName: result.item.pname + ',' + result.item.cityname + ',' + result.item.adname,
              regionCode: provinceCode + ',' + citycode + ',' + adcode,
              address: result.item.address,
              citycode: result.item.citycode
            }
          }
        })
      })
    }
  }
}
</script>

<style lang="scss">
    .amap-icon img,
    .amap-marker-content img {
        width: 25px;
        height: 34px;
    }
    .vue-map {
        &__submit {
            width: 100%;
        }
        &__marker {
            position: absolute;
            top: -20px;
            right: -118px;
            color: #fff;
            padding: 4px 10px;
            box-shadow: 1px 1px 1px rgba(10, 10, 10, 0.2);
            white-space: nowrap;
            font-size: 12px;
            background-color: #25a5f7;
            border-radius: 3px;
        }
        &__content {
            &-input {
                margin-bottom: 10px;
            }
            &-box {
                position: relative;
            }
            &-container {
                width: 100%;
                height: 450px;
            }
            &-result {
                display: block !important;
                position: absolute;
                top: 0;
                right: -8px;
                width: 250px;
                height: 450px;
                overflow-y: auto;
            }
        }
    }
</style>
