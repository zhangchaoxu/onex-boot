<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-form-item label="年" prop="year">
                <el-input v-model="dataForm.year" placeholder="年"/>
            </el-form-item>
            <el-form-item label="月" prop="month">
                <el-input v-model="dataForm.month" placeholder="月"/>
            </el-form-item>
            <el-form-item label="日" prop="day">
                <el-input v-model="dataForm.day" placeholder="日"/>
            </el-form-item>
            <el-form-item label="类型" prop="type">
                <el-input v-model="dataForm.type" placeholder="类型"/>
            </el-form-item>
            <el-form-item label="星期" prop="week">
                <el-input v-model="dataForm.week" placeholder="星期"/>
            </el-form-item>
            <el-form-item label="农历年" prop="lunaryear">
                <el-input v-model="dataForm.lunaryear" placeholder="农历年"/>
            </el-form-item>
            <el-form-item label="农历月" prop="lunarmonth">
                <el-input v-model="dataForm.lunarmonth" placeholder="农历月"/>
            </el-form-item>
            <el-form-item label="农历日" prop="lunarday">
                <el-input v-model="dataForm.lunarday" placeholder="农历日"/>
            </el-form-item>
            <el-form-item label="生肖" prop="shengxiao">
                <el-input v-model="dataForm.shengxiao" placeholder="生肖"/>
            </el-form-item>
            <el-form-item label="干支" prop="ganzhi">
                <el-input v-model="dataForm.ganzhi" placeholder="干支"/>
            </el-form-item>
            <el-form-item label="星座" prop="star">
                <el-input v-model="dataForm.star" placeholder="星座"/>
            </el-form-item>
            <el-form-item label="日期" prop="dayDate">
                <el-input v-model="dataForm.dayDate" placeholder="日期"/>
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
        dataFormSaveURL: `/sys/calendar/save`,
        dataFormUpdateURL: `/sys/calendar/update`,
        dataFormInfoURL: `/sys/calendar/info?id=`
      },
      dataForm: {
        id: '',
        year: '',
        month: '',
        day: '',
        type: '',
        week: '',
        lunaryear: '',
        lunarmonth: '',
        lunarday: '',
        shengxiao: '',
        ganzhi: '',
        star: '',
        dayDate: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        year: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        month: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        day: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        week: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        lunaryear: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        lunarmonth: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        lunarday: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        shengxiao: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        ganzhi: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        star: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        dayDate: [
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
