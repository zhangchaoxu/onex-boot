<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-shop__user-rank">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" placeholder="名称" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:userRank:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="name" label="名称" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="sort" sortable="custom" label="排序" header-align="center" align="center" min-width="100"/>
        <el-table-column prop="amount" label="消费金额" header-align="center" align="center" width="120"/>
        <el-table-column prop="defaultItem" label="默认项" header-align="center" align="center" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.defaultItem === 0" type="danger">否</el-tag>
            <el-tag v-else-if="scope.row.defaultItem === 1" type="success">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scale" label="优惠比例" header-align="center" align="center" width="80"/>
        <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="info">停用</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('shop:userRank:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('shop:userRank:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './user-rank-add-or-update'
import TenantPick from '../uc/tenant-pick'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate, TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/shop/userRank/page',
        getDataListIsPage: true,
        exportURL: '/shop/userRank/export',
        deleteURL: '/shop/userRank/delete',
        deleteIsBatch: false
      },
      searchDataForm: {
        name: ''
      }
    }
  }
}
</script>
