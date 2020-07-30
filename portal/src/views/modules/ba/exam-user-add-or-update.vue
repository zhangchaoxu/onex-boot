<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
          <el-form-item label="用户id" prop="userId">
          <el-input v-model="dataForm.userId" placeholder="用户id"></el-input>
      </el-form-item>
          <el-form-item label="检测类型 1 成人检测 2 孩子检测" prop="subjectType">
          <el-input v-model="dataForm.subjectType" placeholder="检测类型 1 成人检测 2 孩子检测"></el-input>
      </el-form-item>
          <el-form-item label="检测结果" prop="result">
          <el-input v-model="dataForm.result" placeholder="检测结果"></el-input>
      </el-form-item>
          <el-form-item label="家长名字" prop="parentName">
          <el-input v-model="dataForm.parentName" placeholder="家长名字"></el-input>
      </el-form-item>
          <el-form-item label="小孩名字" prop="childName">
          <el-input v-model="dataForm.childName" placeholder="小孩名字"></el-input>
      </el-form-item>
          <el-form-item label="小孩年级" prop="childClass">
          <el-input v-model="dataForm.childClass" placeholder="小孩年级"></el-input>
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
        dataFormSaveURL: `/ba/examUser/save`,
        dataFormUpdateURL: `/ba/examUser/update`,
        dataFormInfoURL: `/ba/examUser/info?id=`
      },
      dataForm: {
        id: '',
        userId: '',
        subjectType: '',
        result: '',
        parentName: '',
        childName: '',
        childClass: '',
        createId: '',
        createTime: '',
        updateId: '',
        updateTime: '',
        deleted: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        userId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        subjectType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        result: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        parentName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        childName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        childClass: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        createId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        createTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        updateId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        updateTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        deleted: [
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
