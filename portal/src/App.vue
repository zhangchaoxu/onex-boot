<template>
  <transition name="el-fade-in-linear">
    <router-view/>
  </transition>
</template>

<script>
import Cookies from 'js-cookie'

export default {
  watch: {
    '$i18n.locale': 'i18nHandle'
  },
  created () {
    this.i18nHandle(this.$i18n.locale)
  },
  methods: {
    i18nHandle (val, oldVal) {
      Cookies.set('language', val)
      document.querySelector('html').setAttribute('lang', val)
      // 显示标题
      // document.title = messages[val].brand.lg
      document.title = Cookies.get('title') || 'loading'
      // 非登录页面，切换语言刷新页面
      if (this.$route.name !== 'login' && oldVal) {
        window.location.reload()
      }
    }
  }
}
</script>
