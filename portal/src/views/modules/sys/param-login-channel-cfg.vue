<template>
    <el-dialog :visible.sync="visible" title="登录配置" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="paramDataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="登录类型">
                        <el-input v-model="paramDataForm.type" placeholder="登录类型" maxlength="20"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="多客户端支持">
                        <el-switch v-model="paramDataForm.multiLogin"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="图形验证码">
                            <el-switch v-model="paramDataForm.captcha"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="paramDataForm.captcha">
                        <el-form-item label="魔法验证码">
                            <el-input v-model="paramDataForm.magicCaptcha" placeholder="魔法验证码" maxlength="6"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="token有效期(秒)">
                            <el-input-number v-model="paramDataForm.expire" controls-position="right" :min="60" :max="31536000"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="token自动续期">
                            <el-switch v-model="paramDataForm.renewalToken"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="基于权限">
                            <el-switch v-model="paramDataForm.permissionsBase"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="基于角色">
                            <el-switch v-model="paramDataForm.roleBase"/>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-row>
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'

export default {
  mixins: [mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormUpdateURL: `/sys/param/update`,
        dataFormInfoURL: `/sys/param/info?id=`
      },
      dataForm: {
        id: '',
        code: '',
        remark: '',
        content: ''
      },
      paramDataForm: {
        type: '',
        captcha: false,
        magicCaptcha: '',
        renewalToken: true,
        expire: 604800,
        multiLogin: true,
        roleBase: false,
        permissionsBase: true
      }
    }
  },
  computed: {
    dataRule () {
      return {
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.formLoading = true
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        this.initFormData()
      })
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // json序列化content
      this.paramDataForm = JSON.parse(this.dataForm.content)
    },
    // 表单提交之前的操作
    beforeDateFormSubmit () {
      // 将form转为content的json
      this.dataForm.content = JSON.stringify(this.paramDataForm)
      this.dataFormSubmitParam = this.dataForm
      return true
    }
  }
}
</script>
