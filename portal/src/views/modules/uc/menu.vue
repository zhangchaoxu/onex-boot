<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-uc__menu">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-button v-if="$hasPermission('uc:menu:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" row-key="id" border style="width: 100%;">
        <el-table-column prop="name" :label="$t('menu.name')" header-align="center" min-width="150"/>
        <el-table-column prop="icon" :label="$t('menu.icon')" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <i :class="scope.row.icon"/>
          </template>
        </el-table-column>
        <el-table-column prop="type" :label="$t('menu.type')" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 0" size="small">{{ $t('menu.type0') }}</el-tag>
            <el-tag v-else-if="scope.row.type === 1" size="small" type="success">{{ $t('menu.type1') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" :label="$t('menu.sort')" header-align="center" align="center" width="120"/>
        <el-table-column prop="url" :label="$t('menu.url')" header-align="center" align="center" show-overflow-tooltip min-width="150"/>
        <el-table-column prop="permissions" :label="$t('menu.permissions')" header-align="center" align="center" show-overflow-tooltip min-width="150"/>
        <el-table-column  :label="$t('handle')" fixed="right" header-align="center" align="center" width="150" v-if="$hasPermission('uc:menu:update') || $hasPermission('uc:menu:delete')">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('uc:menu:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('uc:menu:delete') && scope.row.children.length <= 0" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './menu-add-or-update'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/uc/menu/tree',
        deleteURL: '/uc/menu/delete',
        deleteBatchURL: '/uc/menu/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {

      }
    }
  }
}
</script>
