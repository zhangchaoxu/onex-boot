<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-form-item prop="type" :label="$t('base.type')">
                <el-radio-group v-model="dataForm.type" :disabled="!!dataForm.id" size="small">
                    <el-radio-button label="sms">短信</el-radio-button>
                    <el-radio-button label="email">电子邮件</el-radio-button>
                    <el-radio-button label="wx_mp_template">微信公众号模板消息</el-radio-button>
                    <el-radio-button label="wx_ma_subscribe">微信小程序订阅消息</el-radio-button>
                    <el-radio-button label="app_push">APP推送</el-radio-button>
                </el-radio-group>
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item :label="$t('base.code')" prop="code">
                        <el-input v-model="dataForm.code" :placeholder="$t('base.code')"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item prop="name" :label="$t('base.name')">
                        <el-input v-model="dataForm.name" :placeholder="$t('base.name')"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="平台配置" prop="param">
                <el-input v-model="dataForm.param" placeholder="平台配置" type="textarea"/>
            </el-form-item>
            <div v-if="dataForm.type === 'sms'">
                <el-form-item label="内容" prop="content">
                    <el-input v-model="dataForm.content" placeholder="内容仅作记录,实际内容请在短信平台修改" type="textarea"/>
                </el-form-item>
            </div>
            <div v-else-if="dataForm.type === 'email'">
                <el-form-item prop="title" label="邮件标题">
                    <el-input v-model="dataForm.title" placeholder="请输入邮件标题"/>
                </el-form-item>
                <el-form-item prop="content" label="邮件内容">
                    <quill-editor ref="editorContent"/>
                </el-form-item>
            </div>
            <div v-if="dataForm.type === 'wx_template'">
                <el-form-item prop="title" label="标题">
                    <el-input v-model="dataForm.title" placeholder="请输入标题"/>
                </el-form-item>
                <el-form-item label="内容" prop="content">
                    <el-input v-model="dataForm.content" placeholder="内容仅作记录,实际内容由微信平台对应模板生成" type="textarea"/>
                </el-form-item>
            </div>
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import QuillEditor from '@/components/quill-editor'

export default {
  mixins: [mixinFormModule],
  components: { QuillEditor },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/msg/mailTpl/save`,
        dataFormUpdateURL: `/msg/mailTpl/update`,
        dataFormInfoURL: `/msg/mailTpl/info?id=`
      },
      dataForm: {
        id: '',
        name: '',
        type: '',
        code: '',
        title: '',
        content: '',
        param: ''
      }
    }
  },
  watch: {
    'dataForm.type' () {
      this.$refs['dataForm'].clearValidate()
    }
  },
  computed: {
    dataRule () {
      return {
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        title: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        param: [
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
