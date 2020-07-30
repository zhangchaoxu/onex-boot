<template>
  <el-card shadow="never" :class="[{'aui-card--embed': mode === 'embed'}, 'aui-card--fill']">
    <div class="mod-crm__contract">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" :size="mode === 'embed' ? 'small' : ''">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin') && mode !== 'embed'">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="large-item" v-if="mode !== 'embed'">
          <el-input v-model="searchDataForm.customerName" placeholder="客户" readonly>
            <customer-pick class="small-button" slot="append" :id="searchDataForm.customerId" @onCustomerPicked="onCustomerPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.search" placeholder="名称/编号" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:contract:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:contract:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border
                :size="mode === 'embed' ? 'small' : ''"
                @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="customerName" label="客户" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="name" label="合同名称" header-align="center" align="center" min-width="300">
          <template slot-scope="scope">
            <el-link type="primary" @click="previewHandle(scope.row.id)" :underline="false">{{ scope.row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="合同编号" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="contractDate" label="签约日期" header-align="center" align="center" width="120" :formatter="dateDayFmt"/>
        <el-table-column prop="salesUserName" label="销售" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="businessName" label="关联商机" header-align="center" align="center" min-width="120" class="nowrap">
          <template slot-scope="scope">
            <el-link type="primary" @click="previewBusinessHandle(scope.row.businessId)" :underline="false">{{ scope.row.businessName }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="合同金额" header-align="center" align="center" width="120"/>
        <el-table-column prop="paytype" label="支付方式" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('crm:contract:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('crm:contract:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
    </div>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './contract-add-or-update'
import TenantPick from '../uc/tenant-pick'
import CustomerPick from './customer-pick'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate, TenantPick, CustomerPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/crm/contract/page',
        getDataListIsPage: true,
        exportURL: '/crm/contract/export',
        deleteURL: '/crm/contract/delete',
        deleteBatchURL: '/crm/contract/deleteBatch',
        deleteIsBatch: false,
        activatedIsNeed: true
      },
      searchDataForm: {
        tenantId: '',
        search: '',
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
      this.$router.push({ name: 'crm-contract-add-or-update', query: query })
    },
    // 查看
    previewHandle (id) {
      this.$router.push({ name: 'crm-contract-info', query: { id: id } })
    },
    // 查看
    previewBusinessHandle (id) {
      this.$router.push({ name: 'crm-business-info', query: { id: id } })
    }
  }
}
</script>
