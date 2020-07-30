<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-crm__contract-product">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.id" placeholder="id" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:contractProduct:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:contractProduct:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:contractProduct:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="id" label="id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createId" label="创建者" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" header-align="center" align="center"></el-table-column>
        <el-table-column prop="updateId" label="更新者" header-align="center" align="center"></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" header-align="center" align="center"></el-table-column>
        <el-table-column prop="deleted" label="删除标记" header-align="center" align="center"></el-table-column>
        <el-table-column prop="customerId" label="客户id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="contractId" label="合同id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="tenantId" label="租户id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="tenantName" label="租户名称" header-align="center" align="center"></el-table-column>
        <el-table-column prop="productId" label="产品id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="productUnit" label="产品单位" header-align="center" align="center"></el-table-column>
        <el-table-column prop="productCategoryId" label="产品分类id" header-align="center" align="center"></el-table-column>
        <el-table-column prop="productCategoryName" label="产品分类名称" header-align="center" align="center"></el-table-column>
        <el-table-column prop="qty" label="数量" header-align="center" align="center"></el-table-column>
        <el-table-column prop="salePrice" label="产品标准价格" header-align="center" align="center"></el-table-column>
        <el-table-column prop="discount" label="折扣" header-align="center" align="center"></el-table-column>
        <el-table-column prop="discountPrice" label="产品折扣价格" header-align="center" align="center"></el-table-column>
        <el-table-column prop="totalPrice" label="价格小计" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('crm:contractProduct:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('crm:contractProduct:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './contract-product-add-or-update'
export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/crm/contractProduct/page',
        getDataListIsPage: true,
        exportURL: '/crm/contractProduct/export',
        deleteURL: '/crm/contractProduct/delete',
        deleteBatchURL: '/crm/contractProduct/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        id: ''
      }
    }
  }
}
</script>
