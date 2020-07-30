<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-cms__site">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.code" placeholder="编码" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" placeholder="名称" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('cms:site:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="code" label="编码" header-align="center" align="center" width="120"></el-table-column>
        <el-table-column prop="name" label="名称" header-align="center" align="center" width="200"></el-table-column>
        <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" size="small" type="info">下线</el-tag>
            <el-tag v-else-if="scope.row.status === 1" size="small" type="success">上线</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" header-align="center" align="center"></el-table-column>
        <el-table-column prop="descr" label="描述" header-align="center" align="center"></el-table-column>
        <el-table-column prop="domain" label="网址" header-align="center" align="center"></el-table-column>
        <el-table-column prop="logo" label="LOGO" header-align="center" align="center"></el-table-column>
        <el-table-column prop="copyright" label="版权信息" header-align="center" align="center"></el-table-column>
        <el-table-column prop="keywords" label="关键词" header-align="center" align="center"></el-table-column>
        <el-table-column prop="imgs" label="图片" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('cms:site:info')" type="text" size="small" @click="previewHandle(scope.row.id)">{{ $t('preview') }}</el-button>
            <el-button v-if="$hasPermission('cms:site:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('cms:site:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './site-add-or-update'
export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/cms/site/page',
        getDataListIsPage: true,
        exportURL: '/cms/site/export',
        deleteURL: '/cms/site/delete',
        deleteBatchURL: '/cms/site/deleteBatch',
        deleteIsBatch: false
      },
      searchDataForm: {
        code: '',
        name: ''
      }
    }
  }
}
</script>
