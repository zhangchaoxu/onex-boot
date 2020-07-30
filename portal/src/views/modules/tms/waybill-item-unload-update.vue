<template>
  <el-dialog :visible.sync="visible" title="卸货" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="12">
                <el-form-item label="箱号" prop="code">
                    <el-input v-model="dataForm.code" placeholder="请输入箱号" disabled/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="封号" prop="sealCode">
                    <el-input v-model="dataForm.sealCode" placeholder="请输入封号" disabled/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="数量(吨)" prop="qty">
                    <el-input-number v-model="dataForm.qty" placeholder="输入数量" controls-position="right" :min="0" :max="9999999" :precision="2" class="w-percent-100" disabled/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="卸货数量(吨)" prop="qtyUnload">
                    <el-input-number v-model="dataForm.qtyUnload" placeholder="输入卸货数量(吨)" controls-position="right" :min="0" :max="9999999" :precision="2" class="w-percent-100"/>
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
        dataFormUpdateURL: `/tms/waybillItem/updateQtyUnload`,
        dataFormInfoURL: `/tms/waybillItem/info?id=`
      },
      dataForm: {
        id: '',
        supplierName: '',
        code: '',
        sealCode: '',
        goodsType: '',
        goods: '玉米',
        unit: '吨',
        purchaseDate: '',
        qty: '',
        qtyUnload: '',
        price: '',
        remark: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        qtyUnload: [
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
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      if (this.dataForm.qtyUnload == null) {
        this.dataForm.qtyUnload = this.dataForm.qty
      }
    }
  }
}
</script>
