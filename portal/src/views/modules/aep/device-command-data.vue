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
          <el-table-column prop="taskId" label="指令ID" header-align="center" align="center" width="100">
          </el-table-column>
          <el-table-column prop="result" label="指令执行状态" header-align="center" align="center" width="120" :formatter="resultCodeFmt"/>
          <el-table-column prop="result" label="指令执行结果" header-align="center" align="center" min-width="120" class-name="nowrap json link" :formatter="resultDetailFmt"/>
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
      title: '指令数据查看',
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
    },
    // 指令执行状态
    resultCodeFmt (row, column, cellValue) {
      if (cellValue) {
        let json = JSON.parse(cellValue)
        if (json.resultCode === 'SUCCESSFUL') {
          return '成功'
        } else if (json.resultCode === 'SENT') {
          return '已发送'
        } else if (json.resultCode === 'DELIVERED') {
          return '已送达'
        } else if (json.resultCode === 'FAILED') {
          return '失败'
        }
        return json.resultCode
      } else {
        return cellValue
      }
    },
    // 指令执行结果
    resultDetailFmt (row, column, cellValue) {
      if (cellValue) {
        let json = JSON.parse(cellValue)
        return json.resultDetail
      } else {
        return cellValue
      }
    }
  }
}
</script>
