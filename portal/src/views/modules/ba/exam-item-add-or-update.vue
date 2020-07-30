<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
          <el-form-item label="用户id" prop="userId">
          <el-input v-model="dataForm.userId" placeholder="用户id"></el-input>
      </el-form-item>
          <el-form-item label="检测id" prop="testId">
          <el-input v-model="dataForm.testId" placeholder="检测id"></el-input>
      </el-form-item>
          <el-form-item label="题面" prop="subjectQuestion">
          <el-input v-model="dataForm.subjectQuestion" placeholder="题面"></el-input>
      </el-form-item>
          <el-form-item label="选中结果" prop="subjectOption">
          <el-input v-model="dataForm.subjectOption" placeholder="选中结果"></el-input>
      </el-form-item>
          <el-form-item label="选中结果答案" prop="subjectAnswer">
          <el-input v-model="dataForm.subjectAnswer" placeholder="选中结果答案"></el-input>
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
        dataFormSaveURL: `/ba/examItem/save`,
        dataFormUpdateURL: `/ba/examItem/update`,
        dataFormInfoURL: `/ba/examItem/info?id=`
      },
      dataForm: {
        id: '',
        userId: '',
        testId: '',
        subjectQuestion: '',
        subjectOption: '',
        subjectAnswer: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        userId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        testId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subjectQuestion: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subjectOption: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subjectAnswer: [
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
