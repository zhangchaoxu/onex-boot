<template>
    <div v-loading.fullscreen.lock="loading" :element-loading-text="$t('loading')" :class="['aui-wrapper', { 'aui-sidebar--fold': $store.state.sidebarFold }]">
        <template v-if="!loading">
            <main-navbar/>
            <main-sidebar/>
            <div class="aui-content__wrapper">
                <main-content v-if="!$store.state.contentIsNeedRefresh"/>
            </div>
        </template>
    </div>
</template>

<script>
import MainNavbar from './main-navbar'
import MainSidebar from './main-sidebar'
import MainContent from './main-content'
import debounce from 'lodash/debounce'
import Cookies from 'js-cookie'

export default {
  components: { MainNavbar, MainSidebar, MainContent },
  data () {
    return {
      // websocket
      websocket: null,
      loading: true
    }
  },
  watch: {
    $route: 'routeHandle'
  },
  mounted () {
    // WebSocket
    this.initWebsocket()
  },
  provide () {
    return {
      // 刷新
      refresh () {
        this.$store.state.contentIsNeedRefresh = true
        this.$nextTick(() => {
          this.$store.state.contentIsNeedRefresh = false
        })
      }
    }
  },
  created () {
    this.windowResizeHandle()
    this.routeHandle(this.$route)
    Promise.all([
      this.getUserInfo(),
      this.getParamCfg()
      // this.getPermissions()
    ]).then(() => {
      this.loading = false
    })
  },
  methods: {
    // 窗口改变大小
    windowResizeHandle () {
      this.$store.state.sidebarFold = document.documentElement['clientWidth'] <= 992 || false
      window.addEventListener('resize', debounce(() => {
        this.$store.state.sidebarFold = document.documentElement['clientWidth'] <= 992 || false
      }, 150))
    },
    // 路由, 监听
    routeHandle (route) {
      if (!route.meta.isTab) {
        return false
      }
      let tab = this.$store.state.contentTabs.filter(item => item.name === route.name)[0]
      if (!tab) {
        // tab中不存在,添加
        tab = {
          ...window.SITE_CONFIG['contentTabDefault'],
          ...route.meta,
          'name': route.name,
          'params': { ...route.params },
          'query': { ...route.query }
        }
        this.$store.state.contentTabs = this.$store.state.contentTabs.concat(tab)
      } else {
        // tab中已存在
        if (tab.query !== route.query) {
          // query参数发生变化需要修改
          tab.query = route.query
        }
      }
      // 设置菜单当前项
      this.$store.state.sidebarMenuActiveName = tab.menuId
      this.$store.state.contentTabsActiveName = tab.name
    },
    // 获取当前用户信息
    getUserInfo () {
      return this.$http.get('/uc/user/userInfo').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.$store.state.user = res.data
      }).catch(() => {
      })
    },
    // 初始化websocket
    initWebsocket () {
      if (!this.$hasRole('emes-admin')) {
        return
      }
      if ('WebSocket' in window) {
        this.websocket = new WebSocket(window.SITE_CONFIG['wsURL'] + '/1')
        // 连接错误
        this.websocket.onerror = function () {
          console.log('WebSocket连接发生错误   状态码：' + this.websocket.readyState)
        }
        // 连接成功
        this.websocket.onopen = function () {
          console.log('WebSocket连接成功    状态码：' + this.websocket.readyState)
        }
        // 收到消息的回调
        this.websocket.onmessage = function (event) {
          // 根据服务器推送的消息做自己的业务处理
          console.log('服务端返回：' + event.data)
          let message = JSON.parse(event.data)
          this.$notify({
            title: message.title,
            message: message.message,
            type: message.type === 'leakAlarm' ? 'warning' : 'info'
          })
        }
        // 连接关闭的回调
        this.websocket.onclose = function () {
          console.log('WebSocket连接关闭    状态码：' + this.websocket.readyState)
        }
        // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
        window.onbeforeunload = function () {
          this.websocket.close()
        }
      } else {
        this.$notify.error({
          title: '提示',
          message: '当前浏览器不支持WebSocket'
        })
      }
    },
    // 获取系统配置
    getParamCfg () {
      return this.$http.get(`/sys/param/getContentByCode?code=SYS_CFG`).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        Cookies.set('title', res.data.title)
        Cookies.set('titleBrand', res.data.titleBrand)
        Cookies.set('titleBrandMini', res.data.titleBrandMini)
        document.title = res.data.title
      }).catch(() => {})
    }
    // 获取按钮权限
    /* getPermissions () {
          return this.$http.get('/uc/menu/permissions').then(({ data: res }) => {
            if (res.code !== 0) {
              return this.$message.error(res.toast)
            }
            window.SITE_CONFIG['permissions'] = res.data
          }).catch(() => {})
        } */
  }
}
</script>
