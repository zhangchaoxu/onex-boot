<template>
  <div class="aui-wrapper aui-page__login">
    <div class="aui-content__wrapper">
      <main class="aui-content">
        <div class="login-header">
          <h2 class="login-brand" v-html="sysCfg.loginTitle"/>
        </div>
        <el-card class="login-body">
          <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" status-icon :validate-on-rule-change="false" @keyup.enter.native="dataFormSubmitHandle()">
            <el-form-item>
              <el-radio-group v-model="dataForm.type" size="small" @change="typeChangeHandle">
                <el-radio-button :label="10" v-if="loginCfg.loginByUsernameAndPassword">帐号登录</el-radio-button>
                <el-radio-button :label="30" v-if="loginCfg.loginByMobileAndSmsCode">手机号登录</el-radio-button>
              </el-radio-group>
            </el-form-item>
            <!-- 帐号密码登录 -->
            <template v-if="dataForm.type === 10">
              <el-form-item prop="username">
                <el-input v-model="dataForm.username" prefix-icon="el-icon-user" :placeholder="$t('username')"/>
              </el-form-item>
              <el-form-item prop="password">
                <el-input v-model="dataForm.password" prefix-icon="el-icon-lock" :placeholder="$t('password')" show-password/>
              </el-form-item>
              <el-form-item prop="captcha" v-if="loginChannelCfg.captcha">
                <el-input v-model="dataForm.captcha" prefix-icon="el-icon-c-scale-to-original" :placeholder="$t('captcha')" maxlength="8">
                  <el-tooltip slot="append" effect="dark" content="点击刷新图形验证码" placement="right">
                    <el-image :src="captchaImage" @click="getCaptcha()" style="width: 90px;">
                      <div slot="placeholder" class="image-slot"><i class="el-icon-loading"/></div>
                    </el-image>
                  </el-tooltip>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="dataFormSubmitHandle()" class="w-percent-100">{{ $t('login') }}</el-button>
              </el-form-item>
            </template>
            <!-- 手机号登录 -->
            <template v-else-if="dataForm.type === 30">
              <el-form-item prop="mobile">
                <el-input v-model="dataForm.mobile" :placeholder="$t('mobile')" prefix-icon="el-icon-mobile-phone" maxlength="11" minlength="11" class="input-with-select">
                  <template slot="prepend">+86</template>
                </el-input>
              </el-form-item>
              <el-form-item prop="smsCode">
                <el-input v-model="dataForm.smsCode" :placeholder="$t('smsCode')" prefix-icon="el-icon-message" maxlength="6" minlength="4">
                  <el-button slot="append" @click="smsCodeSendHandle()" :disabled="smsSendTimeout < 60">
                    {{ smsSendTimeout !== 60 ? $t('resendSmsCode', { 'sec': smsSendTimeout }) : $t('sendSmsCode') }}
                  </el-button>
                </el-input>
              </el-form-item>
              <el-form-item prop="captcha" v-if="loginChannelCfg.captcha">
                <el-input v-model="dataForm.captcha" prefix-icon="el-icon-c-scale-to-original" :placeholder="$t('captcha')">
                  <el-tooltip slot="append" effect="dark" content="点击刷新图形验证码" placement="right">
                    <el-image :src="captchaImage" @click="getCaptcha()" style="width: 90px;">
                      <div slot="placeholder" class="image-slot"><i class="el-icon-loading"/></div>
                    </el-image>
                  </el-tooltip>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="dataFormSubmitHandle()" class="w-percent-100">{{ $t('login') }}</el-button>
              </el-form-item>
            </template>
          </el-form>
          <el-divider v-if="loginCfg.loginByWechatScan || loginCfg.loginByDingtalkScan">第三方登录</el-divider>
          <div>
            <el-link :underline="false" @click="onOauthLogin('WECHAT_SCAN')" title="微信" v-if="loginCfg.loginByWechatScan" class="no-underline">
              <i class="ad-icon-wechat-fill" style="font-size: 24px; margin-left: 12px; margin-right: 12px;"/>
            </el-link>
            <el-link :underline="false" @click="onOauthLogin('DINGTALK_SCAN')" title="钉钉" v-if="loginCfg.loginByDingtalkScan" class="no-underline">
              <i class="ad-icon-dingtalk" style="font-size: 24px; margin-left: 12px; margin-right: 12px;"/>
            </el-link>
          </div>
          <el-divider v-if="loginCfg.register || loginCfg.forgetPassword"></el-divider>
          <div>
            <router-link :to="{ name: 'register' }" v-if="loginCfg.register">
              <el-link :underline="false" type="info" class="no-underline fl">{{ $t('register') }}</el-link>
            </router-link>
            <router-link :to="{ name: 'forgetPassword' }" v-if="loginCfg.forgetPassword">
              <el-link :underline="false" type="info" class="no-underline fr">{{ $t('forgetPassword') }}</el-link>
            </router-link>
          </div>
        </el-card>
        <div class="login-footer" v-html="sysCfg.copyright"/>
      </main>
    </div>
  </div>
