<template>
    <el-button icon="el-icon-s-custom" @click="openPickHandle()">
        <el-dialog title="选择客户" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen">
            <div class="mod-crm__customer">
                <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
                    <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
                        <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
                            <tenant-pick class="small-button" slot="append" :id="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
                        </el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-input v-model="searchDataForm.search" placeholder="名称/电话/标签" clearable/>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-select v-model="searchDataForm.level" placeholder="级别" clearable>
                            <el-option label="重点" :value="1"/>
                            <el-option label="普通" :value="2"/>
                            <el-option label="非优先" :value="3"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-select v-model="searchDataForm.dealStatus" placeholder="成交状态" clearable>
                            <el-option label="已成交" :value="1"/>
                            <el-option label="未成交" :value="0"/>
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
                    <el-table-column prop="name" label="客户名" header-align="center" align="center" min-width="100"/>
                    <el-table-column prop="level" label="级别" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.level === 1" type="danger">重点</el-tag>
                            <el-tag v-else-if="scope.row.level === 2">普通</el-tag>
                            <el-tag v-else-if="scope.row.level === 3" type="info">非优先</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="source" label="客户来源" header-align="center" align="center" width="120" show-tooltip-when-overflow/>
                    <el-table-column prop="dealStatus" label="成交状态" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.dealStatus === 1" type="success">已成交</el-tag>
                            <el-tag v-else-if="scope.row.dealStatus === 0" type="info">未成交</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="contacts" label="联系人" header-align="center" align="center" width="120"/>
                    <el-table-column prop="mobile" label="联系手机" header-align="center" align="center" width="120"/>
                    <el-table-column prop="tags" label="标签" header-align="center" align="center">
                        <template slot-scope="scope" v-if="scope.row.tags">
                            <el-tag v-for="item in scope.row.tags.split(',')" :key="item" type="info" style="margin-left: 2px">{{item}}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="address" label="地址" header-align="center" align="center" min-width="120" show-overflow-tooltip/>
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

export default {
  name: 'customer-pick',
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
  components: { TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/crm/customer/page',
        getDataListIsPage: true
      },
      // 是否可见
      visible: false,
      // 全屏
      fullscreen: false,
      // 搜索条件
      searchDataForm: {
        name: '',
        search: '',
        level: '',
        source: '',
        dealStatus: '',
        tenantId: '',
        tenantName: ''
      }
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
      this.$emit('onCustomerPicked', this.dataListSelections, this.requestCode)
      this.visible = false
    },
    // 清空选择内容
    clearSubmitHandle () {
      // 提交表单
      this.$emit('onCustomerPicked', null, this.requestCode)
      this.visible = false
    }
  }
}
</script>
