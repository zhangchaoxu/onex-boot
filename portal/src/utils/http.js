import Vue from 'vue'
import axios from 'axios'
import Cookies from 'js-cookie'
import qs from 'qs'
import { redirectLogin } from '@/utils'
import isPlainObject from 'lodash/isPlainObject'

const http = axios.create({
  baseURL: process.env.VUE_APP_API_URL,
  timeout: 1000 * 180,
  withCredentials: true
})

/**
 * 请求拦截
 */
http.interceptors.request.use(config => {
  config.headers['Accept-Language'] = Cookies.get('language') || 'zh-CN'
  config.headers['token'] = Cookies.get('token') || ''
  // 默认参数
  const defaults = {}
  // 防止缓存，GET请求默认带_t参数
  if (config.method === 'get') {
    config.params = {
      ...config.params,
      ...{ '_t': new Date().getTime() }
    }
  }
  if (isPlainObject(config.params)) {
    config.params = {
      ...defaults,
      ...config.params
    }
  }
  if (isPlainObject(config.data)) {
    config.data = {
      ...defaults,
      ...config.data
    }
    if (/^application\/x-www-form-urlencoded/.test(config.headers['content-type'])) {
      config.data = qs.stringify(config.data)
    }
  }
  return config
}, error => {
  console.log(error)
  Vue.prototype.$message.error('接口请求异常')
  return Promise.reject(error)
})

/**
 * 响应拦截
 */
http.interceptors.response.use(response => {
  if (response.data.code === 401) {
    // 清空登录信息，跳转登录页面
    redirectLogin()
    return Promise.reject(response.data.msg)
  }
  // 设置显示用消息
  response.data.toast = response.data.code + ':' + response.data.msg
  return response
}, error => {
  console.log(error)
  Vue.prototype.$message.error('接口响应异常')
  return Promise.reject(error)
})

export default http