</template>

<script>
import Cookies from 'js-cookie'
import mixinFormModule from '@/mixins/form-module'

export default {
  mixins: [mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        // 登录接口
        dataFormParamEncrypt: false,
        dataFormSaveURL: '/uc/user/login'
      },
      dataFormMode: 'save',
      // 系统配置
      sysCfg: {},
      // 全局登录配置
      loginCfg: {
        forgetPassword: false,
        register: false,
        loginByUsernameAndPassword: false,
        loginByMobileAndSmsCode: false,
        loginByWechatScan: false,
        loginByDingtalkScan: false
      },
      // 当前登录渠道配置
      loginChannelCfg: {
        captcha: false
      },
      // 短信发送倒计时
      smsSendTimeout: 60,
      // 验证码图片
      captchaImage: '',
      dataForm: {
        username: '',
        password: '',
        mobile: '',
        mobileArea: '86',
        smsCode: '',
        uuid: '',
        captcha: '',
        // 登录类型
        type: 0
      },
      // 第三方登录窗口
      thirdLoginWindow: null
    }
  },
  computed: {
    dataRule () {
      return {
        username: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        password: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        captcha: [
          { required: this.loginChannelCfg.captcha, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        smsCode: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  created () {
    // 获取配置项
    this.getParamCfg()
  },
  beforeDestroy () {
    // 把子窗口销毁
    if (this.thirdLoginWindow) {
      this.thirdLoginWindow.close()
    }
  },
  methods: {
    // 切换登录类型
    typeChangeHandle () {
      // 赋值当前渠道配置
      if (this.dataForm.type === 10) {
        this.loginChannelCfg = this.loginCfg.loginByUsernameAndPasswordCfg
      } else if (this.dataForm.type === 30) {
        this.loginChannelCfg = this.loginCfg.loginByMobileAndSmsCodeCfg
      }
      // 获取验证码
      if (this.loginChannelCfg.captcha) {
        this.getCaptcha()
      }
      // 清空校验
      this.$refs['dataForm'].clearValidate()
    },
    // 获取系统配置
    getParamCfg () {
      this.$http.get(`/sys/param/getContentByCodes?codes=SYS_CFG,LOGIN_ADMIN_CFG`).then(({ data: res }) => {
        this.formLoading = false
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        } else {
          // 复制显示配置
          this.sysCfg = res.data.SYS_CFG
          Cookies.set('title', this.sysCfg.title)
          document.title = this.sysCfg.title
          // 赋值全局登录配置
          this.loginCfg = res.data.LOGIN_ADMIN_CFG
          // 找到第一个enable的登录渠道
          if (this.loginCfg.loginByUsernameAndPassword) {
            this.dataForm.type = 10
          } else if (this.loginCfg.loginByMobileAndSmsCode) {
            this.dataForm.type = 30
          } else if (this.loginCfg.loginByWechatScan) {
            this.dataForm.type = 40
          } else if (this.loginCfg.loginByDingtalkScan) {
            this.dataForm.type = 50
          }
          this.typeChangeHandle()
        }
      }).catch(resp => {
        this.formLoading = false
      })
    },
    // 获取验证码
    getCaptcha () {
      this.$http.get(`/sys/captcha/base64?width=110&height=40`).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        } else {
          this.captchaImage = res.data.image
          this.dataForm.uuid = res.data.uuid
        }
      })
    },
    // 发送短信验证码
    smsCodeSendHandle () {
      if (this.smsSendTimeout < 60) {
        return
      }
      this.formLoading = true
      this.$refs['dataForm'].validateField('mobile', (errorMessage) => {
        if (errorMessage) {
          this.formLoading = false
          return false
        }
        this.$http.post(`/msg/smsLog/sendCode`, { 'mobile': this.dataForm.mobile, 'tplCode': 'CODE_LOGIN' }).then(({ data: res }) => {
          if (res.code !== 0) {
            this.$message.error(res.toast)
          } else {
            this.$message.success('短信发送成功')
            // 开始倒计时
            this.smsSendTimeout = 60
            const timer = window.setInterval(() => {
              if (this.smsSendTimeout-- <= 0) {
                this.smsSendTimeout = 60
                window.clearInterval(timer)
              }
            }, 1000)
          }
          this.formLoading = false
        }).catch(resp => {
          this.formLoading = false
        })
      })
    },
    // 表单提交失败
    onFormSubmitError (res) {
      // 刷新验证码
      if (this.loginChannelCfg.captcha) {
        this.getCaptcha()
      }
      this.$message.error(res.toast)
    },
    // 表单提交成功
    onFormSubmitSuccess (res) {
      // 加一个message,缓解跳转过程中的等待
      this.$message({ message: '登录成功', type: 'success', duration: 1000 })
      // 将token保存到cookie
      Cookies.set('token', res.data.token)
      // 跳转到home
      this.$router.replace({ name: 'home' })
    },
    // 第三方登录
    onOauthLogin (type) {
      // 获取配置
      this.$http.get(`/sys/param/getContentByCode?code=` + type).then(({ data: res }) => {
        this.formLoading = false
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        if (type === 'DINGTALK_SCAN') {
          let url = 'https://oapi.dingtalk.com/connect/qrconnect?appid=' + res.data.appid +
                  '&response_type=code' +
                  '&scope=snsapi_login' +
                  '&state=STATE' +
                  '&redirect_uri=' + encodeURIComponent(res.data.callback)
          this.openOauthWindow(url, type)
        } else {
          return this.$message.error('未定义的type=' + type)
        }
      }).catch(() => {
        this.formLoading = false
      })
    },
    /**
     * 打开第三方登录win
     */
    openOauthWindow (url, type) {
      const width = 500 // 弹出窗宽度
      const height = 500 // 弹出窗高度
      const top = (window.screen.availHeight - 30 - height) / 2 // 弹出窗垂直位置
      const left = (window.screen.availWidth - 10 - width) / 2 // 弹出窗水平位置
      this.thirdLoginWindow = window.open(url, `login ${type}`, `width=${width},height=${height},top=${top},left=${left},toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no`)
      let that = this
      window.addEventListener('message', function (event) {
        if (event.origin !== location.origin || !event.data) {
          return
        }
        let data = JSON.parse(event.data)
        that.oauthLoginHandle(type, data.code)
      }, false)
    },
    /**
     * 第三方code登录
     */
    oauthLoginHandle (type, code) {
      this.formLoading = true
      this.$http.post(`/uc/userOauth/oauthLoginByCode`, { code: code, paramCode: type, type: type }).then(({ data: res }) => {
        if (res.code !== 0) {
          this.$message.error(res.toast)
        }
        this.$alert(res.data)
        console.log(res.data)
        this.formLoading = false
      }).catch(() => {
        this.formLoading = false
      })
    }
  }
}
</script>
