<template>
  <el-card :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="8">
                <el-form-item label="运单号" prop="code">
                    <el-input v-model="dataForm.code" placeholder="请输入运单号"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="发货人" prop="sender">
                    <el-input v-model="dataForm.sender" placeholder="请输入发货人"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="运费" prop="priceTotal">
                    <el-input-number v-model="dataForm.priceTotal" placeholder="输入运费总价" controls-position="right" :min="0" :max="9999999" :precision="2"
                                     :step="1" class="w-percent-100"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="8">
                <el-form-item label="船公司" prop="carrierOwner">
                    <el-select v-model="dataForm.carrierOwner" filterable allow-create default-first-option placeholder="请选择船公司" class="w-percent-100">
                        <el-option v-for="item in carrierFromList" :key="item.value" :label="item.name" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="船名" prop="carrierName">
                    <el-input v-model="dataForm.carrierName" placeholder="请输入船名"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="船次" prop="carrierNo">
                    <el-input v-model="dataForm.carrierNo" placeholder="请输入船次"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="装货港" prop="carrierFrom">
                    <el-select v-model="dataForm.carrierFrom" filterable allow-create default-first-option placeholder="请选择装货港" class="w-percent-100">
                        <el-option v-for="item in portsList" :key="item.value" :label="item.name" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="离港日" prop="carrierFromDate">
                    <el-date-picker
                            class="w-percent-100"
                            v-model="dataForm.carrierFromDate"
                            type="date"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择离港日"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="卸货港" prop="carrierTo">
                    <el-select v-model="dataForm.carrierTo" filterable allow-create default-first-option placeholder="请选择卸货港" class="w-percent-100">
                        <el-option v-for="item in portsList" :key="item.value" :label="item.name" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="到港日" prop="carrierToDate">
                    <el-date-picker
                            class="w-percent-100"
                            v-model="dataForm.carrierToDate"
                            type="date"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择到港日"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-divider>其它费用</el-divider>
        <el-row>
            <el-col :span="8">
                <el-form-item label="堆存费" prop="price1">
                    <el-input-number @change="calcPriceTotal" v-model="dataForm.price1" placeholder="输入堆存费" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1"
                                     class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="修箱费" prop="price2">
                    <el-input-number @change="calcPriceTotal" v-model="dataForm.price2" placeholder="输入修箱费" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1"
                                     class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="吊机费" prop="price3">
                    <el-input-number @change="calcPriceTotal" v-model="dataForm.price3" placeholder="输入吊机费" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1"
                                     class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="服务费" prop="price4">
                    <el-input-number @change="calcPriceTotal" v-model="dataForm.price4" placeholder="输入服务费" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1"
                                     class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="转站费" prop="price5">
                    <el-input-number @change="calcPriceTotal" v-model="dataForm.price5" placeholder="输入转站费" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1"
                                     class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="困难作业费" prop="price6">
                    <el-input-number @change="calcPriceTotal" v-model="dataForm.price6" placeholder="输入困难作业费" controls-position="right" :min="0" :max="9999999" :precision="2" :step="1"
                                     class="w-percent-100"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
            <el-input v-model="dataForm.remark" placeholder="备注" type="textarea"/>
        </el-form-item>
        <el-divider>货物</el-divider>
        <el-table :data="dataForm.waybillItemList" border ref="waybillItemListTable"
                  show-summary :summary-method="getWaybillItemSummaries" style="width: 100%;">
            <el-table-column prop="supplierName" label="供应商" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.supplierName"/>
                    <span v-else>{{scope.row.supplierName}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="code" label="箱号" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.code"/>
                    <span v-else>{{scope.row.code}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="sealCode" label="封号" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.sealCode"/>
                    <span v-else>{{scope.row.sealCode}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="goodsType" label="品种" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.goodsType"/>
                    <span v-else>{{scope.row.goodsType}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="goods" label="货名" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.goods"/>
                    <span v-else>{{scope.row.goods}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="qty" label="数量(吨)" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input-number v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.qty" controls-position="right"
                                     :min="0" :max="9999999" :precision="2" :step="1" class="w-percent-100" @change="onWaybillItemChangeHandle(scope.$index)"/>
                    <span v-else>{{scope.row.qty}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="price" label="单价(元)" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input-number v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.price" controls-position="right"
                                     :min="0" :max="9999999" :precision="2" :step="1" class="w-percent-100" @change="onWaybillItemChangeHandle(scope.$index)"/>
                    <span v-else>{{scope.row.price}}</span>
                </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="小计(元)" header-align="center" align="center" width="100" :formatter="numberFmt"/>
            <el-table-column prop="remark" label="备注" header-align="center" align="center" min-width="100">
                <template slot-scope="scope">
                    <el-input v-if="waybillItemEditIndex === scope.$index" size="small" v-model="scope.row.remark"/>
                    <span v-else>{{scope.row.remark}}</span>
                </template>
            </el-table-column>
            <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="120">
                <template slot-scope="scope">
                    <el-button type="text" size="small" v-if="waybillItemEditIndex === scope.$index" @click="waybillItemEditIndex = -1">完成</el-button>
                    <el-button type="text" size="small" v-else @click="editOrderItem(scope.$index)">{{ $t('update') }}</el-button>
                    <el-button type="text" size="small" @click="removeOrderItem(scope.$index)">{{ $t('delete') }}</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div style="text-align: center; margin-top: 20px;">
            <!--<el-button type="success">新增货品</el-button>-->
            <waybill-item-pick class="small-button" btnText="选择订货单" type="multi" @onWaybillItemPicked="onWaybillItemPicked"/>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('save') }}</el-button>
        </div>
      </el-form>
  </el-card>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import mixinBaseModule from '@/mixins/base-module'
import WaybillItemPick from './waybill-item-pick'

export default {
  mixins: [mixinFormModule, mixinBaseModule],
  components: { WaybillItemPick },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/tms/waybill/save`,
        dataFormUpdateURL: `/tms/waybill/update`,
        dataFormInfoURL: `/tms/waybill/info?id=`
      },
      dataForm: {
        id: '',
        code: '',
        sender: '',
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
      // 港口列表
      portsList: [
        { name: '镇海', value: '镇海' },
        { name: '大榭', value: '大榭' }
      ],
      // 船公司列表
      carrierFromList: [
        { name: '信风', value: '信风' },
        { name: '港通', value: '港通' },
        { name: '中谷', value: '中谷' }
      ],
      // 当前编辑行
      waybillItemEditIndex: -1
    }
  },
  computed: {
    dataRule () {
      return {
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        carrierFromDate: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        carrierName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        carrierNo: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sender: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        carrierTo: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        priceTotal: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        carrierOwner: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  activated () {
    this.dataForm.id = this.$route.query.id
    let tab = this.$store.state.contentTabs.filter(item => item.name === this.$route.name)[0]
    if (tab) {
      tab.title = this.dataForm.id ? '修改运单' : '新增运单'
      // 根据step刷新数据
      this.init()
    }
  },
  methods: {
    // item变化
    onWaybillItemChangeHandle (index) {
      let item = this.dataForm.waybillItemList[index]
      item.totalPrice = item.price * item.qty
    },
    // 格式化金额
    numberFmt (row, column, cellValue) {
      return !isNaN(cellValue) ? cellValue.toFixed(2) : ''
    },
    init () {
      this.formLoading = false
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        this.initFormData()
      })
    },
    // 选中item
    onWaybillItemPicked (result) {
      this.waybillItemEditIndex = -1
      if (result && result.length > 0) {
        result.forEach(item => {
          let waybillItem = {
            id: item.id,
            supplierName: item.supplierName,
            purchaseDate: item.purchaseDate,
            code: item.code,
            sealCode: item.sealCode,
            goods: item.goods,
            goodsType: item.goodsType,
            unit: item.unit,
            qty: item.qty,
            qtyUnload: item.qtyUnload,
            price: item.price,
            remark: item.remark,
            totalPrice: item.price * item.qty
          }
          this.$set(this.dataForm.waybillItemList, this.dataForm.waybillItemList.length, waybillItem)
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
    },
    // 删除产品
    removeOrderItem (index) {
      this.waybillItemEditIndex = -1
      this.dataForm.waybillItemList.splice(index, 1)
    },
    // 编辑产品
    editOrderItem (index) {
      this.waybillItemEditIndex = index
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
    // 表单提交成功
    onFormSubmitSuccess (res) {
      this.$confirm('运单信息保存成功', this.$t('prompt.title'), {
        confirmButtonText: '查看详情',
        cancelButtonText: '关闭',
        type: 'success'
      }).then(() => {
        this.removeCurrentTab({ name: 'tms-waybill-info', query: { id: res.data.id } })
        // 关闭当前tab
      }).catch(() => {
        this.removeCurrentTab()
      })
    },
    onPriceTypeChange () {
      if (this.priceType === 1) {
        // 合计
      } else {
        // 明细
        this.calcPriceTotal()
      }
    },
    // 计算总运费
    calcPriceTotal () {
      this.dataForm.priceTotal = this.dataForm.price1 + this.dataForm.price2 + this.dataForm.price3 + this.dataForm.price4 + this.dataForm.price5 + this.dataForm.price6
    }
  }
}
</script>
