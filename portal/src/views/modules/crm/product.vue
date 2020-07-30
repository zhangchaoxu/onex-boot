<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-crm__product">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :id="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.categoryId" placeholder="分类" style="width: 100%" clearable>
            <el-option v-for="item in productCategoryList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-select v-model="searchDataForm.marketable" placeholder="在架" class="w-percent-100">
            <el-option v-for="item in marketableOptions" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.search" placeholder="名称/编号" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:product:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:product:import')">
          <el-button type="danger" @click="importHandle()">{{ $t('import') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:product:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width:100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="categoryName" label="类别" header-align="center" align="center" width="150"/>
        <el-table-column prop="name" label="产品名称" header-align="center" align="center" min-width="120" show-tooltip-when-overflow/>
        <el-table-column prop="sn" label="产品编码" header-align="center" align="center" min-width="120" show-tooltip-when-overflow/>
        <el-table-column prop="unit" label="单位" header-align="center" align="center" width="80"/>
        <el-table-column prop="salePrice" label="标准价" header-align="center" align="center" width="120"/>
        <el-table-column prop="content" label="描述" header-align="center" align="center" min-width="100" class-name="nowrap html link" :formatter="htmlFmt"/>
        <el-table-column prop="marketable" label="在架" header-align="center" align="center" width="70">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.marketable === 0" type="danger">下架</el-tag>
            <el-tag v-else-if="scope.row.marketable === 1" type="success">上架</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('crm:product:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('crm:product:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <!-- 弹窗, 导入 -->
      <import v-if="importVisible" ref="import" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './product-add-or-update'
import Import from './product-import'
import TenantPick from '../uc/tenant-pick'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate, Import, TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/crm/product/page',
        getDataListIsPage: true,
        exportURL: '/crm/product/export',
        deleteURL: '/crm/product/delete',
        deleteBatchURL: '/crm/product/deleteBatch',
        deleteIsBatch: false
      },
      // 产品分类列表
      productCategoryList: [],
      // 是否上架
      marketableOptions: [{
        value: 0,
        label: '下架'
      }, {
        value: 1,
        label: '上架'
      }],
      searchDataForm: {
        categoryId: '',
        marketable: '',
        search: ''
      }
    }
  },
  created () {
    this.getProductCategoryList()
  },
  methods: {
    // 获取文章分类列表
    getProductCategoryList () {
      this.$http.get('/crm/productCategory/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.productCategoryList = res.data
      }).catch(() => {
      })
    }
  }
}
</script>
