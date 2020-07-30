<template>
  <el-button icon="el-icon-document-checked" @click="openPickHandle()">{{ btnText }}
    <el-dialog title="选择订货单" :visible.sync="visible" append-to-body modal-append-to-body
               :close-on-click-modal="false" :close-on-press-escape="false"
               @close="closeHandle"
               width="80%" :fullscreen="fullscreen">
      <div class="mod-tms__waybill-item">
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
          <el-form-item>
            <el-button @click="getDataList()">{{ $t('query') }}</el-button>
          </el-form-item>
        </el-form>
        <el-table v-loading="dataListLoading" :data="dataList" ref="dataTable"
                  :select-on-indeterminate="false" @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column prop="supplierName" label="供应商" header-align="center" align="center" min-width="100"/>
          <el-table-column prop="purchaseDate" label="进货日期" header-align="center" align="center" width="120" sortable="custom" :formatter="dateDayFmt"/>
          <el-table-column prop="code" label="箱号" header-align="center" align="center" width="120"/>
          <el-table-column prop="sealCode" label="封号" header-align="center" align="center" width="120"/>
          <el-table-column prop="goodsType" label="品种" header-align="center" align="center" width="120"/>
          <el-table-column prop="goods" label="货名" header-align="center" align="center" width="120"/>
          <el-table-column prop="price" label="单价(元)" header-align="center" align="center" width="120"/>
          <el-table-column prop="qty" label="数量(吨)" header-align="center" align="center" width="120"/>
          <el-table-column prop="totalPrice" label="小计(元)" header-align="center" align="center" width="120" :formatter="numberFmt"/>
          <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        </el-table>
        <el-pagination
                :current-page="page"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="limit"
                hide-on-single-page
                :total="total"
                layout="total, prev, pager, next"
                @size-change="pageSizeChangeHandle"
                @current-change="pageCurrentChangeHandle"/>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
        <el-button type="primary" @click="dataFormSubmitHandle()" :disabled="!dataListSelections || dataListSelections.length === 0">{{ $t('confirm') }}</el-button>
      </div>
    </el-dialog>
  </el-button>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'

export default {
  name: 'waybill-item-pick',
  // 参数
  props: {
    // 请求码
    requestCode: {
      type: String,
      default: null
    },
    tenantId: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'single'
    },
    btnText: {
      type: String,
      default: ''
    }
  },
  mixins: [mixinBaseModule, mixinListModule],
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/tms/waybillItem/page',
        getDataListIsPage: true
      },
      // 是否可见
      visible: false,
      // 全屏
      fullscreen: false,
      searchDataForm: {
        supplierName: '',
        status: 0,
        code: '',
        sealCode: '',
        goods: '',
        goodsType: '',
        waybillCode: '',
        startPurchaseDate: '',
        endPurchaseDate: ''
      }
    }
  },
  methods: {
    openPickHandle () {
      this.visible = true
      Promise.all([
      ]).then(() => {
        this.getDataList()
      })
    },
    // 关闭时的回调
    closeHandle () {

    },
    dataListSelectionChangeHandle (val) {
      if (this.type === 'multi') {
        this.dataListSelections = val
      } else {
        if (val.length > 1) {
          this.$refs.dataTable.toggleRowSelection(val[0], false)
          val.splice(0, 1)
        }
        this.dataListSelections = val
      }
    },
    dataFormSubmitHandle () {
      // 验证通过,提交表单
      this.$emit('onWaybillItemPicked', this.dataListSelections, this.requestCode)
      this.visible = false
    },
    // 清空选择内容
    clearSubmitHandle () {
      this.$emit('onWaybillItemPicked', null, this.requestCode)
      this.visible = false
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
    }
  }
}
</script>
