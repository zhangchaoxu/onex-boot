<template>
  <el-card shadow="never" class="aui-card--fill">
    <el-divider>合同信息</el-divider>
    <el-form :model="dataForm" ref="dataForm" label-width="120px" v-loading="formLoading" size="small">
      <el-row>
        <el-col :span="12">
          <el-form-item label="客户" prop="customerName">
            <span>{{ dataForm.customerName }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="商机" prop="businessName">
            <el-link :underline="false" type="primary" v-if="dataForm.businessId" @click="$router.push({ name: 'crm-business-info', query: { id: dataForm.businessId } })">{{ dataForm.businessName }}</el-link>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="合同名称" prop="name">
            <span>{{ dataForm.name }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合同编号" prop="code">
            <span>{{ dataForm.code }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="有效期" prop="validStartDate">
            <span>{{ dataForm.validStartDate | dateDayFilter }}->{{ dataForm.validEndDate | dateDayFilter }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合同金额" prop="amount">
            <span>{{ dataForm.amount }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="销售员" prop="salesUserName">
            <span>{{ dataForm.salesUserName }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="销售提成" prop="salesPercentageAmount">
            <span>{{ dataForm.salesPercentageAmount }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="签约日期" prop="orderDate">
            <span>{{ dataForm.contractDate | dateDayFilter }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付方式" prop="paytype">
            <span>{{ dataForm.paytype }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="客户签约人" prop="customerSigner">
            <span>{{ dataForm.customerSigner }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="公司签约人" prop="supplierSigner">
            <span>{{ dataForm.supplierSigner }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="备注" prop="remark">
            <span>{{ dataForm.remark }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="附件" prop="attachment">
            <file-viewer v-if="dataForm.attachment" type="private" privateParamCode="OSS_CFG_PRI" :file="{ name: '附件查看', url: dataForm.attachment }"/>
          </el-form-item>
        </el-col>
      </el-row>
      <div style="text-align: center; margin-top: 20px;" v-if="$hasPermission('crm:contract:update')">
        <el-button type="warning" size="small" @click="$router.push({ name: 'crm-contract-add-or-update', query: { id: dataForm.id } })">{{ $t('update') }}</el-button>
      </div>
    </el-form>
    <el-divider>产品信息</el-divider>
    <el-table :data="dataForm.productList" v-loading="formLoading" border ref="productListTable"
              size="small" show-summary :summary-method="getProductSummaries" style="width: 100%;">
      <el-table-column prop="productName" label="产品名" header-align="center" align="center" min-width="100"/>
      <el-table-column prop="productUnit" label="单位" header-align="center" align="center" width="100"/>
      <el-table-column prop="salePrice" label="标准价" header-align="center" align="center" width="100"/>
      <el-table-column prop="discount" label="折扣" header-align="center" align="center" width="100"/>
      <el-table-column prop="discountPrice" label="折扣价" header-align="center" align="center" width="100"/>
      <el-table-column prop="qty" label="数量" header-align="center" align="center" width="100"/>
      <el-table-column prop="totalPrice" label="小计" header-align="center" align="center" width="100"/>
    </el-table>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinFormModule from '@/mixins/form-module'
import AddOrUpdate from './business-log-add-or-update'
import FileViewer from '@/components/file-viewer'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  components: { AddOrUpdate, FileViewer },
  data: function () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormInfoURL: `/crm/contract/info?id=`
      },
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
    if (!this.dataForm.id) {
      this.formLoading = false
      this.$message.error('参数不能为空')
    } else {
      this.initFormData()
    }
  },
  methods: {
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
    }
  }
}
</script>
