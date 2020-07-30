<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-shop__goods-category">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchDataForm.name" placeholder="名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:goodsCategory:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;" row-key="id">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="name" label="名称" header-align="center" align="left" min-width="100"/>
        <el-table-column prop="sort" label="排序" header-align="center" align="center" width="100"/>
        <el-table-column prop="logo" label="图标" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-image v-if="scope.row.logo" lazy class="table-img" :src="scope.row.logo.split(',')[0]" :preview-src-list="scope.row.logo.split(',')" fit="cover"/>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="描述" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('shop:goodsCategory:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('shop:goodsCategory:delete') && scope.row.children && scope.row.children.length === 0" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './goods-category-add-or-update'
import TenantPick from '../uc/tenant-pick'

export default {
  mixins: [mixinListModule, mixinBaseModule],
  components: { AddOrUpdate, TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/shop/goodsCategory/tree',
        getDataListIsPage: false,
        exportURL: '/shop/goodsCategory/export',
        deleteURL: '/shop/goodsCategory/delete',
        deleteBatchURL: '/shop/goodsCategory/deleteBatch',
        deleteIsBatch: false
      },
      searchDataForm: {
        name: '',
        tenantId: '',
        tenantName: ''
      }
    }
  }
}
</script>
