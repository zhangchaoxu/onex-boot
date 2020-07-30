<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-pay__channel">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" placeholder="名称" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('pay:channel:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('pay:channel:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" width="150"/>
        <el-table-column prop="name" label="名称" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="payType" label="支付类型" header-align="center" align="center">
          <template slot-scope="scope">
            <span v-if="scope.row.payType === 'WECHAT_JSAPI'">微信JSAPI</span>
            <span v-else-if="scope.row.payType === 'WECHAT_NATIVE'">微信NATIVE</span>
            <span v-else-if="scope.row.payType === 'WECHAT_APP'">微信APP</span>
            <span v-else-if="scope.row.payType === 'WECHAT_MWEB'">微信MWEV</span>
            <span v-else-if="scope.row.payType === 'ALIPAY'">支付宝</span>
            <span v-else>{{ scope.row.payType }}</span>
          </template>>
        </el-table-column>
        <el-table-column prop="param" label="参数" header-align="center" align="center" class-name="nowrap json link"/>
        <el-table-column prop="notifyUrl" label="回调地址" header-align="center" align="center" class-name="nowrap text link"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" size="small" type="danger">停用</el-tag>
            <el-tag v-else size="small" type="success">启用</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('pay:channel:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('pay:channel:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './channel-add-or-update'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/pay/channel/page',
        getDataListIsPage: true,
        exportURL: '/pay/channel/export',
        deleteURL: '/pay/channel/delete',
        deleteBatchURL: '/pay/channel/deleteBatch',
        deleteIsBatch: false
      },
      searchDataForm: {
        name: ''
      }
    }
  }
}
</script>
