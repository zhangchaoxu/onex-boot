<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
                    <el-form-item label="客户id" prop="customerId">
          <el-input v-model="dataForm.customerId" placeholder="客户id"></el-input>
      </el-form-item>
          <el-form-item label="合同id" prop="contractId">
          <el-input v-model="dataForm.contractId" placeholder="合同id"></el-input>
      </el-form-item>
          <el-form-item label="租户id" prop="tenantId">
          <el-input v-model="dataForm.tenantId" placeholder="租户id"></el-input>
      </el-form-item>
          <el-form-item label="租户名称" prop="tenantName">
          <el-input v-model="dataForm.tenantName" placeholder="租户名称"></el-input>
      </el-form-item>
          <el-form-item label="产品id" prop="productId">
          <el-input v-model="dataForm.productId" placeholder="产品id"></el-input>
      </el-form-item>
          <el-form-item label="产品单位" prop="productUnit">
          <el-input v-model="dataForm.productUnit" placeholder="产品单位"></el-input>
      </el-form-item>
          <el-form-item label="产品分类id" prop="productCategoryId">
          <el-input v-model="dataForm.productCategoryId" placeholder="产品分类id"></el-input>
      </el-form-item>
          <el-form-item label="产品分类名称" prop="productCategoryName">
          <el-input v-model="dataForm.productCategoryName" placeholder="产品分类名称"></el-input>
      </el-form-item>
          <el-form-item label="数量" prop="qty">
          <el-input v-model="dataForm.qty" placeholder="数量"></el-input>
      </el-form-item>
          <el-form-item label="产品标准价格" prop="salePrice">
          <el-input v-model="dataForm.salePrice" placeholder="产品标准价格"></el-input>
      </el-form-item>
          <el-form-item label="折扣" prop="discount">
          <el-input v-model="dataForm.discount" placeholder="折扣"></el-input>
      </el-form-item>
          <el-form-item label="产品折扣价格" prop="discountPrice">
          <el-input v-model="dataForm.discountPrice" placeholder="产品折扣价格"></el-input>
      </el-form-item>
          <el-form-item label="价格小计" prop="totalPrice">
          <el-input v-model="dataForm.totalPrice" placeholder="价格小计"></el-input>
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
        dataFormSaveURL: `/crm/contractProduct/save`,
        dataFormUpdateURL: `/crm/contractProduct/update`,
        dataFormInfoURL: `/crm/contractProduct/info?id=`
      },
      dataForm: {
        id: '',
        createId: '',
        createTime: '',
        updateId: '',
        updateTime: '',
        deleted: '',
        customerId: '',
        contractId: '',
        tenantId: '',
        tenantName: '',
        productId: '',
        productUnit: '',
        productCategoryId: '',
        productCategoryName: '',
        qty: '',
        salePrice: '',
        discount: '',
        discountPrice: '',
        totalPrice: ''
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
        customerId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        contractId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        tenantId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        tenantName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        productId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        productUnit: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        productCategoryId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        productCategoryName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        qty: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        salePrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        discount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        discountPrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        totalPrice: [
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
