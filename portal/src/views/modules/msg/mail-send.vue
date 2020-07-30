<template>
  <el-dialog :visible.sync="visible" title="发送消息" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item prop="tplCode" label="模板编码">
        <el-input v-model="dataForm.tplCode" disabled/>
      </el-form-item>
      <el-form-item prop="mailTo" label="收件人">
        <el-input v-model="dataForm.mailTo" placeholder="收件人"/>
      </el-form-item>
      <el-form-item prop="titleParam" label="标题参数" v-if="dataForm.type === 'email'">
        <el-input v-model="dataForm.titleParam" placeholder="标题参数"/>
      </el-form-item>
      <el-form-item prop="contentParam" label="内容参数">
        <el-input v-model="dataForm.contentParam" placeholder="内容参数"/>
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
        dataFormSaveURL: `/msg/mailLog/send`
      },
      dataForm: {
        tplType: '',
        tplCode: '',
        mailTo: '',
        contentParam: '',
        titleParam: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        mailTo: [
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
    }
  }
}
</script>
