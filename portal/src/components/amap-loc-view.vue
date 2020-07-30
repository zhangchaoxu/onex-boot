<template>
    <i class="el-icon-map-location vue-map" @click="visible = true">
        <el-dialog :title="title" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen">
            <div slot="title">
                <span class="el-dialog__title">{{ title }}</span>
                <button type="button" class="el-dialog__headerbtn" style="right: 50px;" @click="fullscreen = !fullscreen"><i class="el-dialog__close el-icon el-icon-full-screen"/></button>
            </div>
            <div class="vue-map__content" v-if="visible">
                <div class="vue-map__content-box">
                    <!-- 地图 -->
                    <div id="map__container" class="vue-map__content-container" tabindex="0"/>
                </div>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="closeHandle()">{{ $t('close') }}</el-button>
            </div>
        </el-dialog>
    </i>
</template>

<script>
export default {
  // 参考https://gitee.com/smallweigit/avue-plugin-map
  name: 'amap-loc-view',
  // 参数
  props: {
    poi: {
      type: Object,
      default: () => {
        return {
          lng: 0,
          lat: 0,
          address: ''
        }
      }
    }
  },
  data () {
    return {
      // 是否可见
      visible: false,
      title: '位置查看',
      // 全屏
      fullscreen: false,
      // 地图与标记点
      marker: null,
      map: null
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
                if (!this.poi.address) {
                  // 没有地址,解析marker点地址
                  this.getAddress(this.poi.lng, this.poi.lat)
                } else {
                  this.marker.setContent(this.getMarkerContent(this.poi.address)) // 更新点标记内容
                }
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
      window.AMap.service('AMap.Geocoder', () => {
        // 回调函数
        new window.AMap.Geocoder({}).getAddress([lng, lat], (status, result) => {
          console.log(result)
          if (status === 'complete' && result.info === 'OK' && result.regeocode) {
            this.marker.setContent(this.getMarkerContent(result.regeocode.formattedAddress)) // 更新点标记内容
          } else {
            self.$message.error('地址解析失败')
          }
        })
      })
    },
    // 渲染marker点图标和内容
    getMarkerContent (html, markerImgSrc) {
      if (!markerImgSrc) {
        markerImgSrc = '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png'
      }
      let markerContent = document.createElement('div')
      // 点标记中的图标
      let markerImg = document.createElement('img')
      markerImg.src = markerImgSrc
      markerContent.appendChild(markerImg)

      // 点标记中的文本
      let markerSpan = document.createElement('span')
      markerSpan.className = 'vue-map__marker'
      markerSpan.innerHTML = html
      markerContent.appendChild(markerSpan)
      return markerContent
    },
    // 关闭时的回调
    closeHandle () {
      this.visible = false
    },
    // 初始化
    init (callback) {
      // 定义地图
      this.map = new window.AMap.Map('map__container', {
        zoom: 13,
        center: (() => {
          if (this.isValidLngLat()) {
            return [this.poi.lng, this.poi.lat]
          }
        })()
      })
      callback()
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
            &-box {
                position: relative;
            }
            &-container {
                width: 100%;
                height: 450px;
            }
        }
    }
</style>
