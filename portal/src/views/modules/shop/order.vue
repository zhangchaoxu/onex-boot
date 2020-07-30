<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-shop__order">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
          <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="middle-item">
          <el-input v-model="searchDataForm.userName" placeholder="用户" readonly>
            <user-pick class="small-button" slot="append" :userId="searchDataForm.userId" @onUserPicked="onUserPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.no" placeholder="订单号" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.receiverSearch" placeholder="收件人" clearable/>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-select v-model="searchDataForm.status" placeholder="状态" clearable>
            <el-option label="待支付" :value="0"/>
            <el-option label="待处理" :value="1"/>
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
        <el-form-item v-if="$hasPermission('shop:order:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('shop:order:placeByAdmin')">
          <el-button type="primary" @click="addOrUpdateHandle()">代下单</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
        <el-table-column prop="no" label="订单号" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === -10" type="danger">已取消</el-tag>
            <el-tag v-if="scope.row.status === 0" type="warning">待支付</el-tag>
            <el-tag v-else-if="scope.row.status === 5" type="success">已支付</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payPrice" label="支付价格" header-align="center" align="center" width="150"/>
        <el-table-column prop="receiverConsignee" label="收件人" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-popover trigger="click" placement="top">
              <p>收件人: {{ scope.row.receiverConsignee }}</p>
              <p>电话: {{ scope.row.receiverMobile }}</p>
              <p>区域: {{ scope.row.receiverRegionName }}</p>
              <p>地址: {{ scope.row.receiverAddress }}</p>
              <div slot="reference" class="name-wrapper nowrap">
                {{ scope.row.receiverConsignee }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="expressNo" label="物流" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-popover trigger="click" placement="top">
              <p>物流: {{ scope.row.expressType }}</p>
              <p>物流单: {{ scope.row.expressNo }}</p>
              <div slot="reference" class="name-wrapper nowrap">
                {{ scope.row.expressNo }}
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" header-align="center" align="center" width="160"/>
        <el-table-column prop="updateTime" label="更新时间" header-align="center" align="center" width="160"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-dropdown trigger="click" @command="editActionHandle" class="action-dropdown">
              <span class="el-dropdown-link">{{ $t('handle') }}<i class="el-icon-arrow-down el-icon--right"/></span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-if="$hasPermission('shop:order:update') && scope.row.sysCancelable" :command="composeEditCommandValue('cancel', scope.row)" icon="el-icon-sell">取消</el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('shop:order:update') && scope.row.confirmable" :command="composeEditCommandValue('confirm', scope.row)" icon="el-icon-sell">确认
                </el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('shop:order:update')" :command="composeEditCommandValue('addOrUpdate', scope.row)" icon="el-icon-edit">{{ $t('update') }}</el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('shop:order:update')" :command="composeEditCommandValue('checkWxPay', scope.row)" icon="el-icon-folder-checked">校验微信支付</el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('shop:order:update') && scope.row.payStatus === 0 && scope.row.status === 0" :command="composeEditCommandValue('pay', scope.row)"
                                  icon="el-icon-wallet">支付
                </el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('shop:order:update') && scope.row.payStatus === 1" :command="composeEditCommandValue('refund', scope.row)" icon="el-icon-wallet">退款
                </el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('shop:order:delete')" :command="composeEditCommandValue('delete', scope.row)" icon="el-icon-delete">{{ $t('delete') }}</el-dropdown-item>
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
        @current-change="pageCurrentChangeHandle"/>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    </div>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './order-add-or-update'
import UserPick from '../uc/user-pick'
import TenantPick from '../uc/tenant-pick'
import VueQrcode from '@chenfengyuan/vue-qrcode'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { UserPick, AddOrUpdate, TenantPick, VueQrcode },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/shop/order/page',
        getDataListIsPage: true,
        exportURL: '/shop/order/export',
        deleteURL: '/shop/order/delete',
        deleteBatchURL: '/shop/order/deleteBatch',
        deleteIsBatch: false
      },
      searchDataForm: {
        no: '',
        userName: '',
        userId: '',
        status: '',
        receiverSearch: '',
        startCreateTime: '',
        endCreateTime: '',
        tenantId: '',
        tenantName: ''
      }
    }
  },
  methods: {
    // 新增/修改
    // 其它更多按钮操作
    moreEditActionHandle (command) {
      if (command.command === 'checkWxPay') {
        // 检查微信支付状态
        this.$http.post(`/wx/pay/queryOrder/` + `WX_PAY_CFG_FISH`, { 'outTradeNo': command.row.no }).then(({ data: res }) => {
          if (res.code !== 0) {
            return this.$message.error(res.toast)
          } else {
            console.log(res)
            return this.$message.success(res.data.tradeState)
          }
        }).catch(resp => {
          return this.$message.error(resp)
        })
      } else if (command.command === 'pay') {
        // 支付
        this.$http.post(`/shop/order/pay`, { 'orderId': command.row.id, 'payType': 'WECHAT_NATIVE' }).then(({ data: res }) => {
          if (res.code !== 0) {
            return this.$message.error(res.toast)
          } else {
            const h = this.$createElement
            this.$msgbox({
              title: '请扫码支付',
              message: h('div', { style: 'text-align: center' }, [h('vueQrcode', { attrs: { options: { width: 260 } } }, res.data.codeUrl)]),
              confirmButtonText: '关闭'
            }).then(() => {
              // 刷新列表
              this.getDataList()
            })
          }
        }).catch(resp => {
          return this.$message.error(resp)
        })
      } else if (command.command === 'refund') {
        // 退款
        this.$confirm(this.$t('prompt.info', { 'handle': '退款' }), this.$t('prompt.title'), {
          confirmButtonText: this.$t('confirm'),
          cancelButtonText: this.$t('cancel'),
          type: 'warning'
        }).then(() => {
          // 调用退款接口
          this.$http.post(`/shop/order/refund`, { id: command.row.id }).then(({ data: res }) => {
            if (res.code !== 0) {
              return this.$message.error(res.toast)
            }
            this.$message({
              message: '退款成功',
              type: 'success',
              duration: 500,
              onClose: () => {
                this.getDataList()
              }
            })
          }).catch(resp => {
            return this.$message.error(resp)
          })
        }).catch(() => {
        })
      }
    }
  }
}
</script>
