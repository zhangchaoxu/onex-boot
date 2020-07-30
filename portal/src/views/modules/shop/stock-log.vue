<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-shop__stock-log">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-select v-model="searchDataForm.type" placeholder="类型" clearable>
            <el-option label="入库" :value="0"/>
            <el-option label="出库" :value="1"/>
          </el-select>
        </el-form-item>
        <el-form-item class="middle-item">
          <el-input v-model="searchDataForm.goodsName" placeholder="商品" readonly>
            <goods-pick class="small-button" slot="append" :id="searchDataForm.goodsId" @onGoodsPicked="onGoodsPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-date-picker
                  v-model="dateRange"
                  type="datetimerange"
                  @change="dateRangeChangeHandle"
                  :picker-options="dateRangePickerOptions"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :range-separator="$t('datePicker.range')"
                  :start-placeholder="$t('datePicker.start')"
                  :end-placeholder="$t('datePicker.end')">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:stockLog:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:stockLog:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" width="150" show-tooltip-when-overflow v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="type" label="类型" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 0" type="success">入库</el-tag>
            <el-tag v-else-if="scope.row.type === 1" type="danger">出库</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="goodsName" label="商品" header-align="center" align="center" min-width="120" show-tooltip-when-overflow/>
        <el-table-column prop="inQty" label="入库数量" header-align="center" align="center" width="120"/>
        <el-table-column prop="outQty" label="出库数量" header-align="center" align="center" width="120"/>
        <el-table-column prop="stock" label="操作后库存" header-align="center" align="center" width="120"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
        <el-table-column prop="createName" label="操作人" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
        <el-table-column prop="createTime" label="操作时间" header-align="center" align="center" width="160"/>
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
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './stock-log-add-or-update'
import TenantPick from '../uc/tenant-pick'
import GoodsPick from './goods-pick'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate, TenantPick, GoodsPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/shop/stockLog/page',
        getDataListIsPage: true,
        exportURL: '/shop/stockLog/export',
        deleteURL: '/shop/stockLog/delete',
        deleteIsBatch: false
      },
      searchDataForm: {
        type: '',
        startCreateTime: '',
        endCreateTime: '',
        tenantId: '',
        goodsId: '',
        goodsName: '',
        tenantName: ''
      }
    }
  },
  methods: {
    // 选中商品
    onGoodsPicked (result) {
      if (result && result.length > 0) {
        this.searchDataForm.goodsId = result[0].id
        this.searchDataForm.goodsName = result[0].name
      } else {
        this.searchDataForm.goodsId = ''
        this.searchDataForm.goodsName = ''
      }
    }
  }
}
</script>
