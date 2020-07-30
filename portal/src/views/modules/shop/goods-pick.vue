<template>
    <el-button icon="el-icon-goods" @click="openPickHandle()">
        <el-dialog title="选择商品" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen">
            <div class="mod-shop__goods">
                <el-form :inline="true" :model="searchDataForm" size="small">
                    <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
                        <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
                            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
                        </el-input>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-input v-model="searchDataForm.name" placeholder="商品名" clearable></el-input>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-input v-model="searchDataForm.sn" placeholder="编号" clearable></el-input>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-select v-model="searchDataForm.marketable" clearable placeholder="在架">
                            <el-option label="上架" :value="1"></el-option>
                            <el-option label="下架" :value="0"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-select v-model="searchDataForm.top" clearable placeholder="置顶">
                            <el-option label="置顶" :value="1"></el-option>
                            <el-option label="不置顶" :value="0"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="getDataList()">{{ $t('query') }}</el-button>
                    </el-form-item>
                </el-form>
                <el-table v-loading="dataListLoading" :data="dataList" ref="dataTable"
                          :select-on-indeterminate="false" @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
                    <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
                    <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
                    <el-table-column prop="name" label="名称" header-align="center" align="center" min-width="120"></el-table-column>
                    <el-table-column prop="sn" label="编号" header-align="center" align="center" width="120"></el-table-column>
                    <el-table-column prop="imgs" label="图片" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-image v-if="scope.row.imgs" lazy class="table-img" :src="scope.row.imgs.split(',')[0]" :preview-src-list="scope.row.imgs.split(',')" fit="cover"/>
                        </template>
                    </el-table-column>
                    <el-table-column prop="marketable" label="上架" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.marketable === 0" type="danger">下架</el-tag>
                            <el-tag v-else-if="scope.row.marketable === 1" type="success">上架</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="top" label="置顶" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.top === 0" type="danger">不置顶</el-tag>
                            <el-tag v-else-if="scope.row.top === 1" type="success">置顶</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="type" label="类型" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.type === 1">商品</el-tag>
                            <el-tag v-else-if="scope.row.type === 2" type="success">积分兑换</el-tag>
                            <el-tag v-else-if="scope.row.type === 3" type="danger">赠品</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort" label="排序" header-align="center" align="center" width="100" sortable="custom"/>
                    <el-table-column prop="delivery" label="物流" header-align="center" align="center" width="100">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.delivery === 0" type="danger">不需要</el-tag>
                            <el-tag v-else-if="scope.row.delivery === 1" type="success">需要</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="marketPrice" label="市场价" header-align="center" align="center"></el-table-column>
                    <el-table-column prop="salePrice" label="售价" header-align="center" align="center"></el-table-column>
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
  name: 'goods-pick',
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
        getDataListURL: '/shop/goods/page',
        getDataListIsPage: true
      },
      // 是否可见
      visible: false,
      // 全屏
      fullscreen: false,
      // 搜索条件
      searchDataForm: {
        name: '',
        top: '',
        status: '',
        delivery: '',
        marketable: '',
        title: '',
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
      this.$emit('onGoodsPicked', this.dataListSelections, this.requestCode)
      this.visible = false
    },
    // 清空选择内容
    clearSubmitHandle () {
      // 提交表单
      this.$emit('onGoodsPicked', null, this.requestCode)
      this.visible = false
    }
  }
}
</script>
