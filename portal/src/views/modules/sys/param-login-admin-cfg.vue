<template>
    <el-dialog :visible.sync="visible" title="后台登录配置" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="paramDataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row v-for="(item, index) in paramDataForm.channels" :key="index">
                <el-col :span="8">
                    <el-form-item label="标题">
                        <el-input v-model="item.title"/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="类型值">
                        <el-input v-model="item.type"/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="开放">
                        <el-switch v-model="item.enable"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="开放注册" prop="register">
                        <el-switch v-model="paramDataForm.register"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="忘记密码" prop="forgetPassword">
                        <el-switch v-model="paramDataForm.forgetPassword"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="注意">
                <span>各登录类型详细配置见具体参数</span>
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
        register: '',
        forgetPassword: '',
        channels: []
      }
    }
  },
  computed: {
    dataRule () {
      return {
        register: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        forgetPassword: [
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
