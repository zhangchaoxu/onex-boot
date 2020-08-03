<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm"  label-width="120px">
      <el-form-item prop="name" :label="$t('base.name')">
        <el-input v-model="dataForm.name" :placeholder="$t('base.name')"/>
      </el-form-item>
      <el-form-item v-if="dataForm.pid === '0'" prop="type" :label="$t('base.type')">
        <el-input v-model="dataForm.type" :placeholder="$t('base.type')"/>
      </el-form-item>
      <el-form-item v-if="dataForm.pid !== '0'" prop="value" :label="$t('base.value')">
        <el-input v-model="dataForm.value" :placeholder="$t('base.value')"/>
      </el-form-item>
      <el-form-item prop="sort" :label="$t('base.sort')">
        <el-input-number v-model="dataForm.sort" controls-position="right" :min="0" :label="$t('base.sort')"/>
      </el-form-item>
      <el-form-item prop="remark" :label="$t('base.remark')">
        <el-input v-model="dataForm.remark" :placeholder="$t('base.remark')"/>
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
        dataFormSaveURL: `/sys/dict/save`,
        dataFormUpdateURL: `/sys/dict/update`,
        dataFormInfoURL: `/sys/dict/info?id=`
      },
      dataForm: {
        id: '',
        pid: '0',
        name: '',
        type: '',
        value: '',
        sort: 0,
        remark: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        value: [
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
