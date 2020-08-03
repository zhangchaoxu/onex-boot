<template>
    <el-card shadow="never" class="aui-card--fill">
        <el-divider>商机信息</el-divider>
        <el-form :model="dataForm" ref="dataForm" label-width="120px" v-loading="formLoading" size="small">
            <el-row>
                <el-col :span="8">
                    <el-form-item label="客户" prop="customerId">
                        <span>{{ dataForm.customerName }}</span>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="商机名称" prop="name">
                        <span>{{ dataForm.name }}</span>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="销售员" prop="salesUserId">
                        <span>{{ dataForm.salesUserName }}</span>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="商机状态" prop="status">
                        <template slot-scope="scope">
                            <el-tag v-if="dataForm.status === 1" type="warning">阶段1</el-tag>
                            <el-tag v-else-if="dataForm.status === 2" type="warning">阶段2</el-tag>
                            <el-tag v-else-if="dataForm.status === 3" type="warning">阶段3</el-tag>
                            <el-tag v-else-if="dataForm.status === 10" type="success">赢单</el-tag>
                            <el-tag v-else-if="dataForm.status === -10" type="danger">输单</el-tag>
                            <el-tag v-else-if="dataForm.status === 0" type="info">无效</el-tag>
                        </template>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="商机来源" prop="source">
                        <span>{{ dataForm.source }}</span>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="商机金额" prop="amount">
                        <span>{{ dataForm.amount }}</span>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="预计成交" prop="dealDate">
                        <span>{{ dataForm.dealDate | dateDayFilter }}</span>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <el-form-item label="备注" prop="remark">
                        <span>{{ dataForm.remark }}</span>
                    </el-form-item>
                </el-col>
            </el-row>
            <div style="text-align: center; margin-top: 20px;" v-if="$hasPermission('crm:business:update') || $hasPermission('crm:contract:save')">
                <el-button v-if="$hasPermission('crm:business:update')" type="warning" size="small" @click="$router.push({ name: 'crm-business-add-or-update', query: { id: dataForm.id } })">{{
                $t('update') }}</el-button>
                <el-button v-if="$hasPermission('crm:contract:save')" type="success" size="small" @click="$router.push({ name: 'crm-contract-add-or-update', query: { businessId: dataForm.id } })">
                    生成合同</el-button>
            </div>
        </el-form>
        <el-divider>意向产品</el-divider>
        <el-table size="small" :data="dataForm.productList" v-loading="formLoading" border ref="productListTable"
                  show-summary :summary-method="getProductSummaries" style="width: 100%;">
            <el-table-column prop="productName" label="产品名" header-align="center" align="center" min-width="100"/>
            <el-table-column prop="productUnit" label="单位" header-align="center" align="center" width="100"/>
            <el-table-column prop="salePrice" label="标准价" header-align="center" align="center" width="100"/>
            <el-table-column prop="discount" label="折扣" header-align="center" align="center" width="100"/>
            <el-table-column prop="discountPrice" label="折扣价" header-align="center" align="center" width="100"/>
            <el-table-column prop="qty" label="数量" header-align="center" align="center" width="100"/>
            <el-table-column prop="totalPrice" label="小计" header-align="center" align="center" width="100"/>
        </el-table>
        <el-divider>商机记录</el-divider>
        <div class="mod-crm__business-log">
            <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
                <el-form-item class="small-item">
                    <el-select v-model="searchDataForm.type" placeholder="记录类型" clearable>
                        <el-option label="跟进" value="followup"/>
                        <el-option label="修改" value="edit"/>
                        <el-option label="创建" value="new"/>
                    </el-select>
                </el-form-item>
                <el-form-item class="small-item">
                    <el-input v-model="searchDataForm.content" placeholder="内容" clearable/>
                </el-form-item>
                <el-form-item>
                    <el-button @click="getDataList()">{{ $t('query') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('crm:businessLog:save')">
                    <el-button type="primary" @click="addOrUpdateHandle()">跟进</el-button>
                </el-form-item>
            </el-form>
            <el-table size="small" v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
                <el-table-column prop="type" label="记录类型" header-align="center" align="center" width="100">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.type === 'followup'" type="success">跟进</el-tag>
                        <el-tag v-else-if="scope.row.type === 'new'" type="info">创建</el-tag>
                        <el-tag v-else-if="scope.row.type === 'edit'" type="warning">修改</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status === 1" type="warning">阶段1</el-tag>
                        <el-tag v-else-if="scope.row.status === 2" type="warning">阶段2</el-tag>
                        <el-tag v-else-if="scope.row.status === 3" type="warning">阶段3</el-tag>
                        <el-tag v-else-if="scope.row.status === 10" type="success">赢单</el-tag>
                        <el-tag v-else-if="scope.row.status === -10" type="danger">输单</el-tag>
                        <el-tag v-else-if="scope.row.status === 0" type="info">无效</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="logDate" label="记录时间" header-align="center" align="center" width="120" :formatter="dateDayFmt"/>
                <el-table-column prop="nextFollowDate" label="下次跟进时间" header-align="center" align="center" width="120" :formatter="dateDayFmt"/>
                <el-table-column prop="content" label="内容" header-align="center" align="center" show-tooltip-when-overflow/>
                <el-table-column prop="attachment" label="附件" header-align="center" align="center">
                    <template slot-scope="scope" v-if="scope.row.attachment">
                        <file-viewer v-for="(item, index) in scope.row.attachment.split(',')" :key="index" type="private" privateParamCode="OSS_CFG_PRI" :file="{ name: '附件' + (index + 1), url:
                        item }"/>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" header-align="center" align="center" width="150" :formatter="dateTimeShortFmt"/>
                <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
                    <template slot-scope="scope">
                        <el-button v-if="$hasPermission('crm:businessLog:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
                        <el-button v-if="$hasPermission('crm:businessLog:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                    v-if="mixinListModuleOptions.getDataListIsPage"
                    :current-page="page"
                    :page-sizes="[10, 20, 50, 100]"
                    :page-size="limit"
                    :total="total"
                    hide-on-single-page
                    layout="total, prev, pager, next"
                    @size-change="pageSizeChangeHandle"
                    @current-change="pageCurrentChangeHandle"/>
            <!-- 弹窗, 新增 / 修改 -->
            <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
        </div>
    </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import mixinFormModule from '@/mixins/form-module'
import FileViewer from '@/components/file-viewer'
import AddOrUpdate from './business-log-add-or-update'

export default {
  mixins: [mixinBaseModule, mixinListModule, mixinFormModule],
  components: { AddOrUpdate, FileViewer },
  data: function () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormInfoURL: `/crm/business/info?id=`
      },
      // 列表模块参数
      mixinListModuleOptions: {
        getDataListURL: '/crm/businessLog/page',
        getDataListIsPage: true,
        exportURL: '/crm/businessLog/export',
        deleteURL: '/crm/businessLog/delete',
        deleteIsBatch: false,
        activatedIsNeed: false
      },
      dataForm: {
        id: '',
        name: '',
        customerName: '',
        source: '',
        remark: '',
        status: '',
        amount: '',
        dealDate: '',
        productDiscount: '',
        productPrice: '',
        salesUserId: '',
        productList: []
      },
      searchDataForm: {
        businessId: '',
        type: '',
        content: ''
      }
    }
  },
  activated () {
    this.dataForm.id = this.$route.query.id
    if (!this.dataForm.id) {
      this.formLoading = false
      this.$message.error('参数不能为空')
    } else {
      this.searchDataForm.businessId = this.dataForm.id
      this.initFormData()
    }
  },
  methods: {
    addOrUpdateHandle (id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.clear()
        this.$refs.addOrUpdate.dataForm.id = id
        this.$refs.addOrUpdate.dataForm.businessId = this.dataForm.id
        this.$refs.addOrUpdate.dataFormMode = !id ? 'save' : 'update'
        this.$refs.addOrUpdate.init()
      })
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      this.getDataList()
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
    }
  }
}
</script>
