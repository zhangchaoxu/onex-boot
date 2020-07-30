<template>
  <el-dialog :visible.sync="visible" :close-on-click-modal="false" :close-on-press-escape="false" width="80%" :fullscreen="fullscreen">
    <div slot="title">
      <span class="el-dialog__title">{{ title }}</span>
      <button type="button" class="el-dialog__headerbtn" style="right: 50px;" @click="fullscreen = !fullscreen"><i class="el-dialog__close el-icon el-icon-full-screen"/></button>
    </div>
      <div class="mod-aep__subscription-push-message">
        <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
          <el-form-item class="tiny-item">
            <el-select v-model="searchDataForm.payloadAppdataDataType" placeholder="类型" clearable>
              <el-option label="正常" value="0"/>
              <el-option label="漏电" value="1"/>
            </el-select>
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
                    :end-placeholder="$t('datePicker.end')"/>
          </el-form-item>
          <el-form-item>
            <el-button @click="getDataList()">{{ $t('query') }}</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="dataListLoading" :data="dataList" border @sort-change="dataListSortChangeHandle" @cell-click="cellClickHandle" style="width: 100%;" size="small">
          <el-table-column prop="deviceId" label="设备ID" header-align="center" align="center" min-width="120"/>
          <el-table-column prop="timestamp" label="上报时间" header-align="center" align="center" width="160" :formatter="dateTimeUnixFmt"/>
          <el-table-column prop="payloadAppdata" label="元数据" header-align="center" align="center" width="180"/>
          <el-table-column prop="payloadAppdataFun" label="功能码" header-align="center" align="center" width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.payloadAppdataFun === '02'" type="success">上报</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataFun === '03'" type="warning">下发</el-tag>
              <el-tag v-else type="warning">{{ scope.row.payloadAppdataFun }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="payloadAppdataDevice" label="设备类型" header-align="center" align="center" width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.payloadAppdataDevice === '00'" type="danger">开关设备</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataDevice === 'ff'" type="warning">三相设备</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataDevice === '01'" type="info">单相A设备</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataDevice === '02'" type="info">单相B设备</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataDevice === '03'" type="info">单相C设备</el-tag>
              <el-tag v-else type="warning">{{ scope.row.payloadAppdataDevice }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="payloadAppdataData" label="内容" header-align="center" align="center" width="120">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.payloadAppdataData === '040100' || scope.row.payloadAppdataData === '04010a'">正常</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataData === '040111' || scope.row.payloadAppdataData === '04010c' || scope.row.payloadAppdataData === '040130' || scope.row.payloadAppdataData
              === '04013f'" type="warning">零线漏电</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataData === '040101' || scope.row.payloadAppdataData === '040104' || scope.row.payloadAppdataData === '040110'" type="danger">火线漏电</el-tag>
              <el-tag v-else-if="scope.row.payloadAppdataData === '04010b'" type="danger">漏电</el-tag>
              <el-tag v-else>漏电{{ scope.row.payloadAppdataData }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
                v-if="mixinListModuleOptions.getDataListIsPage"
                :current-page="page"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="limit"
                :total="total"
                hide-on-single-page
                small
                layout="total, prev, pager, next"
                @size-change="pageSizeChangeHandle"
                @current-change="pageCurrentChangeHandle"/>
      </div>
  </el-dialog>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import dayjs from 'dayjs'

export default {
  mixins: [mixinListModule, mixinBaseModule],
  components: { },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/aep/subscriptionPushMessage/page',
        getDataListIsPage: true,
        exportURL: '/aep/subscriptionPushMessage/export',
        deleteURL: '/aep/subscriptionPushMessage/delete',
        deleteBatchURL: '/aep/subscriptionPushMessage/deleteBatch',
        deleteIsBatch: true
      },
      // 全屏
      fullscreen: false,
      title: '状态数据查看',
      searchDataForm: {
        productId: '',
        deviceId: '',
        messageType: '',
        payloadAppdataDataType: '',
        startCreateTime: '',
        endCreateTime: ''
      },
      // 是否可见
      visible: false
    }
  },
  methods: {
    init () {
      this.searchDataForm.startCreateTime = dayjs().format('YYYY-MM-DD 00:00:00')
      this.searchDataForm.endCreateTime = dayjs().format('YYYY-MM-DD 23:59:59')
      this.dateRange = [this.searchDataForm.startCreateTime, this.searchDataForm.endCreateTime]
      this.getDataList()
    }
  }
}
</script>
