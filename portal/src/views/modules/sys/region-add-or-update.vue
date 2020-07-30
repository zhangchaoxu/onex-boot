<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm"  :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
          <el-form-item label="上级区域编码,0为跟目录" prop="pid">
          <el-input v-model="dataForm.pid" placeholder="上级区域编码,0为跟目录"/>
      </el-form-item>
              <el-form-item label="更新者" prop="updater">
          <el-input v-model="dataForm.updater" placeholder="更新者"/>
      </el-form-item>
          <el-form-item label="更新时间" prop="updateDate">
          <el-input v-model="dataForm.updateDate" placeholder="更新时间"/>
      </el-form-item>
          <el-form-item label="软删标记" prop="delFlag">
          <el-input v-model="dataForm.delFlag" placeholder="软删标记"/>
      </el-form-item>
          <el-form-item label="区域名称" prop="name">
          <el-input v-model="dataForm.name" placeholder="区域名称"/>
      </el-form-item>
          <el-form-item label="区域邮编" prop="code">
          <el-input v-model="dataForm.code" placeholder="区域邮编"/>
      </el-form-item>
          <el-form-item label="区域级别" prop="level">
          <el-input v-model="dataForm.level" placeholder="区域级别"/>
      </el-form-item>
          <el-form-item label="区域级别名称" prop="levelName">
          <el-input v-model="dataForm.levelName" placeholder="区域级别名称"/>
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
        dataFormSaveURL: `/sys/region/save`,
        dataFormUpdateURL: `/sys/region/update`,
        dataFormInfoURL: `/sys/region/info?id=`
      },
      dataForm: {
        id: '',
        pid: '',
        name: '',
        code: '',
        level: '',
        levelName: ''
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
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        level: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        levelName: [
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
        this.$refs['dataForm'].resetFields()
        this.initFormData()
      })
    }
  }
}
</script>
