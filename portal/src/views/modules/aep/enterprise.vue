<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-aep__enterprise">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-input v-model="searchDataForm.search" placeholder="名称/电话/标签" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('aep:enterprise:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('aep:enterprise:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column prop="name" label="名称" header-align="center" align="center" min-width="100" show-tooltip-when-overflow/>
        <el-table-column prop="deviceCount" label="设备数" header-align="center" align="center" min-width="100"/>
        <el-table-column prop="contacts" label="联系人" header-align="center" align="center" width="120"/>
        <el-table-column prop="telephone" label="联系电话" header-align="center" align="center" width="120"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center" min-width="100" show-tooltip-when-overflow/>
        <el-table-column prop="status" label="状态" header-align="center" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="success">正常</el-tag>
            <el-tag v-else type="info">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签" header-align="center" align="center">
          <template slot-scope="scope" v-if="scope.row.tags">
            <el-tag v-for="item in scope.row.tags.split(',')" :key="item" type="info" style="margin-left: 2px">{{item}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" header-align="center" align="center" min-width="120" show-tooltip-when-overflow>
          <template slot-scope="scope" v-if="scope.row.address">
            <amap-loc-view :poi="{ regionName: scope.row.regionName, regionCd: scope.row.regionCd, address: scope.row.address, lat: scope.row.lat, lng: scope.row.lng }" />{{ scope.row.regionName }}{{ scope.row.address }}
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('aep:enterprise:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('aep:enterprise:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
        @current-change="pageCurrentChangeHandle">
      </el-pagination>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './enterprise-add-or-update'
import AmapLocView from '@/components/amap-loc-view'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate, AmapLocView },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/aep/enterprise/page',
        getDataListIsPage: true,
        exportURL: '/aep/enterprise/export',
        deleteURL: '/aep/enterprise/delete',
        deleteBatchURL: '/aep/enterprise/deleteBatch',
        deleteIsBatch: false
      },
      searchDataForm: {
        search: ''
      }
    }
  }
}
</script>
