<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-aep__product">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.productId" placeholder="产品ID" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.productName" placeholder="产品名称" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('aep:product:sync')">
          <el-button type="success" @click="syncHandle()">同步</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column prop="productId" label="产品ID" header-align="center" align="center" width="120"/>
        <el-table-column prop="productName" label="产品名称" header-align="center" align="center" min-width="150"/>
        <el-table-column prop="productType" label="类别" header-align="center" align="center" min-width="120" show-tooltip-when-overflow>
          <template slot-scope="scope">
            <span>{{ scope.row.productTypeValue }}>{{ scope.row.secondaryTypeValue }}>{{ scope.row.thirdTypeValue }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="productProtocol" label="产品协议" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.productProtocol === 1">T-LINK协议</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 2">MQTT协议</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 3">LWM2M协议</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 4">TUP协议</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 5">HTTP协议</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 6">JT/T808</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 7">TCP协议</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 8">私有TCP(网关子设备协议)</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 9">私有UDP(网关子设备协议)</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 10">网关产品MQTT(网关产品协议)</el-tag>
            <el-tag v-else-if="scope.row.productProtocol === 11">南向云</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="networkType" label="网络类型" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.networkType === 1">WIFI</el-tag>
            <el-tag v-else-if="scope.row.networkType === 2">移动蜂窝数据</el-tag>
            <el-tag v-else-if="scope.row.networkType === 3">NB-IoT</el-tag>
            <el-tag v-else-if="scope.row.networkType === 4">以太网</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="authType" label="认证方式" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.authType === 1">特征串认证</el-tag>
            <el-tag v-else-if="scope.row.authType === 2">sm9认证</el-tag>
            <el-tag v-else-if="scope.row.authType === 3">dtls双向认证</el-tag>
            <el-tag v-else-if="scope.row.authType === 4">IMEI认证</el-tag>
            <el-tag v-else-if="scope.row.authType === 5">SIMID认证</el-tag>
            <el-tag v-else-if="scope.row.authType === 6">SM2认证</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceCount" label="设备总数" header-align="center" align="center" width="100"/>
        <el-table-column prop="onlineDeviceCount" label="在线设备数" header-align="center" align="center" width="100"/>
        <el-table-column prop="powerModel" label="省电模式" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.powerModel === 1">PSM</el-tag>
            <el-tag v-else-if="scope.row.powerModel === 2">DRX</el-tag>
            <el-tag v-else-if="scope.row.powerModel === 3">eDRX</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="devicdeIpPort" label="设备接入IP端口" header-align="center" align="center" width="150"/>
        <el-table-column prop="ext" label="完整信息" header-align="center" align="center"></el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('aep:product:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('aep:product:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
import mixinListModule from '@/mixins/list-module'
export default {
  mixins: [mixinListModule],
  components: { },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/aep/product/page',
        getDataListIsPage: true,
        exportURL: '/aep/product/export',
        deleteURL: '/aep/product/delete',
        deleteBatchURL: '/aep/product/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        productId: '',
        productName: ''
      }
    }
  },
  methods: {
    // 同步接口
    syncHandle () {
      this.$http.post('/aep/product/sync').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.$message.success('同步完成')
        this.getDataList()
      }).catch(() => {
      })
    }
  }
}
</script>
