<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false" width="70%">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="产品" prop="productId">
                        <el-input v-model="dataForm.productName" placeholder="选择产品" readonly>
                            <product-pick class="small-button" slot="append" :id="dataForm.productId" @onProductPicked="onProductPicked"/>
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="产品单位" prop="salePrice">
                        <el-select v-model="dataForm.productUnit" filterable allow-create default-first-option placeholder="请选择单位" class="w-percent-100">
                            <el-option label="个" value="个"/>
                            <el-option label="件" value="件"/>
                            <el-option label="套" value="套"/>
                            <el-option label="只" value="只"/>
                            <el-option label="盒" value="盒"/>
                            <el-option label="箱" value="箱"/>
                            <el-option label="千克" value="千克"/>
                            <el-option label="米" value="米"/>
                            <el-option label="台" value="台"/>
                            <el-option label="吨" value="吨"/>
                            <el-option label="瓶" value="瓶"/>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="标准价" prop="salePrice">
                        <el-input-number v-model="dataForm.salePrice" placeholder="输入产品标准价" controls-position="right" :min="0" :max="99999" :precision="2" :step="1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="折扣" prop="discount">
                        <el-input-number v-model="dataForm.discount" placeholder="输入产品折扣" controls-position="right" :min="0" :max="100" :precision="2" :step="1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="产品折扣价" prop="discountPrice">
                        <el-input-number v-model="dataForm.discountPrice" placeholder="输入产品折扣价" controls-position="right" :min="0" :max="99999" :precision="2" :step="1" class="w-percent-100" disabled/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="数量" prop="qty">
                        <el-input-number v-model="dataForm.qty" placeholder="输入产品数量" controls-position="right" :min="0" :max="999999" :precision="0" :step="1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-divider>价格小计<i class="el-icon-money"/>{{dataForm.totalPrice}}</el-divider>
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import ProductPick from './product-pick'
import debounce from 'lodash/debounce'

export default {
  mixins: [mixinFormModule],
  components: { ProductPick },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/crm/businessProduct/save`,
        dataFormUpdateURL: `/crm/businessProduct/update`,
        dataFormInfoURL: `/crm/businessProduct/info?id=`
      },
      index: -1,
      dataForm: {
        id: '',
        customerId: '',
        businessId: '',
        tenantId: '',
        tenantName: '',
        productId: '',
        productUnit: '',
        productCategoryId: '',
        productCategoryName: '',
        qty: 1,
        salePrice: '',
        discount: 100,
        discountPrice: '',
        totalPrice: ''
      }
    }
  },
  watch: {
    // 监听数据变化
    'dataForm.salePrice' () {
      this.calcPrice()
    },
    'dataForm.discount' () {
      this.calcPrice()
    },
    'dataForm.qty' () {
      this.calcPrice()
    }
  },
  computed: {
    dataRule () {
      return {
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
        this.formLoading = false
      })
    },
    // 选中客户
    onProductPicked (result) {
      if (result && result.length > 0) {
        this.dataForm.productId = result[0].id
        this.dataForm.productName = result[0].name
        this.dataForm.productUnit = result[0].unit
        this.dataForm.salePrice = result[0].salePrice
      }
    },
    // 计算价格
    calcPrice () {
      this.dataForm.discountPrice = this.dataForm.salePrice * this.dataForm.discount / 100
      this.dataForm.totalPrice = this.dataForm.discountPrice * this.dataForm.qty
    },
    // 表单提交
    dataFormSubmitHandle: debounce(function () {
      this.formLoading = true
      if (this.beforeDateFormSubmit()) {
        // 验证表单
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) {
            this.formLoading = false
            return false
          }
          this.$emit('onProductEdited', this.dataForm, this.index)
          this.visible = false
          this.formLoading = false
        })
      } else {
        this.formLoading = false
      }
    }, 1000, { 'leading': true, 'trailing': false })
  }
}
</script>
