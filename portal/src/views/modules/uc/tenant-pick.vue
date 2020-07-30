<template>
    <el-button icon="el-icon-user" @click="openPickHandle()">
        <el-dialog title="选择租户" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen">
                <div class="mod-uc__tenant">
                    <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
                        <el-form-item class="small-item">
                            <el-input v-model="searchDataForm.name" :placeholder="$t('base.name')" clearable/>
                        </el-form-item>
                        <el-form-item>
                            <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                        </el-form-item>
                    </el-form>
                    <el-table v-loading="dataListLoading" :data="dataList" ref="dataTable"
                            :select-on-indeterminate="false" @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
                        <el-table-column type="selection" header-align="center" align="center" width="50"/>
                        <el-table-column prop="name" label="租户名" header-align="left" align="left"/>
                        <el-table-column prop="validStartTime" label="有效期" header-align="center" align="center" width="300">
                            <template slot-scope="scope">
                                {{ scope.row.validStartTime }}<i class="el-icon-arrow-right"/>{{ scope.row.validEndTime }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
                            <template slot-scope="scope">
                                <el-tag v-if="scope.row.status === 0" type="danger">无效</el-tag>
                                <el-tag v-else-if="scope.row.status === 1" type="success">有效</el-tag>
                            </template>
                        </el-table-column>
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

export default {
  name: 'tenant-pick',
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
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/uc/tenant/page',
        getDataListIsPage: true
      },
      // 是否可见
      visible: false,
      // 全屏
      fullscreen: false,
      searchDataForm: {
        name: ''
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
    dataFormSubmitHandle () {
      // 验证通过,提交表单
      this.$emit('onTenantPicked', this.dataListSelections, this.requestCode)
      this.visible = false
    },
    // 清空选择内容
    clearSubmitHandle () {
      this.$emit('onTenantPicked', null, this.requestCode)
      this.visible = false
    }
  }
}
</script>
