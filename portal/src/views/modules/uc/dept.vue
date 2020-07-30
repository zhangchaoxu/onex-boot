<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-uc__dept">
            <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
                <el-form-item>
                    <el-input v-model="searchDataForm.name" placeholder="名称" clearable maxlength="50" show-word-limit/>
                </el-form-item>
                <el-form-item>
                    <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('uc:dept:save')">
                    <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('uc:dept:export')">
                    <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
                </el-form-item>
            </el-form>
            <el-table v-loading="dataListLoading" :data="dataList" border @sort-change="dataListSortChangeHandle" style="width: 100%;" row-key="id">
                <el-table-column prop="name" :label="$t('base.name')" header-align="center" align="left" min-width="150"/>
                <el-table-column prop="remark" label="备注" header-align="center" align="center" min-width="150"/>
                <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
                    <template slot-scope="scope">
                        <el-button v-if="$hasPermission('uc:dept:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
                        <!-- 只有末级可以删除 -->
                        <el-button v-if="$hasPermission('uc:dept:delete') && scope.row.userNum === 0 && scope.row.lineNum === 0" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './dept-add-or-update'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/uc/dept/tree',
        getDataListIsPage: false,
        deleteURL: '/uc/dept/delete',
        deleteBatchURL: '/uc/dept/deleteBatch',
        deleteIsBatch: false,
        exportURL: '/uc/dept/export'
      },
      searchDataForm: {
        code: '',
        name: ''
      }
    }
  }

}
</script>
