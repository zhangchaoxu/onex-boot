<template>
    <el-card :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-divider>商机信息</el-divider>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="客户" prop="customerId">
                        <el-input v-model="dataForm.customerName" placeholder="客户" readonly>
                            <customer-pick class="small-button" slot="append" :id="dataForm.customerId" @onCustomerPicked="onCustomerPicked"/>
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="商机名称" prop="name">
                        <el-input v-model="dataForm.name" placeholder="商机名称"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="预计成交" prop="dealDate">
                        <el-date-picker v-model="dataForm.dealDate" type="date" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd" placeholder="选择预计成交日期" style="width: 100%"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="下次跟进" prop="followDate">
                        <el-date-picker v-model="dataForm.followDate" type="date" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd" placeholder="选择下次跟进日期" style="width: 100%"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="商机状态" prop="status">
                        <el-select v-model="dataForm.status" filterable placeholder="请选择商机状态" class="w-percent-100">
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                                <span style="float: left">{{ item.label }}</span>
                                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.tip }}</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="商机来源" prop="source">
                        <el-select v-model="dataForm.source" filterable allow-create default-first-option placeholder="请选择商机来源" class="w-percent-100">
                            <el-option v-for="item in sourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="商机金额" prop="amount">
                        <el-input-number v-model="dataForm.amount" placeholder="商机金额" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="dataForm.remark" placeholder="备注" type="textarea"/>
            </el-form-item>
            <el-divider>意向产品</el-divider>
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
import ProductPick from './product-pick'
import CustomerPick from './customer-pick'

export default {
  inject: ['refresh'],
  mixins: [mixinBaseModule, mixinFormModule],
  components: { CustomerPick, ProductPick },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/crm/business/save`,
        dataFormUpdateURL: `/crm/business/update`,
        dataFormInfoURL: `/crm/business/info?id=`
      },
      // 当前编辑行
      productEditIndex: -1,
      // 状态选项
      statusOptions: [{
        value: 1,
        label: '阶段1',
        tip: '赢单率10%'
      }, {
        value: 2,
        label: '阶段2',
        tip: '赢单率30%'
      }, {
        value: 3,
        label: '阶段3',
        tip: '赢单率50%'
      }, {
        value: 10,
        label: '赢单',
        tip: '赢得订单'
      }, {
        value: -10,
        label: '输单',
        tip: '输了订单'
      }, {
        value: 0,
        label: '无效',
        tip: '商机无效'
      }],
      // 来源选项
      sourceOptions: [{
        value: '促销',
        label: '促销'
      }, {
        value: '广告',
        label: '广告'
      }, {
        value: '转介绍',
        label: '转介绍'
      }, {
        value: '陌拜',
        label: '陌拜'
      }, {
        value: '电话咨询',
        label: '电话咨询'
      }, {
        value: '网上咨询',
        label: '网上咨询'
      }],
      userList: [],
      dataForm: {
        id: '',
        customerId: '',
        source: '',
        remark: '',
        followDate: '',
        status: '',
        amount: '',
        dealDate: '',
        productDiscount: '',
        productPrice: '',
        salesUserId: '',
        name: '',
        productList: []
      }
    }
  },
  activated () {
    this.dataForm.id = this.$route.query.id || ''
    let tab = this.$store.state.contentTabs.filter(item => item.name === this.$route.name)[0]
    if (tab) {
      tab.title = this.dataForm.id ? '修改商机' : '新增商机'
      // 根据step刷新数据
      this.init()
    }

    if (!this.dataForm.id) {
      // 只有新增时候有意义
      let customerId = this.$route.query.customerId || '' // 客户id
      if (customerId) {
        this.getCustomerInfo(customerId)
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
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 商品变化
    onProductChangeHandle (index) {
      let item = this.dataForm.productList[index]
      item.discountPrice = item.salePrice * item.discount / 100
      item.totalPrice = item.discountPrice * item.qty
    },
    init () {
      this.formLoading = false
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        Promise.all([
          this.getUserList()
        ]).then(() => {
          this.initFormData()
        })
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
    },
    // 表单提交成功
    onFormSubmitSuccess (res) {
      this.$confirm('商机信息保存成功', this.$t('prompt.title'), {
        confirmButtonText: '查看详情',
        cancelButtonText: '关闭',
        type: 'success'
      }).then(() => {
        this.removeCurrentTab({ name: 'crm-business-info', query: { id: res.data.id } })
        // 关闭当前tab
      }).catch(() => {
        this.removeCurrentTab()
      })
    },
    // 选中客户
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
    // 获取用户列表
    getUserList () {
      this.$http.get('/uc/user/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.userList = res.data
        this.formLoading = false
      }).catch(resp => {
        this.formLoading = false
        this.$message.error(this.$t('prompt.apierror') + resp)
      })
    },
    // 删除产品
    removeProduct (index) {
      this.productEditIndex = -1
      this.dataForm.productList.splice(index, 1)
    }
  }
}
</script>
