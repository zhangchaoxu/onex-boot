<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-tms__order-item">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
        <el-form-item class="tiny-item">
          <el-input v-model="searchDataForm.supplierName" placeholder="供应商" clearable/>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-input v-model="searchDataForm.code" placeholder="箱号" clearable/>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-input v-model="searchDataForm.sealCode" placeholder="封号" clearable/>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-input v-model="searchDataForm.goodsType" placeholder="品种" clearable/>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-input v-model="searchDataForm.goods" placeholder="货名" clearable/>
        </el-form-item>
        <el-form-item>
          <el-date-picker
                  v-model="dateRange"
                  type="daterange"
                  @change="dateRangeChangeHandle"
                  :picker-options="dateRangePickerOptions"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :range-separator="$t('datePicker.range')"
                  :start-placeholder="$t('datePicker.start')"
                  :end-placeholder="$t('datePicker.end')">
          </el-date-picker>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-select v-model="searchDataForm.status" placeholder="状态" clearable @change="onStatusChange">
            <el-option label="未装船" :value="0"/>
            <el-option label="已装船" :value="1"/>
          </el-select>
        </el-form-item>
        <el-form-item class="small-item" v-if="searchDataForm.status === 1">
          <el-input v-model="searchDataForm.waybillCode" placeholder="运单号" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('tms:waybillItem:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('tms:waybillItem:import')">
          <el-button type="danger" @click="importHandle()">{{ $t('import') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('tms:waybillItem:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" show-summary :summary-method="getWaybillItemSummaries" style="width: 100%;">
        <el-table-column prop="supplierName" label="供应商" header-align="center" align="center" min-width="100"/>
        <el-table-column prop="purchaseDate" label="进货日期" header-align="center" align="center" width="120" sortable="custom" :formatter="dateDayFmt"/>
        <el-table-column prop="code" label="箱号" header-align="center" align="center" width="120"/>
        <el-table-column prop="sealCode" label="封号" header-align="center" align="center" width="120"/>
        <el-table-column prop="goodsType" label="品种" header-align="center" align="center" width="120"/>
        <el-table-column prop="goods" label="货名" header-align="center" align="center" width="120"/>
        <el-table-column prop="price" label="单价(元)" header-align="center" align="center" width="120"/>
        <el-table-column prop="qty" label="数量(吨)" header-align="center" align="center" width="120"/>
        <el-table-column prop="qtyUnload" label="卸货数量" header-align="center" align="center" width="100"/>
        <el-table-column prop="totalPrice" label="小计(元)" header-align="center" align="center" width="120" :formatter="numberFmt"/>
        <el-table-column prop="waybillCode" label="运单号" header-align="center" align="center" width="180" class="nowrap">
          <template slot-scope="scope">
            <el-link v-if="scope.row.status !== 0" type="primary" @click="previewWaybillHandle(scope.row.waybillId)" :underline="false">{{ scope.row.waybillCode }}</el-link>
            <el-tag v-else type="danger">未装船</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('tms:waybillItem:update')" type="text" size="small" @click="updateUnloadHandle(scope.row.id)">卸货</el-button>
            <el-button v-if="$hasPermission('tms:waybillItem:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('tms:waybillItem:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <!-- 弹窗, 修改卸货数量-->
      <update-unload v-if="updateUnloadVisible" ref="updateUnload" @refreshDataList="getDataList"/>
      <!-- 弹窗, 导入 -->
      <import v-if="importVisible" ref="import" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './waybill-item-add-or-update'
import UpdateUnload from './waybill-item-unload-update'
import Import from './waybill-item-import'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate, UpdateUnload, Import },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/tms/waybillItem/page',
        getDataListIsPage: true,
        exportURL: '/tms/waybillItem/export',
        deleteURL: '/tms/waybillItem/delete',
        deleteBatchURL: '/tms/waybillItem/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        supplierName: '',
        status: '',
        code: '',
        sealCode: '',
        goods: '',
        goodsType: '',
        waybillCode: '',
        startPurchaseDate: '',
        endPurchaseDate: ''
      },
      // 卸货
      updateUnloadVisible: false
    }
  },
  methods: {
    // 修改卸货数量
    updateUnloadHandle (id) {
      this.updateUnloadVisible = true
      this.$nextTick(() => {
        this.$refs.updateUnload.clear()
        this.$refs.updateUnload.dataForm.id = id
        this.$refs.updateUnload.dataFormMode = 'update'
        this.$refs.updateUnload.init()
      })
    },
    onStatusChange () {
      if (this.searchDataForm.status === 0) {
        this.searchDataForm.waybillCode = ''
      }
    },
    // 查看运单
    previewWaybillHandle (id) {
      this.$router.push({ name: 'tms-waybill-info', query: { id: id } })
    },
    // 时间区间选择器变化
    dateRangeChangeHandle (value) {
      if (value !== null && value.length === 2) {
        this.searchDataForm.startPurchaseDate = value[0]
        this.searchDataForm.endPurchaseDate = value[1]
      } else {
        this.searchDataForm.startPurchaseDate = ''
        this.searchDataForm.endPurchaseDate = ''
      }
    },
    numberFmt (row, column, cellValue) {
      return !isNaN(cellValue) ? cellValue.toFixed(2) : ''
    },
    // list信息获取成功
    onGetListSuccess (res) {
      this.dataList = this.mixinListModuleOptions.getDataListIsPage ? res.data.list : res.data
      this.total = this.mixinListModuleOptions.getDataListIsPage ? res.data.total : res.data.list.length
      if (this.dataList) {
        this.dataList.forEach(item => {
          item.totalPrice = item.qty * item.price
        })
      }
    },
    /** 统计行 */
    getWaybillItemSummaries (param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (column.property === 'supplierName') {
          sums[index] = '合计'
          return
        }
        if (column.property === 'code') {
          sums[index] = data.length + '箱'
          return
        }
        if (column.property === 'qty' || column.property === 'totalPrice') {
          // 针对部分列开放统计
          const values = data.map(item => Number(item[column.property]))
          if (!values.every(value => isNaN(value))) {
            // 所有项都是数字
            sums[index] = values.reduce((prev, curr) => {
              const value = Number(curr)
              if (!isNaN(value)) {
                return prev + curr
              } else {
                return prev
              }
            }, 0)
            sums[index] = sums[index].toFixed(2)
          } else {
            sums[index] = 'NA'
          }
        } else {
          sums[index] = ''
        }
      })
      return sums
    }
  }
}
</script>
