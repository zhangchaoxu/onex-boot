<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-sys__ad">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.position" placeholder="位置" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" placeholder="标题" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('sys:ad:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="position" label="位置" header-align="center" align="center" with="120"/>
        <el-table-column prop="name" label="标题" header-align="center" align="center" min-width="150"/>
        <el-table-column prop="needLogin" label="登录" header-align="center" align="center" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.needLogin === 0" type="info">不需要</el-tag>
            <el-tag v-else-if="scope.row.needLogin === 1">需要</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="link" label="链接" header-align="center" align="center"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center"/>
        <el-table-column prop="imgs" label="封面图" header-align="center" align="center">
          <template slot-scope="scope">
            <el-image v-if="scope.row.imgs" lazy class="table-img" :src="scope.row.imgs.split(',')[0]" :preview-src-list="scope.row.imgs.split(',')" fit="cover"/>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" header-align="center" align="center" width="80" sortable="custom"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('sys:ad:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('sys:ad:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './ad-add-or-update'
import TenantPick from '../uc/tenant-pick'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate, TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/sys/axd/page',
        getDataListIsPage: true,
        exportURL: '/sys/axd/export',
        deleteURL: '/sys/axd/delete',
        deleteBatchURL: '/sys/axd/deleteBatch',
        deleteIsBatch: false
      },
      searchDataForm: {
        position: '',
        name: '',
        tenantId: '',
        tenantName: ''
      }
    }
  }
}
</script>
