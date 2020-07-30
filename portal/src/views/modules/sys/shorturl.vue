<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-sys__shorturl">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" placeholder="名称" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.code" placeholder="短地址编码" clearable/>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchDataForm.url" placeholder="原始地址" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('sys:shorturl:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('sys:shorturl:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="name" label="名称" header-align="center" align="center" width="150"/>
        <el-table-column prop="url" label="地址" header-align="center" align="center" min-width="200">
          <template slot-scope="scope">
            <el-link type="primary" v-if="scope.row.url" :underline="false" :href="scope.row.url">{{ scope.row.url }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="短地址路径" header-align="center" align="center" width="150"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center"/>
        <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="danger">不开放</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">开放</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('sys:shorturl:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('sys:shorturl:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './shorturl-add-or-update'
export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/sys/shorturl/page',
        getDataListIsPage: true,
        exportURL: '/sys/shorturl/export',
        deleteURL: '/sys/shorturl/delete',
        deleteBatchURL: '/sys/shorturl/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        name: '',
        url: '',
        code: ''
      }
    }
  }
}
</script>
