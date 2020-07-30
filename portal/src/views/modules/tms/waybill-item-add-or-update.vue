<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="12">
                <el-form-item label="供应商" prop="supplierName">
                    <el-input v-model="dataForm.supplierName" placeholder="供应商名称"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="进货日期" prop="purchaseDate">
                    <el-date-picker
                            class="w-percent-100"
                            v-model="dataForm.purchaseDate"
                            type="date"
                            value-format="yyyy-MM-dd HH:mm:ss"
                            placeholder="请选择进货日期"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="品种" prop="goodsType">
                    <el-select v-model="dataForm.goodsType" filterable allow-create default-first-option placeholder="请选择品种" class="w-percent-100">
                        <el-option label="一等新粮" value="一等新粮"/>
                        <el-option label="一等陈粮" value="一等陈粮"/>
                        <el-option label="二等新粮" value="二等新粮"/>
                        <el-option label="二等陈粮" value="二等陈粮"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="货名" prop="goods">
                    <el-select v-model="dataForm.goods" filterable allow-create default-first-option placeholder="请选择货名" class="w-percent-100">
                        <el-option label="玉米" value="玉米"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="箱号" prop="code">
                    <el-input v-model="dataForm.code" placeholder="请输入箱号"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="封号" prop="sealCode">
                    <el-input v-model="dataForm.sealCode" placeholder="请输入封号"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="数量(吨)" prop="qty">
                    <el-input-number v-model="dataForm.qty" placeholder="输入数量" controls-position="right" :min="0" :max="9999999" :precision="2" class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="单价(元)" prop="price">
                    <el-input-number v-model="dataForm.price" placeholder="输入单价(元)" controls-position="right" :min="0" :max="9999999" :precision="2" class="w-percent-100"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-divider>小计:{{ dataForm.qty * dataForm.price | numberFmt(2) }}元</el-divider>
        <el-form-item label="备注" prop="remark">
            <el-input v-model="dataForm.remark" placeholder="备注" type="textarea"/>
        </el-form-item>
      </el-form>
    <template slot="footer">
      <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinFormModule from '@/mixins/form-module'
import dayjs from 'dayjs'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/tms/waybillItem/save`,
        dataFormUpdateURL: `/tms/waybillItem/update`,
        dataFormInfoURL: `/tms/waybillItem/info?id=`
      },
      dataForm: {
        id: '',
        supplierName: '',
        code: '',
        sealCode: '',
        goodsType: '',
        goods: '玉米',
        unit: '吨',
        purchaseDate: '',
        qty: '',
        price: '',
        remark: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        supplierName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        goods: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        qty: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        price: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.formLoading = true
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        this.initFormData()
        if (!this.dataForm.id) {
          // 新增
          this.dataForm.purchaseDate = dayjs().format('YYYY-MM-DD 00:00:00')
        }
      })
    }
  }
}
</script>
