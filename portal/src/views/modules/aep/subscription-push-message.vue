<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-aep__subscription-push-message">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.productId" placeholder="产品ID" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.deviceId" placeholder="设备ID" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.messageType" placeholder="消息类型" clearable>
            <el-option label="设备数据变化" value="dataReport"/>
            <el-option label="设备命令响应" value="commandResponse"/>
            <el-option label="设备事件上报" value="eventReport"/>
            <el-option label="设备上下线" value="deviceOnlineOfflineReport"/>
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
                  :end-placeholder="$t('datePicker.end')">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('aep:subscriptionPushMessage:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('aep:subscriptionPushMessage:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="productId" label="产品ID" header-align="center" align="center" width="100"/>
        <el-table-column prop="deviceId" label="设备ID" header-align="center" align="center" width="150"/>
        <el-table-column prop="messageType" label="消息类型" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.messageType === 'dataReport'">设备数据变化</el-tag>
            <el-tag v-else-if="scope.row.messageType === 'commandResponse'">设备命令响应</el-tag>
            <el-tag v-else-if="scope.row.messageType === 'eventReport'">设备事件上报</el-tag>
            <el-tag v-else-if="scope.row.messageType === 'deviceOnlineOfflineReport'">设备上下线</el-tag>
            <el-tag v-else>{{ scope.row.messageType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="imei" label="imei" header-align="center" align="center" width="150"/>
        <el-table-column prop="imsi" label="imsi" header-align="center" align="center" width="100"/>
        <el-table-column prop="timestamp" label="上报时间" header-align="center" align="center" width="160" :formatter="dateTimeUnixFmt"/>
        <el-table-column prop="deviceType" label="设备标识" header-align="center" align="center"></el-table-column>
        <el-table-column prop="topic" label="数据上报主题" header-align="center" align="center"></el-table-column>
        <el-table-column prop="assocAssetId" label="合作伙伴ID" header-align="center" align="center"></el-table-column>
        <el-table-column prop="upPacketSn" label="上行报文序号" header-align="center" align="center" width="100"/>
        <el-table-column prop="upDataSn" label="数据上报报文序号" header-align="center" align="center" width="100"/>
        <el-table-column prop="serviceId" label="服务ID" header-align="center" align="center"></el-table-column>
        <el-table-column prop="protocol" label="协议" header-align="center" align="center" width="100"/>
        <el-table-column prop="payload" label="消息荷载" header-align="center" align="center" width="150" class-name="nowrap json link"/>
        <el-table-column prop="taskId" label="指令任务ID" header-align="center" align="center"></el-table-column>
        <el-table-column prop="result" label="指令执行结果" header-align="center" align="center" width="150" class-name="nowrap json link"/>
        <el-table-column prop="deviceSn" label="设备编号" header-align="center" align="center"></el-table-column>
        <el-table-column prop="eventType" label="事件类型" header-align="center" align="center" width="100"/>
        <el-table-column prop="eventContent" label="事件上报数据" header-align="center" align="center" width="150" class-name="nowrap json link"/>
        <el-table-column prop="createTime" label="创建时间" header-align="center" align="center" width="150"/>
        <el-table-column prop="tenantId" label="租户id" header-align="center" align="center" width="100"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('aep:subscriptionPushMessage:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
    </div>
  </el-card>
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
      searchDataForm: {
        productId: '',
        deviceId: '',
        messageType: '',
        startCreateTime: '',
        endCreateTime: ''
      }
    }
  },
  created () {
    this.searchDataForm.startCreateTime = dayjs().format('YYYY-MM-DD 00:00:00')
    this.searchDataForm.endCreateTime = dayjs().format('YYYY-MM-DD 23:59:59')
    this.dateRange = [this.searchDataForm.startCreateTime, this.searchDataForm.endCreateTime]
  }
}
</script>
