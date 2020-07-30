<template>
  <el-card :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" ref="dataForm" label-width="120px" size="small">
        <el-row>
            <el-col :span="8">
                <el-form-item label="运单号" prop="code">
                    <span>{{ dataForm.code }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="发货人" prop="sender">
                    <span>{{ dataForm.sender }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="运费" prop="priceTotal">
                    <span>{{ dataForm.priceTotal }}</span>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="船公司" prop="carrierOwner">
                    <span>{{ dataForm.carrierOwner }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="船名/船次" prop="carrierName">
                    <span>{{ dataForm.carrierName }}/{{ dataForm.carrierNo }}</span>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="装卸货港" prop="carrierFrom">
                    <span>{{ dataForm.carrierFrom }}<i class="el-icon-right"/>{{ dataForm.carrierTo }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="离港日->到港日" prop="carrierFromDate">
                    <span>{{ dataForm.carrierFromDate | dateDayFilter}}<i class="el-icon-right"/>{{ dataForm.carrierToDate | dateDayFilter}}</span>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="8">
                <el-form-item label="堆存费" prop="price1">
                    <span>{{ dataForm.price1 }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="修箱费" prop="price2">
                    <span>{{ dataForm.price2 }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="吊机费" prop="price3">
                    <span>{{ dataForm.price3 }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="服务费" prop="price4">
                    <span>{{ dataForm.price4 }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="转站费" prop="price5">
                    <span>{{ dataForm.price5 }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="困难作业费" prop="price6">
                    <span>{{ dataForm.price6 }}</span>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
            <span>{{ dataForm.remark }}</span>
        </el-form-item>
        <el-divider>货物</el-divider>
        <el-table size="small" :data="dataForm.waybillItemList" border ref="waybillItemListTable"
                  show-summary :summary-method="getWaybillItemSummaries" style="width: 100%;">
            <el-table-column prop="supplierName" label="供应商" header-align="center" align="center" width="150"/>
            <el-table-column prop="purchaseDate" label="进货日期" header-align="center" align="center" width="120" sortable="custom" :formatter="dateDayFmt"/>
            <el-table-column prop="code" label="箱号" header-align="center" align="center" width="120"/>
            <el-table-column prop="sealCode" label="封号" header-align="center" align="center" width="120"/>
            <el-table-column prop="goodsType" label="品种" header-align="center" align="center" width="100"/>
            <el-table-column prop="goods" label="货名" header-align="center" align="center" width="100"/>
            <el-table-column prop="price" label="单价(元)" header-align="center" align="center" width="100"/>
            <el-table-column prop="qty" label="数量(吨)" header-align="center" align="center" width="100"/>
            <el-table-column prop="qtyUnload" label="卸货数量" header-align="center" align="center" width="100"/>
            <el-table-column prop="totalPrice" label="小计(元)" header-align="center" align="center" width="120" :formatter="numberFmt"/>
            <el-table-column prop="remark" label="备注" header-align="center" align="center" show-tooltip-when-overflow/>
        </el-table>
        <div style="text-align: center; margin-top: 20px;">
            <el-button v-if="$hasPermission('tms:waybill:update')" type="info" @click="updateCarrierToDateHandle(dataForm.id)">到港</el-button>
            <el-button v-if="$hasPermission('tms:waybill:update')" type="warning" @click="$router.push({ name: 'tms-waybill-add-or-update', query: { id: dataForm.id } })">{{ $t('update')
                }}</el-button>
        </div>
        <!-- 弹窗, 新增 / 修改 -->
        <update-carriertodate v-if="updateCarriertodateVisible" ref="updateCarriertodate" @refreshDataList="getInfo"/>
      </el-form>
  </el-card>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import UpdateCarriertodate from './waybill-carriertodate-update'

export default {
  mixins: [mixinFormModule, mixinListModule, mixinBaseModule],
  components: { UpdateCarriertodate },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormInfoURL: `/tms/waybill/info?id=`
      },
      // 列表模块参数
      mixinListModuleOptions: {
        activatedIsNeed: false
      },
      dataForm: {
        id: '',
        code: '',
        carrierOwner: '',
        carrierName: '',
        carrierNo: '',
        carrierFromDate: '',
        carrierFrom: '',
        carrierTo: '',
        carrierToDate: '',
        priceType: 1,
        priceTotal: 0,
        price1: 0,
        price2: 0,
        price3: 0,
        price4: 0,
        price5: 0,
        price6: 0,
        price7: 0,
        price8: 0,
        price9: 0,
        status: '',
        remark: '',
        waybillItemList: []
      },
      // 到港
      updateCarriertodateVisible: false
    }
  },
  activated () {
    this.dataForm.id = this.$route.query.id
    if (!this.dataForm.id) {
      this.formLoading = false
      this.$message.error('参数不能为空')
    } else {
      this.initFormData()
    }
  },
  methods: {
    init () {
      this.formLoading = false
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        this.initFormData()
      })
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
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // 防止后台返回null
      if (!this.dataForm.waybillItemList) {
        this.dataForm.waybillItemList = []
      }
      this.dataForm.waybillItemList.forEach(item => {
        item.totalPrice = item.price * item.qty
      })
    },
    // 格式化金额
    numberFmt (row, column, cellValue) {
      return !isNaN(cellValue) ? cellValue.toFixed(2) : ''
    },
    // 修改到港时间
    updateCarrierToDateHandle (id) {
      this.updateCarriertodateVisible = true
      this.$nextTick(() => {
        this.$refs.updateCarriertodate.clear()
        this.$refs.updateCarriertodate.dataForm.id = id
        this.$refs.updateCarriertodate.dataFormMode = 'update'
        this.$refs.updateCarriertodate.init()
      })
    }
  }
}
</script>
