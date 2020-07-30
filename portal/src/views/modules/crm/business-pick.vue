<template>
    <el-button icon="el-icon-s-finance" @click="openPickHandle()">
        <el-dialog title="选择商机" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen">
            <div class="mod-crm__business">
                <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
                    <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
                        <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
                            <tenant-pick class="small-button" slot="append" :id="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
                        </el-input>
                    </el-form-item>
                    <el-form-item class="large-item">
                        <el-input v-model="searchDataForm.customerName" placeholder="客户" readonly>
                            <customer-pick class="small-button" slot="append" :id="searchDataForm.customerId" @onCustomerPicked="onCustomerPicked" />
                        </el-input>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-input v-model="searchDataForm.name" placeholder="名称" clearable/>
                    </el-form-item>
                    <el-form-item class="tiny-item">
                        <el-select v-model="searchDataForm.status" placeholder="状态" clearable>
                            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                                <span style="float: left">{{ item.label }}</span>
                                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.tip }}</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-select v-model="searchDataForm.source" placeholder="来源" filterable allow-create clearable>
                            <el-option v-for="item in sourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="getDataList()">{{ $t('query') }}</el-button>
                    </el-form-item>
                </el-form>
                <el-table v-loading="dataListLoading" :data="dataList" ref="dataTable"
                          :select-on-indeterminate="false" @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
                    <el-table-column type="selection" header-align="center" align="center" width="50"/>
                    <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
                    <el-table-column prop="customerName" label="客户" header-align="center" align="center" min-width="120"/>
                    <el-table-column prop="name" label="名称" header-align="center" align="center" min-width="120" class="nowrap">
                        <template slot-scope="scope">
                            <el-link type="primary" @click="previewHandle(scope.row.id)" :underline="false">{{ scope.row.name }}</el-link>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.status === 1" type="warning">阶段1</el-tag>
                            <el-tag v-else-if="scope.row.status === 2" type="warning">阶段2</el-tag>
                            <el-tag v-else-if="scope.row.status === 3" type="warning">阶段3</el-tag>
                            <el-tag v-else-if="scope.row.status === 10" type="success">赢单</el-tag>
                            <el-tag v-else-if="scope.row.status === -10" type="danger">输单</el-tag>
                            <el-tag v-else-if="scope.row.status === 0" type="info">无效</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="salesUserName" label="销售" header-align="center" align="center" width="120"/>
                    <el-table-column prop="source" label="商机来源" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
                    <el-table-column prop="amount" label="金额" header-align="center" align="center" width="120"/>
                    <el-table-column prop="dealDate" label="预计成交" header-align="center" align="center" width="120" :formatter="dateDayFmt"/>
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
                <el-button type="warning" @click="clearSubmitHandle()">{{ $t('clear') }}</el-button>
            </div>
        </el-dialog>
    </el-button>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import TenantPick from '../uc/tenant-pick'
import CustomerPick from './customer-pick'

export default {
  name: 'business-pick',
  // 参数
  props: {
    // 请求码
    requestCode: {
      type: String,
      default: null
    },
    id: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'single'
    }
  },
  mixins: [mixinListModule],
  components: { TenantPick, CustomerPick },
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/crm/business/page',
        getDataListIsPage: true
      },
      // 是否可见
      visible: false,
      // 全屏
      fullscreen: false,
      // 搜索条件
      searchDataForm: {
        name: '',
        status: '',
        tenantId: '',
        customerName: '',
        customerId: ''
      },
      // 状态选项
      statusOptions: [{
        value: 1,
        label: '阶段1',
        tip: '赢单率10%'
      }, {
        value: 2,
        label: '阶段2',
        tip: '赢单率30%'
      }, {
        value: 3,
        label: '阶段3',
        tip: '赢单率50%'
      }, {
        value: 10,
        label: '赢单',
        tip: '赢得订单'
      }, {
        value: -10,
        label: '输单',
        tip: '输了订单'
      }, {
        value: 0,
        label: '无效',
        tip: '商机无效'
      }],
      // 来源选项
      sourceOptions: [{
        value: '促销',
        label: '促销'
      }, {
        value: '广告',
        label: '广告'
      }, {
        value: '转介绍',
        label: '转介绍'
      }, {
        value: '陌拜',
        label: '陌拜'
      }, {
        value: '电话咨询',
        label: '电话咨询'
      }, {
        value: '网上咨询',
        label: '网上咨询'
      }]
    }
  },
  methods: {
    openPickHandle () {
      this.visible = true
      this.getDataList()
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
    /**
     * 提交选择内容
     */
    dataFormSubmitHandle () {
      // 验证通过,提交表单
      this.$emit('onBusinessPicked', this.dataListSelections, this.requestCode)
      this.visible = false
    },
    // 清空选择内容
    clearSubmitHandle () {
      // 提交表单
      this.$emit('onBusinessPicked', null, this.requestCode)
      this.visible = false
    }
  }
}
</script>
