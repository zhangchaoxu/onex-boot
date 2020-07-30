<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
                    <el-form-item label="订单id" prop="orderId">
          <el-input v-model="dataForm.orderId" placeholder="订单id"></el-input>
      </el-form-item>
          <el-form-item label="订单明细id" prop="orderItemId">
          <el-input v-model="dataForm.orderItemId" placeholder="订单明细id"></el-input>
      </el-form-item>
          <el-form-item label="商品id" prop="goodsId">
          <el-input v-model="dataForm.goodsId" placeholder="商品id"></el-input>
      </el-form-item>
          <el-form-item label="租户id" prop="tenantId">
          <el-input v-model="dataForm.tenantId" placeholder="租户id"></el-input>
      </el-form-item>
          <el-form-item label="租户名称" prop="tenantName">
          <el-input v-model="dataForm.tenantName" placeholder="租户名称"></el-input>
      </el-form-item>
          <el-form-item label="收益用户id" prop="benefitUserId">
          <el-input v-model="dataForm.benefitUserId" placeholder="收益用户id"></el-input>
      </el-form-item>
          <el-form-item label="收益金额" prop="benefitPrice">
          <el-input v-model="dataForm.benefitPrice" placeholder="收益金额"></el-input>
      </el-form-item>
          <el-form-item label="收益状态 0 未发放 1 已发放 -1 已回收" prop="benefitStatus">
          <el-input v-model="dataForm.benefitStatus" placeholder="收益状态 0 未发放 1 已发放 -1 已回收"></el-input>
      </el-form-item>
          <el-form-item label="支付订单id" prop="payOrderId">
          <el-input v-model="dataForm.payOrderId" placeholder="支付订单id"></el-input>
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
        dataFormSaveURL: `/shop/orderBenefit/save`,
        dataFormUpdateURL: `/shop/orderBenefit/update`,
        dataFormInfoURL: `/shop/orderBenefit/info?id=`
      },
      dataForm: {
        id: '',
        createId: '',
        createTime: '',
        updateId: '',
        updateTime: '',
        deleted: '',
        orderId: '',
        orderItemId: '',
        goodsId: '',
        tenantId: '',
        tenantName: '',
        benefitUserId: '',
        benefitPrice: '',
        benefitStatus: '',
        payOrderId: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
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
        ],
        orderId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        orderItemId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        goodsId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        tenantId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        tenantName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        benefitUserId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        benefitPrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        benefitStatus: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        payOrderId: [
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
