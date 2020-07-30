<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-shop__order-benefit">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.id" placeholder="id" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:orderBenefit:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:orderBenefit:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:orderBenefit:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createId" label="创建者" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" header-align="center" align="center"></el-table-column>
        <el-table-column prop="updateId" label="更新者" header-align="center" align="center"></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" header-align="center" align="center"></el-table-column>
        <el-table-column prop="deleted" label="删除标记" header-align="center" align="center"></el-table-column>
        <el-table-column prop="orderId" label="订单id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="orderItemId" label="订单明细id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="goodsId" label="商品id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="tenantId" label="租户id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="tenantName" label="租户名称" header-align="center" align="center"></el-table-column>
        <el-table-column prop="benefitUserId" label="收益用户id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="benefitPrice" label="收益金额" header-align="center" align="center"></el-table-column>
        <el-table-column prop="benefitStatus" label="收益状态 0 未发放 1 已发放 -1 已回收" header-align="center" align="center"></el-table-column>
        <el-table-column prop="payOrderId" label="支付订单id" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('shop:orderBenefit:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('shop:orderBenefit:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './order-benefit-add-or-update'
export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/shop/orderBenefit/page',
        getDataListIsPage: true,
        exportURL: '/shop/orderBenefit/export',
        deleteURL: '/shop/orderBenefit/delete',
        deleteBatchURL: '/shop/orderBenefit/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        id: ''
      }
    }
  }
}
</script>
