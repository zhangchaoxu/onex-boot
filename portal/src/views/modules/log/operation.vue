<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-log__operation">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.uri" placeholder="请求Uri" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.status" placeholder="状态" clearable>
            <el-option :label="$t('error')" :value="0"/>
            <el-option :label="$t('success')" :value="1"/>
          </el-select>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.createName" placeholder="用户" clearable/>
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
          <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('log:operation:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column prop="createName" label="用户" header-align="center" align="center" width="150"/>
        <el-table-column prop="operation" label="操作" header-align="center" align="center" width="150"/>
        <el-table-column prop="uri" label="请求Uri" header-align="center" align="center" width="200"/>
        <el-table-column prop="method" label="请求方法" header-align="center" align="center" width="100"/>
        <el-table-column prop="params" label="请求参数" header-align="center" align="center" class-name="nowrap json link"/>
        <el-table-column prop="requestTime" label="耗时" sortable="custom" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            {{ `${scope.row.requestTime}ms` }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" sortable="custom" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" size="small" type="danger">{{ $t('error') }}</el-tag>
            <el-tag v-else size="small" type="success">{{ $t('success') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="IP" header-align="center" align="center" width="200"/>
        <el-table-column prop="userAgent" label="UA" header-align="center" align="center" width="150" :show-overflow-tooltip="true"/>
        <el-table-column prop="createTime" label="操作时间" sortable="custom" header-align="center" align="center" width="180"/>
      </el-table>
      <el-pagination
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
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/log/operation/page',
        getDataListIsPage: true,
        exportURL: '/log/operation/export'
      },
      searchDataForm: {
        createName: '',
        status: '',
        uri: '',
        startCreateTime: '',
        endCreateTime: ''
      }
    }
  }
}
</script>
