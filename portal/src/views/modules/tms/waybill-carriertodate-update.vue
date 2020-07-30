<template>
  <el-dialog :visible.sync="visible" title="到港时间" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="12">
                <el-form-item label="运单号" prop="code">
                    <el-input v-model="dataForm.code" disabled/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="发货人" prop="sender">
                    <el-input v-model="dataForm.sender" disabled/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="装货港" prop="carrierFrom">
                    <el-input v-model="dataForm.carrierFrom" disabled/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="离港日" prop="carrierFromDate">
                    <el-date-picker
                            disabled
                            class="w-percent-100"
                            v-model="dataForm.carrierFromDate"
                            type="date"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择离港日"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="卸货港" prop="carrierTo">
                    <el-input v-model="dataForm.carrierTo" disabled/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="到港日" prop="carrierToDate">
                    <el-date-picker
                            class="w-percent-100"
                            v-model="dataForm.carrierToDate"
                            type="date"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择到港日"/>
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
import mixinBaseModule from '@/mixins/base-module'
import mixinFormModule from '@/mixins/form-module'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormUpdateURL: `/tms/waybill/updateCarrierToDate`,
        dataFormInfoURL: `/tms/waybill/info?id=`
      },
      dataForm: {
        id: '',
        code: '',
        sender: '',
        carrierOwner: '',
        carrierName: '',
        carrierNo: '',
        carrierFromDate: '',
        carrierFrom: '',
        carrierTo: '',
        carrierToDate: '',
        status: '',
        remark: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        carrierToDate: [
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
