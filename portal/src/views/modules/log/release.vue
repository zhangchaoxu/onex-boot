<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-log__release">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.code" placeholder="编码" clearable/>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchDataForm.content" placeholder="更新内容" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('log:release:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('log:release:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="code" sortable="custom" label="编码" header-align="center" align="center" width="100"/>
        <el-table-column prop="name" label="名称" header-align="center" align="center" width="120"/>
        <el-table-column prop="versionNo" sortable="custom" label="版本号" header-align="center" align="center" width="100"/>
        <el-table-column prop="versionName" label="版本名称" header-align="center" align="center" width="120"/>
        <el-table-column prop="content" label="更新内容" header-align="center" align="center" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="downloadLink" label="下载链接" header-align="center" align="center" min-width="120" show-overflow-tooltip>
          <template slot-scope="scope">
            <el-link type="primary" v-if="scope.row.downloadLink" :underline="false" :href="scope.row.downloadLink">{{ scope.row.downloadLink }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="forceUpdate" label="强制更新" header-align="center" align="center" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.forceUpdate === 0" type="danger">否</el-tag>
            <el-tag v-else-if="scope.row.forceUpdate === 1">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('log:release:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('log:release:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './release-add-or-update'
export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/log/release/page',
        getDataListIsPage: true,
        exportURL: '/log/release/export',
        deleteURL: '/log/release/delete',
        deleteIsBatch: false
      },
      searchDataForm: {
        code: '',
        type: ''
      }
    }
  }
}
</script>
