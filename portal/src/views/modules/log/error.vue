<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-log__error">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.uri" placeholder="请求Uri" clearable/>
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
        <el-form-item v-if="$hasPermission('log:error:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column prop="uri" label="请求Uri" header-align="center" align="center" width="200"/>
        <el-table-column prop="method" label="方法" header-align="center" align="center" width="100"/>
        <el-table-column prop="params" label="参数" header-align="center" align="center" min-width="150" class-name="nowrap json link"/>
        <el-table-column prop="content" label="异常信息" header-align="center" align="center" min-width="150" class-name="nowrap text link"/>
        <el-table-column prop="ip" label="IP" header-align="center" align="center" width="200"/>
        <el-table-column prop="userAgent" label="UA" header-align="center" align="center" width="150" :show-overflow-tooltip="true"/>
        <el-table-column prop="createTime" label="创建时间" sortable="custom" header-align="center" align="center" width="180"/>
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
        getDataListURL: '/log/error/page',
        getDataListIsPage: true,
        exportURL: '/log/error/export'
      },
      searchDataForm: {
        uri: '',
        startCreateTime: '',
        endCreateTime: ''
      }
    }
  }
}
</script>
