<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-tms__waybill">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.code" placeholder="运单号" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.sender" placeholder="发货人" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.carrierOwner" placeholder="船公司" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.carrierName" placeholder="船名" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.carrierNo" placeholder="船次" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('tms:waybill:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('tms:waybill:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column prop="sender" label="发货人" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
        <el-table-column prop="carrierOwner" label="船公司" header-align="center" align="center" width="100" show-tooltip-when-overflow/>
        <el-table-column prop="carrierName" label="船名" header-align="center" align="center" width="100" show-tooltip-when-overflow/>
        <el-table-column prop="carrierNo" label="船次" header-align="center" align="center" width="100" show-tooltip-when-overflow/>
        <el-table-column prop="code" label="运单号" header-align="center" align="center" min-width="180" class="nowrap">
          <template slot-scope="scope">
            <el-link type="primary" @click="previewHandle(scope.row.id)" :underline="false">{{ scope.row.code }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="carrierFrom" label="装卸货港" header-align="center" align="center" min-width="150">
          <template slot-scope="scope">
              <span>{{ scope.row.carrierFrom }}<i class="el-icon-right"/>{{ scope.row.carrierTo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="itemCount" label="货物" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <span>{{ scope.row.waybillItemCount }}箱</span>
          </template>
        </el-table-column>
        <el-table-column prop="carrierFromDate" label="离港日" header-align="center" align="center" :formatter="dateDayFmt" sortable="custom" width="120"/>
        <el-table-column prop="carrierToDate" label="到港日" header-align="center" align="center" :formatter="dateDayFmt" width="120"/>
        <el-table-column prop="priceTotal" label="运费" header-align="center" align="center" width="120"/>
        <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('tms:waybill:update')" type="text" size="small" @click="updateCarrierToDateHandle(scope.row.id)">到港</el-button>
            <el-button v-if="$hasPermission('tms:waybill:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('tms:waybill:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <update-carriertodate v-if="updateCarriertodateVisible" ref="updateCarriertodate" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import UpdateCarriertodate from './waybill-carriertodate-update'

export default {
  mixins: [mixinListModule],
  components: { UpdateCarriertodate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/tms/waybill/page',
        getDataListIsPage: true,
        exportURL: '/tms/waybill/export',
        deleteURL: '/tms/waybill/delete',
        deleteBatchURL: '/tms/waybill/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        code: '',
        carrierOwner: '',
        carrierNo: '',
        sender: '',
        carrierName: ''
      },
      // 到港
      updateCarriertodateVisible: false
    }
  },
  methods: {
    // 修改到港时间
    updateCarrierToDateHandle (id) {
      this.updateCarriertodateVisible = true
      this.$nextTick(() => {
        this.$refs.updateCarriertodate.clear()
        this.$refs.updateCarriertodate.dataForm.id = id
        this.$refs.updateCarriertodate.dataFormMode = 'update'
        this.$refs.updateCarriertodate.init()
      })
    },
    // 查看
    previewHandle (id) {
      this.$router.push({ name: 'tms-waybill-info', query: { id: id } })
    },
    // 新增/修改
    addOrUpdateHandle (id) {
      this.$router.push({ name: 'tms-waybill-add-or-update', query: { id: id }, meta: { title: id ? 'edit' : 'add', isTab: true, isDynamic: true } })
    }
  }
}
</script>
