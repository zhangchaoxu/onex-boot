<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-ba__subject">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.type" placeholder="类型" clearable>
            <el-option label="成人检测" :value="1"/>
            <el-option label="孩子检测" :value="2"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchDataForm.question" placeholder="题目" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('ba:subject:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('ba:subject:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('ba:subject:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="type" label="类型" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 1">成人检测</el-tag>
            <el-tag v-else-if="scope.row.type === 2" type="success">孩子检测</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" sortable="custom" label="排序" header-align="center" align="center" width="100"/>
        <el-table-column prop="question" label="题面" header-align="center" align="center" min-width="200" show-tooltip-when-overflow/>
        <el-table-column prop="options" label="选项" header-align="center" align="center" min-width="150"/>
        <el-table-column prop="answer" label="答案" header-align="center" align="center" min-width="100"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('ba:subject:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('ba:subject:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './subject-add-or-update'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/ba/subject/page',
        getDataListIsPage: true,
        exportURL: '/ba/subject/export',
        deleteURL: '/ba/subject/delete',
        deleteBatchURL: '/ba/subject/deleteBatch',
        deleteIsBatch: true
      },
      order: 'asc', // 排序，asc／desc
      orderField: 'sort', // 排序，字段
      searchDataForm: {
        type: '',
        question: ''
      }
    }
  }
}
</script>
