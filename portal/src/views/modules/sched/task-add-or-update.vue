<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item prop="name" :label="$t('base.name')">
        <el-input v-model="dataForm.name" :placeholder="$t('base.name')"></el-input>
      </el-form-item>
      <el-form-item prop="cron" label="cron">
        <el-input v-model="dataForm.cron" placeholder="cron">
          <el-link type="primary" href="https://www.bejson.com/othertools/cron/" target="_blank" :underline="false" slot="append">在线cron生成</el-link>
        </el-input>
      </el-form-item>
      <el-form-item prop="param" :label="$t('base.param')">
        <el-input v-model="dataForm.param" :placeholder="$t('base.param')"></el-input>
      </el-form-item>
      <el-form-item prop="remark" :label="$t('base.remark')">
        <el-input v-model="dataForm.remark" :placeholder="$t('base.remark')"></el-input>
      </el-form-item>
      <el-form-item prop="status" :label="$t('base.status')">
        <el-radio-group v-model="dataForm.status" size="mini">
          <el-radio-button :label="0">暂停</el-radio-button>
          <el-radio-button :label="1">启用</el-radio-button>
        </el-radio-group>
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
      // 表单参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/sched/task/save`,
        dataFormUpdateURL: `/sched/task/update`,
        dataFormInfoURL: `/sched/task/info?id=`
      },
      dataForm: {
        id: '',
        name: '',
        param: '',
        cron: '',
        remark: '',
        status: 0
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        cron: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.visible = true
      this.formLoading = true
      this.$nextTick(() => {
        this.resetForm()
        this.initFormData()
      })
    }
  }
}
</script>
