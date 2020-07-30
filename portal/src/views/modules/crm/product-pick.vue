<template>
    <el-button icon="el-icon-s-goods" @click="openPickHandle()">{{ btnText }}
        <el-dialog title="选择产品" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen">
            <div class="mod-crm__product">
                <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()" size="small">
                    <el-form-item class="middle-item" v-if="$hasRole('sysadmin')">
                        <el-input v-model="searchDataForm.tenantName" placeholder="租户" readonly>
                            <tenant-pick class="small-button" slot="append" :userId="searchDataForm.tenantId" @onTenantPicked="onTenantPicked"/>
                        </el-input>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-select v-model="searchDataForm.categoryId" placeholder="分类" class="w-percent-100" clearable>
                            <el-option v-for="item in productCategoryList" :key="item.id" :label="item.name" :value="item.id"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item class="tiny-item">
                        <el-select v-model="searchDataForm.marketable" placeholder="上架" class="w-percent-100" clearable>
                            <el-option v-for="item in marketableOptions" :key="item.value" :label="item.label" :value="item.value"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item class="small-item">
                        <el-input v-model="searchDataForm.search" placeholder="名称/编号" clearable/>
                    </el-form-item>
                    <el-form-item>
                        <el-button @click="getDataList()">{{ $t('query') }}</el-button>
                    </el-form-item>
                </el-form>
                <el-table v-loading="dataListLoading" :data="dataList" ref="dataTable"
                          :select-on-indeterminate="false" @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
                    <el-table-column type="selection" header-align="center" align="center" width="50"/>
                    <el-table-column prop="tenantName" label="租户" header-align="center" align="center" min-width="100" v-if="$hasRole('sysadmin')"/>
                    <el-table-column prop="categoryName" label="类别" header-align="center" align="center" width="120"/>
                    <el-table-column prop="name" label="产品名称" header-align="center" align="center" min-width="120"/>
                    <el-table-column prop="sn" label="产品编码" header-align="center" align="center" min-width="120"/>
                    <el-table-column prop="unit" label="单位" header-align="center" align="center" width="80"/>
                    <el-table-column prop="salePrice" label="标准价" header-align="center" align="center" width="120"/>
                    <el-table-column prop="marketable" label="在架" header-align="center" align="center" width="60">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.marketable === 0" type="danger">下架</el-tag>
                            <el-tag v-else-if="scope.row.marketable === 1" type="success">上架</el-tag>
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
            </div>
        </el-dialog>
    </el-button>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import TenantPick from '../uc/tenant-pick'

export default {
  name: 'product-pick',
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
  mixins: [mixinListModule],
  components: { TenantPick },
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/crm/product/page',
        getDataListIsPage: true
      },
      // 是否可见
      visible: false,
      // 全屏
      fullscreen: false,
      // 产品分类列表
      productCategoryList: [],
      // 是否上架
      marketableOptions: [{
        value: 0,
        label: '下架'
      }, {
        value: 1,
        label: '上架'
      }],
      searchDataForm: {
        categoryId: '',
        marketable: '',
        search: ''
      }
    }
  },
  methods: {
    openPickHandle () {
      this.visible = true
      Promise.all([
        this.getProductCategoryList()
      ]).then(() => {
        this.getDataList()
      })
    },
    // 获取文章分类列表
    getProductCategoryList () {
      this.$http.get('/crm/productCategory/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.productCategoryList = res.data
      }).catch(() => {
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
      this.$emit('onProductPicked', this.dataListSelections, this.requestCode)
      this.visible = false
    },
    // 清空选择内容
    clearSubmitHandle () {
      this.$emit('onProductPicked', null, this.requestCode)
      this.visible = false
    }
  }
}
</script>
