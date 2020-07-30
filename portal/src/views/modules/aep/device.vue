<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-aep__device">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.productId" placeholder="产品ID" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.deviceId" placeholder="设备ID" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('aep:device:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('aep:device:sync')">
          <el-button type="success" @click="syncHandle()">同步</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column prop="enterpriseName" label="企业" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="deviceType" label="设备类型" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.deviceType === '00'" type="danger">开关设备</el-tag>
            <el-tag v-else-if="scope.row.deviceType === 'ff'" type="warning">三相设备</el-tag>
            <el-tag v-else-if="scope.row.deviceType === '01'" type="info">单相A设备</el-tag>
            <el-tag v-else-if="scope.row.deviceType === '02'" type="info">单相B设备</el-tag>
            <el-tag v-else-if="scope.row.deviceType === '03'" type="info">单相C设备</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceName" label="设备名称" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="imei" label="IMEI" header-align="center" align="center" width="150"/>
        <el-table-column prop="autoObserver" label="自动订阅" header-align="center" align="center" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.autoObserver === 0" type="success">是</el-tag>
            <el-tag v-else-if="scope.row.autoObserver === 1" type="warning">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceStatus" label="状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.deviceStatus === 0" type="info">已注册</el-tag>
            <el-tag v-else-if="scope.row.deviceStatus === 1" type="success">已激活</el-tag>
            <el-tag v-else-if="scope.row.deviceStatus === 2" type="info">已注销</el-tag>
            <el-tag v-else type="warning">未激活</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="netStatus" label="网络状态" header-align="center" align="center" width="80">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.netStatus === 1" type="success">在线</el-tag>
            <el-tag v-else type="info">离线</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="aepStatus" label="漏电状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.aepStatus === '040100' || scope.row.payloadAppdataData === '04010a'">正常</el-tag>
            <el-tag v-else-if="scope.row.aepStatus === '040111' || scope.row.aepStatus === '04010C' || scope.row.aepStatus === '040130' || scope.row.payloadAppdataData
              === '04013f'" type="warning">零线漏电</el-tag>
            <el-tag v-else-if="scope.row.aepStatus === '040101' || scope.row.aepStatus === '040104' || scope.row.aepStatus === '040110'" type="danger">火线漏电</el-tag>
            <el-tag v-else-if="scope.row.aepStatus === '04010b'" type="danger">漏电</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="onlineAt" label="最后上线时间" header-align="center" align="center" width="160" :formatter="dateTimeUnixFmt"/>
        <el-table-column prop="offlineAt" label="最后离线时间" header-align="center" align="center" width="160" :formatter="dateTimeUnixFmt"/>
        <el-table-column prop="alarmPush" label="推送报警" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.alarmPush === 1" type="success">是</el-tag>
            <el-tag v-else type="info">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="interruptRule" label="漏电处理规则" header-align="center" align="center">
          <template slot-scope="scope" v-if="scope.row.interruptRule">
            <el-tag v-if="scope.row.interruptRule === 'no'" type="info">不处理</el-tag>
            <el-tag v-else-if="scope.row.interruptRule === 'rule1'" type="success">异相断电</el-tag>
            <el-tag v-else>{{scope.row.interruptRule}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="160">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="dataViewHandle(scope.row.deviceId)">状态</el-button>
            <el-divider direction="vertical"/>
            <el-button type="text" size="small" @click="commandDataViewHandle(scope.row.deviceId)">指令</el-button>
            <el-divider direction="vertical"/>
            <el-dropdown trigger="click" @command="editActionHandle" class="action-dropdown">
              <span class="el-dropdown-link" style="font-size:12px;">更多<i class="el-icon-arrow-down el-icon--right"/></span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item size="small" v-if="$hasPermission('aep:device:sendCommand')" :command="composeEditCommandValue('sendCommand', scope.row)" icon="el-icon-position">指令下发
                </el-dropdown-item>
                <el-dropdown-item size="small" v-if="$hasPermission('aep:device:sendCommandOff')" :command="composeEditCommandValue('sendCommandOff', scope.row)" icon="el-icon-link">断电
                </el-dropdown-item>
                <el-dropdown-item size="small" v-if="$hasPermission('aep:device:sendCommandOn')" :command="composeEditCommandValue('sendCommandOn', scope.row)" icon="el-icon-connection">合电
                </el-dropdown-item>
                <el-dropdown-item size="small" v-if="$hasPermission('aep:device:update')" :command="composeEditCommandValue('addOrUpdate', scope.row)" icon="el-icon-edit">{{ $t('update')
                  }}</el-dropdown-item>
                <el-dropdown-item size="small" v-if="$hasPermission('aep:device:delete')" :command="composeEditCommandValue('delete', scope.row)" icon="el-icon-delete">{{ $t('delete')
                  }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
      <!-- 弹窗, 状态数据查看 -->
      <device-data v-if="deviceDataVisible" ref="deviceData" mode="dialog" :lazyLoad="true" @refreshDataList="getDataList"/>
      <!-- 弹窗, 指令数据查看 -->
      <device-command-data v-if="deviceCommandDataVisible" ref="deviceCommandData" mode="dialog" :lazyLoad="true" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './device-add-or-update'
import DeviceData from './device-data'
import DeviceCommandData from './device-command-data'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate, DeviceData, DeviceCommandData },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/aep/device/page',
        getDataListIsPage: true,
        exportURL: '/aep/device/export',
        deleteURL: '/aep/device/delete',
        deleteBatchURL: '/aep/device/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        productId: '',
        deviceId: ''
      },
      // 状态数据查看
      deviceDataVisible: false,
      // 指令数据查看
      deviceCommandDataVisible: false
    }
  },
  methods: {
    // 同步接口
    syncHandle () {
      this.$http.post('/aep/device/sync').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.$message.success('同步完成')
        this.getDataList()
      }).catch(() => {
      })
    },
    // 数据查看
    dataViewHandle (deviceId) {
      this.deviceDataVisible = true
      this.$nextTick(() => {
        this.$refs.deviceData.visible = true
        this.$refs.deviceData.searchDataForm.messageType = 'dataReport'
        this.$refs.deviceData.searchDataForm.deviceId = deviceId
        this.$refs.deviceData.init()
      })
    },
    // 指令数据查看
    commandDataViewHandle (deviceId) {
      this.deviceCommandDataVisible = true
      this.$nextTick(() => {
        this.$refs.deviceCommandData.visible = true
        this.$refs.deviceCommandData.searchDataForm.messageType = 'commandResponse'
        this.$refs.deviceCommandData.searchDataForm.deviceId = deviceId
        this.$refs.deviceCommandData.init()
      })
    },
    // 其它更多按钮操作
    moreEditActionHandle (command) {
      if (command.command === 'sendCommand') {
        // 下发指令
        this.$prompt('请输入16进制指令', '提示', {
          confirmButtonText: '发送',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          this.$http.post(`/aep/device/sendCommand?deviceId=${command.row.deviceId}&command=${value}`).then(({ data: res }) => {
            if (res.code !== 0) {
              return this.$message.error(res.toast)
            }
            this.$message.success('指令已发送')
          }).catch(() => {
          })
        }).catch(() => {

        })
      } else if (command.command === 'sendCommandOn') {
        // 下发指令-合电
        this.$confirm(this.$t('prompt.info', { 'handle': '合电' }), this.$t('prompt.title'), {
          confirmButtonText: this.$t('confirm'),
          cancelButtonText: this.$t('cancel'),
          type: 'warning'
        }).then(() => {
          this.$http.post(`/aep/device/sendCommand?deviceId=${command.row.deviceId}&command=f50003010003030100f4f9`).then(({ data: res }) => {
            if (res.code !== 0) {
              return this.$message.error(res.toast)
            }
            this.$message.success('合电指令已发送')
          }).catch(() => {
          })
        }).catch(() => {
        })
      } else if (command.command === 'sendCommandOff') {
        // 下发指令-合电
        this.$confirm(this.$t('prompt.info', { 'handle': '断电' }), this.$t('prompt.title'), {
          confirmButtonText: this.$t('confirm'),
          cancelButtonText: this.$t('cancel'),
          type: 'warning'
        }).then(() => {
          this.$http.post(`/aep/device/sendCommand?deviceId=${command.row.deviceId}&command=f50003010003030101f3f9`).then(({ data: res }) => {
            if (res.code !== 0) {
              return this.$message.error(res.toast)
            }
            this.$message.success('断电指令已发送')
          }).catch(() => {
          })
        }).catch(() => {
        })
      }
    }
  }
}
</script>
