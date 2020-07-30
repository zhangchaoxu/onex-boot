<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="12">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="dataForm.name" placeholder="名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item prop="sort" :label="$t('base.sort')">
                    <el-input-number v-model="dataForm.sort" controls-position="right" :min="0" :max="9999" :label="$t('base.sort')"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item prop="logo" label="图标">
            <file-upload ref="fileUpload" v-model="dataForm.logo" :limit="5" mode="image"/>
        </el-form-item>
        <el-form-item label="描述" prop="content">
            <el-input v-model="dataForm.content" placeholder="描述" type="textarea"/>
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
import FileUpload from '@/components/file-upload'

export default {
  mixins: [mixinFormModule],
  components: { FileUpload },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/crm/productCategory/save`,
        dataFormUpdateURL: `/crm/productCategory/update`,
        dataFormInfoURL: `/crm/productCategory/info?id=`
      },
      dataForm: {
        id: '',
        pid: 0,
        name: '',
        logo: '',
        sort: '',
        content: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        pid: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sort: [
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
