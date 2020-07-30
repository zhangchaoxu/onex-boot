<template>
  <el-card shadow="never" :class="[{'aui-card--embed': mode === 'embed'}, 'aui-card--fill']">
    <div class="mod-crm__business">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" :size="mode === 'embed' ? 'small' : ''">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin') && mode !== 'embed'">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :id="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="large-item" v-if="mode !== 'embed'">
          <el-input v-model="searchDataForm.customerName" placeholder="客户" readonly>
            <customer-pick class="small-button" slot="append" :id="searchDataForm.customerId" @onCustomerPicked="onCustomerPicked" />
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" placeholder="名称" clearable/>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-select v-model="searchDataForm.status" placeholder="状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
              <span style="float: left">{{ item.label }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.tip }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.source" placeholder="来源" filterable allow-create clearable>
            <el-option v-for="item in sourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:business:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:business:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border
                :size="mode === 'embed' ? 'small' : ''"
                @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="customerName" label="客户" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="name" label="名称" header-align="center" align="center" min-width="120" class="nowrap">
          <template slot-scope="scope">
            <el-link type="primary" @click="previewHandle(scope.row.id)" :underline="false">{{ scope.row.name }}</el-link>
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
        <el-table-column prop="salesUserName" label="销售" header-align="center" align="center" width="120"/>
        <el-table-column prop="source" label="商机来源" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
        <el-table-column prop="amount" label="金额" header-align="center" align="center" width="120"/>
        <el-table-column prop="dealDate" label="预计成交" header-align="center" align="center" width="120" :formatter="dateDayFmt"/>
        <el-table-column prop="followDate" sortable="custom" label="下次跟进" header-align="center" align="center" width="120" :formatter="dateDayFmt"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="180">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('crm:businessLog:save')" type="text" size="small" @click="addFollowupLogHandle(scope.row.id)">跟进</el-button>
            <el-button v-if="$hasPermission('crm:contract:save')" type="text" size="small" @click="addContractHandle(scope.row.id)">生成合同</el-button>
            <el-button v-if="$hasPermission('crm:business:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('crm:business:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="mixinListModuleOptions.getDataListIsPage"
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="limit"
        :total="total"
        :hide-on-single-page="mode === 'embed'"
        :small="mode === 'embed'"
        :layout="mode === 'embed' ? 'total, prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
        @size-change="pageSizeChangeHandle"
        @current-change="pageCurrentChangeHandle"/>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
      <!-- 弹窗, 新增 / 修改 -->
      <info v-if="infoVisible" ref="info" @refreshDataList="getDataList"/>
      <!-- 弹窗, 新增 / 修改 -->
      <log-add-or-update v-if="logAddOrUpdateVisible" ref="logAddOrUpdate" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './business-add-or-update'
import Info from './business-info'
import LogAddOrUpdate from './business-log-add-or-update'
import TenantPick from '../uc/tenant-pick'
import CustomerPick from './customer-pick'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { Info, AddOrUpdate, LogAddOrUpdate, TenantPick, CustomerPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/crm/business/page',
        getDataListIsPage: true,
        exportURL: '/crm/business/export',
        deleteURL: '/crm/business/delete',
        deleteBatchURL: '/crm/business/deleteBatch',
        deleteIsBatch: false,
        activatedIsNeed: true
      },
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
      infoVisible: false, // 查看信息，弹窗visible状态
      logAddOrUpdateVisible: false, // 新增跟进
      searchDataForm: {
        name: '',
        status: '',
        tenantId: '',
        customerName: '',
        customerId: ''
      }
    }
  },
  methods: {
    // 新增/修改
    addOrUpdateHandle (id) {
      let query = {}
      if (id) {
        query = { id: id }
      } else if (this.searchDataForm.customerId) {
        query = { customerId: this.searchDataForm.customerId }
      }
      this.$router.push({ name: 'crm-business-add-or-update', query: query })
    },
    // 查看
    previewHandle (id) {
      this.$router.push({ name: 'crm-business-info', query: { id: id } })
    },
    // 添加合同
    addContractHandle (id) {
      this.$router.push({ name: 'crm-contract-add-or-update', query: { businessId: id } })
    },
    // 商机跟进
    addFollowupLogHandle (id) {
      this.logAddOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.logAddOrUpdate.dataForm.id = ''
        this.$refs.logAddOrUpdate.dataForm.businessId = id
        this.$refs.logAddOrUpdate.init()
      })
    }
  }
}
</script>
