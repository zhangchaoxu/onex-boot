<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-cms__article-category">
            <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
                <el-form-item class="small-item">
                    <el-input v-model="searchDataForm.name" placeholder="名称" clearable/>
                </el-form-item>
                <el-form-item>
                    <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('cms:articleCategory:save')">
                    <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
                </el-form-item>
            </el-form>
            <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
                <el-table-column prop="name" label="名称" header-align="center" align="center"/>
                <el-table-column prop="imgs" label="封面" header-align="center" align="center">
                    <template slot-scope="scope">
                        <el-image v-if="scope.row.imgs" lazy class="table-img" :src="scope.row.imgs.split(',')[0]" :preview-src-list="scope.row.imgs.split(',')" fit="cover"/>
                    </template>
                </el-table-column>
                <el-table-column prop="remark" label="备注" header-align="center" align="center"/>
                <el-table-column prop="sort" label="排序" header-align="center" align="center" sortable="custom" width="100"/>
                <el-table-column prop="updateTime" label="更新时间" header-align="center" align="center" width="180"/>
                <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
                    <template slot-scope="scope">
                        <el-button v-if="$hasPermission('cms:articleCategory:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
                        <el-button v-if="$hasPermission('cms:articleCategory:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
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
import AddOrUpdate from './article-category-add-or-update'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/cms/articleCategory/page',
        getDataListIsPage: true,
        exportURL: '/cms/articleCategory/export',
        deleteURL: '/cms/articleCategory/delete',
        deleteBatchURL: '/cms/articleCategory/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        name: '',
        code: ''
      }
    }
  }
}
</script>
