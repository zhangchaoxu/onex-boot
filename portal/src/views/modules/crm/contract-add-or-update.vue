<template>
    <el-card :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-divider>合同信息</el-divider>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="客户" prop="customerId">
                        <el-input v-model="dataForm.customerName" placeholder="选择客户" readonly>
                            <customer-pick class="small-button" slot="append" :id="dataForm.customerId" @onCustomerPicked="onCustomerPicked"/>
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="商机" prop="businessId">
                        <el-input v-model="dataForm.businessName" placeholder="选择相关商机" readonly>
                            <business-pick class="small-button" slot="append" :id="dataForm.businessId" @onBusinessPicked="onBusinessPicked"/>
                        </el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="合同名称" prop="name">
                        <el-input v-model="dataForm.name" placeholder="合同名称"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="合同编号" prop="code">
                        <el-input v-model="dataForm.code" placeholder="合同编号"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="有效期" prop="validStartDate">
                        <el-date-picker
                                class="w-percent-100"
                                v-model="dateRange"
                                type="daterange"
                                @change="dateRangeChangeHandle"
                                :picker-options="dateRangePickerOptions"
                                value-format="yyyy-MM-dd HH:mm:ss"
                                format="yyyy-MM-dd"
                                :range-separator="$t('datePicker.range')"
                                :start-placeholder="$t('datePicker.start')"
                                :end-placeholder="$t('datePicker.end')">
                        </el-date-picker>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="合同金额" prop="amount">
                        <el-input-number v-model="dataForm.amount" placeholder="合同金额" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1"  class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="销售员" prop="salesUserId">
                        <el-input v-model="dataForm.salesUserName" placeholder="销售员" readonly>
                            <user-pick class="small-button" slot="append" :id="dataForm.salesUserId" @onUserPicked="onUserPicked"/>
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="销售提成" prop="salesPercentageAmount">
                        <el-input-number v-model="dataForm.salesPercentageAmount" placeholder="输入销售提成" controls-position="right" :min="0" :max="999999" :precision="2" :step="1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="签约日期" prop="contractDate">
                        <el-date-picker v-model="dataForm.contractDate" type="date" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择签约日期" style="width: 100%"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="支付方式" prop="paytype">
                        <el-select v-model="dataForm.paytype" filterable allow-create default-first-option placeholder="请选择支付方式" class="w-percent-100">
                            <el-option label="无须支付" value="无须支付"/>
                            <el-option label="现金" value="现金"/>
                            <el-option label="银行转账" value="银行转账"/>
                            <el-option label="支付宝" value="支付宝"/>
                            <el-option label="微信" value="微信"/>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="客户签约人" prop="customerSigner">
                        <el-input v-model="dataForm.customerSigner" placeholder="客户签约人"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="公司签约人" prop="supplierSigner">
                        <el-input v-model="dataForm.supplierSigner" placeholder="公司签约人"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="附件" prop="attachment">
                <file-upload ref="fileUpload" v-model="dataForm.attachment" :limit="1" ossParamCode="OSS_CFG_PRI" :tips="`支持图片、文档和压缩包,大小不超过10M`"/>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="dataForm.remark" placeholder="备注" type="textarea"></el-input>
            </el-form-item>
            <el-divider>合同产品</el-divider>
            <el-table :data="dataForm.productList" border ref="productListTable"
                      show-summary :summary-method="getProductSummaries" style="width: 100%;">
                <el-table-column prop="productName" label="产品名" header-align="center" align="center" min-width="100"/>
                <el-table-column prop="productUnit" label="单位" header-align="center" align="center" width="100" class-name="no-padding">
                    <template slot-scope="scope">
                        <el-select v-if="productEditIndex === scope.$index" size="small" v-model="scope.row.productUnit" filterable allow-create default-first-option
                                   placeholder="请选择单位" class="w-percent-100">
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
                        <span v-else>{{scope.row.productUnit}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="salePrice" label="标准价" header-align="center" align="center" width="200" class-name="no-padding">
                    <template slot-scope="scope">
                        <el-input-number v-if="productEditIndex === scope.$index" size="small" v-model="scope.row.salePrice" placeholder="输入产品标准价" controls-position="right"
                                         :min="0" :max="999999" :precision="2" :step="1" class="w-percent-100" @change="onProductChangeHandle(scope.$index)"/>
                        <span v-else>{{scope.row.salePrice}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="discount" label="折扣" header-align="center" align="center" width="120" class-name="no-padding">
                    <template slot-scope="scope">
                        <el-input-number v-if="productEditIndex === scope.$index" size="small" v-model="scope.row.discount" placeholder="输入产品折扣" controls-position="right"
                                         :min="0" :max="100" :precision="2" :step="1" class="w-percent-100" @change="onProductChangeHandle(scope.$index)"/>
                        <span v-else>{{scope.row.discount}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="discountPrice" label="折扣价" header-align="center" align="center" width="100"/>
                <el-table-column prop="qty" label="数量" header-align="center" align="center" width="120" class-name="no-padding">
                    <template slot-scope="scope">
                        <el-input-number v-if="productEditIndex === scope.$index" size="small" v-model="scope.row.qty" placeholder="输入产品数量" controls-position="right"
                                         :min="0" :max="999999" :precision="0" :step="1" class="w-percent-100" @change="onProductChangeHandle(scope.$index)"/>
                        <span v-else>{{scope.row.qty}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="totalPrice" label="小计" header-align="center" align="center" width="100"/>
                <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="120">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" v-if="productEditIndex === scope.$index" @click="productEditIndex = -1">完成</el-button>
                        <el-button type="text" size="small" v-else @click="productEditIndex = scope.$index">{{ $t('update') }}</el-button>
                        <el-button type="text" size="small" @click="removeProduct(scope.$index)">{{ $t('delete') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div style="text-align: center; margin-top: 20px;">
                <product-pick class="small-button" btnText="添加产品" type="multi" @onProductPicked="onProductPicked"/>
                <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('save') }}</el-button>
            </div>
        </el-form>
    </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinFormModule from '@/mixins/form-module'
import UserPick from '../uc/user-pick'
import ProductPick from './product-pick'
import CustomerPick from './customer-pick'
import BusinessPick from './business-pick'
import FileUpload from '@/components/file-upload'

export default {
  inject: ['refresh'],
  mixins: [mixinBaseModule, mixinFormModule],
  components: { FileUpload, CustomerPick, ProductPick, UserPick, BusinessPick },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/crm/contract/save`,
        dataFormUpdateURL: `/crm/contract/update`,
        dataFormInfoURL: `/crm/contract/info?id=`
      },
      // 当前编辑行
      productEditIndex: -1,
      dateRange: null,
      dataForm: {
        id: '',
        customerId: '',
        paytype: '',
        content: '',
        attachment: '',
        contractDate: '',
        name: '',
        code: '',
        businessId: '',
        amount: '',
        orderDate: '',
        validStartDate: '',
        validEndDate: '',
        customerSigner: '',
        supplierSigner: '',
        remark: '',
        productDiscount: '',
        productPrice: '',
        salesUserId: '',
        salesPercentageAmount: '',
        productList: []
      }
    }
  },
  activated () {
    this.dataForm.id = this.$route.query.id
    let tab = this.$store.state.contentTabs.filter(item => item.name === this.$route.name)[0]
    if (tab) {
      tab.title = this.dataForm.id ? '修改合同' : '新增合同'
      // 根据step刷新数据
      this.init()
    }

    if (!this.dataForm.id) {
      // 只有新增时候有意义
      let businessId = this.$route.query.businessId || '' // 商机id
      if (businessId) {
        this.initContractByBusinessId(businessId)
      } else {
        let customerId = this.$route.query.customerId || '' // 客户id
        if (customerId) {
          this.getCustomerInfo(customerId)
        }
      }
    }
  },
  computed: {
    dataRule () {
      return {
        customerId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        amount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        contractDate: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.formLoading = false
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        this.dateRange = null
        this.initFormData()
      })
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // 防止后台返回null
      if (!this.dataForm.productList) {
        this.dataForm.productList = []
      }
      // 赋值日期选择器
      if (this.dataForm.validStartDate && this.dataForm.validEndDate) {
        this.dateRange = [this.dataForm.validStartDate, this.dataForm.validEndDate]
      }
    },
    // 表单提交成功
    onFormSubmitSuccess (res) {
      this.$confirm('合同信息保存成功', this.$t('prompt.title'), {
        confirmButtonText: '查看详情',
        cancelButtonText: '关闭',
        type: 'success'
      }).then(() => {
        this.removeCurrentTab({ name: 'crm-contract-info', query: { id: res.data.id } })
        // 关闭当前tab
      }).catch(() => {
        this.removeCurrentTab()
      })
    },
    // 时间区间选择器变化
    dateRangeChangeHandle (value) {
      if (value && value.length === 2) {
        this.dataForm.validStartDate = value[0]
        this.dataForm.validEndDate = value[1]
      } else {
        this.dataForm.validStartDate = ''
        this.dataForm.validEndDate = ''
      }
    },
    // 选中商机
    onBusinessPicked (result) {
      if (result && result.length > 0) {
        this.initContractByBusinessId(result[0].id)
      } else {
        this.dataForm.businessId = ''
        this.dataForm.businessName = ''
      }
    },
    // 选中用户
    onUserPicked (result) {
      if (result && result.length > 0) {
        this.dataForm.salesUserId = result[0].id
        this.dataForm.salesUserName = result[0].realName
      } else {
        this.dataForm.salesUserId = ''
        this.dataForm.salesUserName = ''
      }
    },
    // 通过商机初始化合同
    initContractByBusinessId (id) {
      this.formLoading = true
      this.$http.get('/crm/business/info?id=' + id).then(({ data: res }) => {
        if (res.code !== 0) {
          this.onGetInfoError(res)
        } else {
          // 复制相关属性到合同
          this.dataForm.businessId = res.data.id
          this.dataForm.businessName = res.data.name
          this.dataForm.productList = res.data.productList
          this.dataForm.customerId = res.data.customerId
          this.dataForm.customerName = res.data.customerName
          this.dataForm.amount = res.data.amount
          this.dataForm.name = res.data.name
        }
        this.formLoading = false
      }).catch(resp => {
        this.$message.error(this.$t('prompt.apierror') + resp)
        this.formLoading = false
      })
    },
    // 获得客户信息
    getCustomerInfo (id) {
      this.formLoading = true
      this.$http.get('/crm/customer/info?id=' + id).then(({ data: res }) => {
        if (res.code !== 0) {
          this.onGetInfoError(res)
        } else {
          // 复制相关属性
          this.dataForm.customerId = res.data.id
          this.dataForm.customerName = res.data.name
        }
        this.formLoading = false
      }).catch(resp => {
        this.$message.error(this.$t('prompt.apierror') + resp)
        this.formLoading = false
      })
    },
    // 选中产品
    onProductPicked (result) {
      this.productEditIndex = -1
      if (result && result.length > 0) {
        result.forEach(item => {
          let product = {
            productId: item.id,
            productCategoryIdId: item.catrgoryId,
            productCategoryIdName: item.catrgoryName,
            productName: item.name,
            productUnit: item.unit,
            salePrice: item.salePrice,
            discount: 100,
            discountPrice: item.salePrice,
            qty: 1,
            totalPrice: item.salePrice
          }
          this.$set(this.dataForm.productList, this.dataForm.productList.length, product)
        })
      }
    },
    /** 产品统计行 */
    getProductSummaries (param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (column.property === 'productName') {
          sums[index] = '合计'
          return
        }
        if (column.property === 'totalPrice' || column.property === 'qty') {
          // 针对部分列开放统计
          const values = data.map(item => Number(item[column.property]))
          if (!values.every(value => isNaN(value))) {
            // 所有项都是数字
            sums[index] = values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return prev + curr
              } else {
                return prev
              }
            }, 0)
            sums[index] = sums[index]
          } else {
            sums[index] = 'NA'
          }
        } else {
          sums[index] = ''
        }
      })
      return sums
    },
    // 删除产品
    removeProduct (index) {
      this.productEditIndex = -1
      this.dataForm.productList.splice(index, 1)
    },
    // 商品变化
    onProductChangeHandle (index) {
      let item = this.dataForm.productList[index]
      item.discountPrice = item.salePrice * item.discount / 100
      item.totalPrice = item.discountPrice * item.qty
    }
  }
}
</script>
