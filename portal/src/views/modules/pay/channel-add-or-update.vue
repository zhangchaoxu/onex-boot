<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item prop="name" :label="$t('base.name')">
                        <el-input v-model="dataForm.name" :placeholder="$t('base.name')"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="支付类型" prop="payType">
                        <el-select v-model="dataForm.payType" filterable allow-create default-first-option placeholder="请选择支付类型" class="w-percent-100">
                            <el-option v-for="item in payTypeOptions" :key="item.value" :label="item.label" :value="item.value"/>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="回调地址" prop="notifyUrl">
                <el-input v-model="dataForm.notifyUrl" placeholder="回调地址"/>
            </el-form-item>
            <el-form-item label="参数" prop="param">
                <el-input v-model="dataForm.param" placeholder="参数">
                    <el-link type="primary" href="https://www.bejson.com/jsoneditoronline/" target="_blank" :underline="false" slot="append">在线json编辑</el-link>
                </el-input>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="dataForm.remark" placeholder="备注" type="textarea"/>
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-radio-group v-model="dataForm.status" size="small">
                    <el-radio-button :label="0">停用</el-radio-button>
                    <el-radio-button :label="1">启用</el-radio-button>
                </el-radio-group>
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

export default {
  mixins: [mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/pay/channel/save`,
        dataFormUpdateURL: `/pay/channel/update`,
        dataFormInfoURL: `/pay/channel/info?id=`
      },
      // 支付类型选项
      payTypeOptions: [{
        value: 'WECHAT_JSAPI',
        label: '微信JSAPI'
      }, {
        value: 'WECHAT_NATIVE',
        label: '微信NATIVE'
      }, {
        value: 'WECHAT_APP',
        label: '微信APP'
      }, {
        value: 'WECHAT_MWEB',
        label: '微信MWEV'
      }, {
        value: 'ALIPAY',
        label: '支付宝'
      }],
      dataForm: {
        id: '',
        name: '',
        sort: 0,
        payType: '',
        param: '',
        remark: '',
        notifyUrl: '',
        status: 1,
        tenantId: '',
        tenantName: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        payType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        param: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        tenantId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        tenantName: [
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
      })
    }
  }
}
</script>
