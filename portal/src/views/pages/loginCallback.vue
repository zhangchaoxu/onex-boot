<template>
  <el-card v-loading="formLoading" element-loading-text="登录跳转中..."/>
</template>

<script>
/**
 * 登录回调页面
 * 接收回调返回的参数(code、statue、source)
 * 其中source为传参
 */
export default {
  data () {
    return {
      formLoading: true
    }
  },
  created () {
    // 将回调结果返回给父窗口(登录页面),包括source code state
    // stringify是因为部分浏览器只支持string,不支持object
    window.opener.postMessage(JSON.stringify(this.$route.query), location.origin)
    // 关闭子窗口
    window.close()
  }
}
</script>
