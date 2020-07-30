<template>
  <el-dialog :visible.sync="visible" title="App关于我们配置" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="paramDataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="版本号" prop="version">
            <el-input-number v-model="paramDataForm.version" placeholder="版本号" controls-position="right" :min="0" :max="999999" class="w-percent-100"/>
          </el-form-item>
        </el-col>
        <el-col :span="12" prop="brief">
          <el-form-item label="标语">
            <el-input v-model="paramDataForm.brief" placeholder="标语" maxlength="50" show-word-limit/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="详细介绍">
          <el-input v-model="paramDataForm.detail" placeholder="详细介绍" type="textarea" maxlength="400" show-word-limit/>
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
        version: '',
        brief: '',
        detail: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        version: [
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
