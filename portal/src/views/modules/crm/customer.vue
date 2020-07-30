<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-crm__customer">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchDataForm.search" placeholder="名称/电话/标签" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.level" placeholder="级别" clearable>
            <el-option label="重点" :value="1"/>
            <el-option label="普通" :value="2"/>
            <el-option label="非优先" :value="3"/>
          </el-select>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.source" placeholder="来源" filterable allow-create clearable>
            <el-option v-for="item in sourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.dealStatus" placeholder="成交状态" clearable>
            <el-option label="已成交" :value="1"/>
            <el-option label="未成交" :value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:customer:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:customer:import')">
          <el-button type="danger" @click="importHandle()">{{ $t('import') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('crm:customer:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="name" label="客户名称" header-align="center" align="center" min-width="100">
          <template slot-scope="scope">
            <el-link type="primary" @click="previewHandle(scope.row.id,scope.row.name)" :underline="false">{{ scope.row.name }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.level === 1" type="danger">重点</el-tag>
            <el-tag v-else-if="scope.row.level === 2">普通</el-tag>
            <el-tag v-else-if="scope.row.level === 3" type="info">非优先</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="source" label="客户来源" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
        <el-table-column prop="dealStatus" label="成交状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.dealStatus === 1" type="success">已成交</el-tag>
            <el-tag v-else-if="scope.row.dealStatus === 0" type="info">未成交</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contacts" label="联系人" header-align="center" align="center" width="120"/>
        <el-table-column prop="mobile" label="联系手机" header-align="center" align="center" width="120"/>
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
        <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('crm:customer:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('crm:customer:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import AddOrUpdate from './customer-add-or-update'
import Import from './customer-import'
import AmapLocView from '@/components/amap-loc-view'
import TenantPick from '../uc/tenant-pick'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { Import, AddOrUpdate, AmapLocView, TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/crm/customer/page',
        getDataListIsPage: true,
        exportURL: '/crm/customer/export',
        deleteURL: '/crm/customer/delete',
        deleteBatchURL: '/crm/customer/deleteBatch',
        deleteIsBatch: false
      },
      // 来源选项
      sourceOptions: [{
        value: '促销',
        label: '促销'
      }, {
        value: '广告',
        label: '广告'
      }, {
        value: '转介绍',
        label: '转介绍'
      }, {
        value: '陌拜',
        label: '陌拜'
      }, {
        value: '电话咨询',
        label: '电话咨询'
      }, {
        value: '网上咨询',
        label: '网上咨询'
      }],
      searchDataForm: {
        name: '',
        search: '',
        level: '',
        source: '',
        dealStatus: '',
        tenantId: '',
        tenantName: ''
      }
    }
  },
  methods: {
    // 查看
    previewHandle (id) {
      this.$router.push({ name: 'crm-customer-info', query: { id: id } })
    }
  }
}
</script>
