<template>
  <el-dialog :visible.sync="visible" title="微信配置" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="paramDataForm" :rules="dataRule" ref="dataForm"  label-width="150px">
      <el-form-item prop="appid" label="appid">
        <el-input v-model="paramDataForm.appid" placeholder="appid"/>
      </el-form-item>
      <el-form-item prop="secret" label="secret">
        <el-input v-model="paramDataForm.secret" placeholder="secret"/>
      </el-form-item>
      <el-form-item prop="token" label="token">
        <el-input v-model="paramDataForm.token" placeholder="token"/>
      </el-form-item>
      <el-form-item prop="aesKey" label="EncodingAESKey">
        <el-input v-model="paramDataForm.aesKey" placeholder="EncodingAESKey"/>
      </el-form-item>
      <el-form-item label="消息格式" size="mini">
        <el-radio-group v-model="paramDataForm.msgDataFormat">
          <el-radio label="JSON">JSON</el-radio>
          <el-radio label="XML">XML</el-radio>
        </el-radio-group>
      </el-form-item>
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
        appId: '',
        secret: '',
        token: '',
        aesKey: '',
        msgDataFormat: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        appid: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        secret: [
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
