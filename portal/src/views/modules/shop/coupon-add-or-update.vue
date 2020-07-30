<template>
  <el-dialog ref="dialog" :visible.sync="visible" :title="title" :fullscreen="fullscreen" :close-on-click-modal="false" :close-on-press-escape="false">
      <div slot="title">
          <span class="el-dialog__title">{{ title }}</span>
          <button type="button" class="el-dialog__headerbtn" style="right: 50px;" @click="fullscreenHandle"><i class="el-dialog__close el-icon el-icon-full-screen"/></button>
      </div>
      <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="12">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="dataForm.name" placeholder="名称"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="类型" prop="type">
                    <el-select v-model="dataForm.type" placeholder="选择类型" class="w-percent-100">
                        <el-option label="满减券" :value="1"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="领取方式" prop="giveType">
                    <el-radio-group v-model="dataForm.giveType">
                        <el-radio-button :label="1">注册赠送</el-radio-button>
                        <el-radio-button :label="2">积分兑换</el-radio-button>
                        <el-radio-button :label="3">会员领取</el-radio-button>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12" v-if="dataForm.giveType === 2">
                <el-form-item label="所需积分" prop="pointExchange">
                    <el-input-number controls-position="right" :min="0" v-model="dataForm.pointExchange" placeholder="兑换所需积分" class="w-percent-100"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="满足金额" prop="limitPrice">
                    <el-input-number controls-position="right" :min="0" :max="99999" :precision="2" :step="1" v-model="dataForm.limitPrice" placeholder="满面额" class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="减免金额" prop="reducedPrice">
                    <el-input-number controls-position="right" :min="0" :max="99999" :precision="2" :step="1" v-model="dataForm.reducedPrice" placeholder="减免金额" class="w-percent-100"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="16">
                <el-form-item label="有效期" prop="validStartTime">
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
            </el-col>
            <el-col :span="8">
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="dataForm.status" size="small">
                        <el-radio-button :label="1">已激活</el-radio-button>
                        <el-radio-button :label="0">未激活</el-radio-button>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="发放数量" prop="totalQty">
                    <el-input-number controls-position="right" :min="0" v-model="dataForm.totalQty" placeholder="当前数量" class="w-percent-100"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="每人限领" prop="userQtyLimit">
                    <el-input-number controls-position="right" :min="0" v-model="dataForm.userQtyLimit" placeholder="当前数量" class="w-percent-100"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="描述" prop="content">
            <el-input v-model="dataForm.content" placeholder="描述" type="textarea"></el-input>
        </el-form-item>
                </el-form>
    <template slot="footer">
      <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import mixinBaseModule from '@/mixins/base-module'
export default {
  mixins: [mixinFormModule, mixinBaseModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/shop/coupon/save`,
        dataFormUpdateURL: `/shop/coupon/update`,
        dataFormInfoURL: `/shop/coupon/info?id=`
      },
      dateRange: null,
      dataForm: {
        id: '',
        name: '',
        content: '',
        type: 1,
        giveType: 3,
        validStartTime: '',
        limitPrice: '',
        reducedPrice: '',
        validEndTime: '',
        status: 0,
        pointExchange: '',
        totalQty: 0,
        userQtyLimit: 0
      }
    }
  },
  computed: {
    dataRule () {
      return {
        giveType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        limitPrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        reducedPrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        validStartTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        validEndTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        totalQty: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        userQtyLimit: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        pointExchange: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.formLoading = true
      this.visible = true
      this.dateRange = null
      this.$nextTick(() => {
        this.resetForm()
        Promise.all([
          // this.getStoreList()
        ]).then(() => {
          this.initFormData()
        })
      })
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // 赋值日期选择器
      this.dateRange = [this.dataForm.validStartTime, this.dataForm.validEndTime]
    },
    // 商铺列表
    getStoreList (name, callback) {
      return this.$http.get(`/shop/store/list?name=` + name).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        callback(res.data)
      }).catch(() => {
      })
    },
    // 时间区间选择器变化
    dateRangeChangeHandle (value) {
      if (value && value.length === 2) {
        this.dataForm.validStartTime = value[0]
        this.dataForm.validEndTime = value[1]
      } else {
        this.dataForm.validStartTime = ''
        this.dataForm.validEndTime = ''
      }
    }
  }
}
</script>
