<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-uc__bill">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="middle-item">
          <el-input v-model="searchDataForm.userName" placeholder="用户" clearable readonly>
            <user-pick class="small-button" slot="append" :id="searchDataForm.userId" @onUserPicked="onUserPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.type" placeholder="类型" clearable>
            <el-option label="账户" value="balance"/>
            <el-option label="收入" value="income"/>
            <el-option label="积分" value="points"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
                  v-model="dateRange"
                  type="datetimerange"
                  @change="dateRangeChangeHandle"
                  :picker-options="dateRangePickerOptions"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :range-separator="$t('datePicker.range')"
                  :start-placeholder="$t('datePicker.start')"
                  :end-placeholder="$t('datePicker.end')">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('uc:bill:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('uc:bill:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="userName" label="用户" header-align="center" align="center" width="120"></el-table-column>
        <el-table-column prop="type" label="类型" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 'balance'">账户</el-tag>
            <el-tag v-else-if="scope.row.type === 'income'" type="success">收入</el-tag>
            <el-tag v-else-if="scope.row.type === 'points'" type="warning">积分</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="optType" label="操作类型" header-align="center" align="center" width="120"/>
        <el-table-column prop="amount" label="金额" header-align="center" align="center" width="120"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        <el-table-column prop="createTime" label="操作时间" header-align="center" align="center" width="180"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('uc:bill:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('uc:bill:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="mixinListModuleOptions.getDataListIsPage"
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="limit"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="pageSizeChangeHandle"
        @current-change="pageCurrentChangeHandle"/>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import mixinBaseModule from '@/mixins/base-module'
import AddOrUpdate from './bill-add-or-update'
import UserPick from './user-pick'
import TenantPick from '../uc/tenant-pick'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { UserPick, AddOrUpdate, TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/uc/bill/page',
        getDataListIsPage: true,
        exportURL: '/uc/bill/export',
        deleteURL: '/uc/bill/delete',
        deleteBatchURL: '/uc/bill/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        userId: '',
        type: '',
        optType: '',
        startCreateTime: '',
        endCreateTime: '',
        tenantId: '',
        tenantName: ''
      }
    }
  }
}
</script>
