<template>
  <el-dialog :visible.sync="visible" title="App客服配置" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="paramDataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="版本号" prop="version">
            <el-input-number v-model="paramDataForm.version" placeholder="版本号" controls-position="right" :min="0" :max="999999" class="w-percent-100"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" prop="tel">
          <el-form-item label="客服电话">
            <el-input v-model="paramDataForm.tel" placeholder="客服电话" maxlength="100" show-word-limit/>
          </el-form-item>
        </el-col>
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
        version: '',
        tel: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        version: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        tel: [
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
