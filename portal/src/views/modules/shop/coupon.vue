<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-shop__coupon">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" placeholder="名称" clearable></el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.type" clearable placeholder="类型">
            <el-option label="满减券" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.status" clearable placeholder="状态">
            <el-option label="未激活" value="0"></el-option>
            <el-option label="已激活" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:coupon:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:coupon:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="name" label="名称" header-align="center" align="center"/>
        <el-table-column prop="type" label="类型" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 1" size="small">满减券</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="面额" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            满{{ scope.row.limitPrice }}减{{ scope.row.reducedPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="giveType" label="发放方式" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.giveType === 1" size="small">注册赠送</el-tag>
            <el-tag v-if="scope.row.giveType === 2" size="small">{{pointExchange}}积分兑换</el-tag>
            <el-tag v-if="scope.row.giveType === 3" size="small">会员领取</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="validStartTime" label="有效期" header-align="center" align="center" width="300">
          <template slot-scope="scope">
            {{ scope.row.validStartTime }}<i class="el-icon-arrow-right"/>{{ scope.row.validEndTime }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" header-align="center" align="center" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" size="small" type="info">未激活</el-tag>
            <el-tag v-else-if="scope.row.status === 1" size="small" type="success">已激活</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="数量" header-align="center" align="center" width="210">
          <template slot-scope="scope">
            共{{ scope.row.totalQty }}张/每人限领{{ scope.row.userQtyLimit }}张/已领{{ scope.row.giveQty }}张
          </template>
        </el-table-column>
        <el-table-column prop="content" label="描述" header-align="center" align="center" show-overflow-tooltip></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('shop:coupon:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('shop:coupon:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './coupon-add-or-update'
import TenantPick from '../uc/tenant-pick'
export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate, TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/shop/coupon/page',
        getDataListIsPage: true,
        exportURL: '/shop/coupon/export',
        deleteURL: '/shop/coupon/delete',
        deleteIsBatch: false
      },
      searchDataForm: {
        name: '',
        type: '',
        status: '',
        storeName: '',
        tenantId: '',
        tenantName: ''
      }
    }
  }
}
</script>
