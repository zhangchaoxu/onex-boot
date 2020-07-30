<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-uc__user-oauth">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.id" placeholder="id" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('uc:userOauth:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('uc:userOauth:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('uc:userOauth:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="appid" label="appid" header-align="center" align="center"/>
        <el-table-column prop="unionid" label="unionid" header-align="center" align="center"/>
        <el-table-column prop="openid" label="openid" header-align="center" align="center"></el-table-column>
        <el-table-column prop="type" label="类型 dingtalk/wechat/apple" header-align="center" align="center"></el-table-column>
        <el-table-column prop="nickname" label="昵称" header-align="center" align="center"></el-table-column>
        <el-table-column prop="avatar" label="头像" header-align="center" align="center"></el-table-column>
        <el-table-column prop="ext" label="附属信息" header-align="center" align="center"></el-table-column>
        <el-table-column prop="userId" label="对应的用户id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('uc:userOauth:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('uc:userOauth:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
export default {
  mixins: [mixinListModule],
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/uc/userOauth/page',
        getDataListIsPage: true,
        exportURL: '/uc/userOauth/export',
        deleteURL: '/uc/userOauth/delete',
        deleteBatchURL: '/uc/userOauth/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        id: ''
      }
    }
  }
}
</script>
