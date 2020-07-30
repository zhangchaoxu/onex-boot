<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-form-item label="名称" prop="name">
                <el-input v-model="dataForm.name" placeholder="名称"></el-input>
            </el-form-item>
            <el-row>
                <el-col :span="16">
                    <el-form-item label="有效期" prop="validStartTime">
                        <el-date-picker
                                v-model="dateRange"
                                type="datetimerange"
                                @change="dateRangeChangeHandle"
                                :picker-options="dateRangePickerOptions"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                :range-separator="$t('datePicker.range')"
                                :start-placeholder="$t('datePicker.start')"
                                :end-placeholder="$t('datePicker.end')">
                        </el-date-picker>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="dataForm.status" size="small">
                            <el-radio-button :label="1">有效</el-radio-button>
                            <el-radio-button :label="0">无效</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import mixinBaseModule from '@/mixins/base-module'

export default {
  mixins: [mixinFormModule, mixinBaseModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/uc/tenant/save`,
        dataFormUpdateURL: `/uc/tenant/update`,
        dataFormInfoURL: `/uc/tenant/info?id=`
      },
      dateRange: null,
      dataForm: {
        id: '',
        name: '',
        validStartTime: '',
        validEndTime: '',
        status: 1
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        validStartTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        validEndTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
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
    // 时间区间选择器变化
    dateRangeChangeHandle (value) {
      if (value && value.length === 2) {
        this.dataForm.validStartTime = value[0]
        this.dataForm.validEndTime = value[1]
      } else {
        this.dataForm.validStartTime = ''
        this.dataForm.validEndTime = ''
      }
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // 赋值日期选择器
      this.dateRange = [this.dataForm.validStartTime, this.dataForm.validEndTime]
    }
  }
}
</script>
