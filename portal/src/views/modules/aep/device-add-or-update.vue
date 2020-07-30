<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="企业" prop="enterpriseId">
                        <el-select v-model="dataForm.enterpriseId"
                                   filterable remote :remote-method="getEnterpriseList"
                                   placeholder="选择所属企业" class="w-percent-100">
                            <el-option v-for="item in enterpriseList" :key="item.id" :label="item.name" :value="item.id"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="产品" prop="productId">
                        <el-select v-model="dataForm.productId" placeholder="选择所属产品" class="w-percent-100">
                            <el-option v-for="item in productList" :key="item.productId" :label="item.productName" :value="item.productId"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="设备类型" prop="deviceType">
                        <el-select v-model="dataForm.deviceType" placeholder="选择设备类型" class="w-percent-100">
                            <el-option label="开关设备" value="00"/>
                            <el-option label="三相设备" value="ff"/>
                            <el-option label="单相A设备" value="01"/>
                            <el-option label="单相B设备" value="02"/>
                            <el-option label="单相C设备" value="03"/>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="设备名称" prop="deviceName">
                <el-input v-model="dataForm.deviceName" placeholder="请输入设备名称" show-word-limit maxlength="64"/>
            </el-form-item>
            <el-form-item label="IMEI号" prop="imei">
                <el-input v-model="dataForm.imei" placeholder="请根据产品Endpoint格式输入IMEI号" show-word-limit maxlength="15" minlength="15" :disabled="!!dataForm.id"/>
            </el-form-item>
            <el-form-item label="IMSI号" prop="imsi">
                <el-input v-model="dataForm.imsi" placeholder="请根据产品Endpoint格式输入IMSI号" show-word-limit maxlength="15" minlength="0"/>
            </el-form-item>
            <el-row>
                <el-col :span="8">
                    <el-form-item label="自动订阅" prop="autoObserver">
                        <el-switch v-model="dataForm.autoObserver" :active-value="0" :inactive-value="1"/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="推送报警" prop="alarmPush">
                        <el-switch v-model="dataForm.alarmPush" :active-value="1" :inactive-value="0"/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="漏电处理规则" prop="deviceType">
                        <el-select v-model="dataForm.interruptRule" placeholder="选择漏电处理规则" class="w-percent-100">
                            <el-option label="不处理" value="no"/>
                            <el-option label="异相断电" value="rule1"/>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
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
        dataFormSaveURL: `/aep/device/save`,
        dataFormUpdateURL: `/aep/device/update`,
        dataFormInfoURL: `/aep/device/info?id=`
      },
      dataForm: {
        id: '',
        enterpriseId: '',
        enterpriseName: '',
        alarmPush: 1,
        interruptRule: 'rule1',
        productId: '',
        devicePid: '0',
        deviceName: '',
        deviceType: '',
        imei: '',
        imsi: '',
        autoObserver: 0
      },
      // 产品列表
      productList: [],
      // 企业列表
      enterpriseList: []
    }
  },
  computed: {
    dataRule () {
      return {
        productId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        enterpriseId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        alarmPush: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        interruptRule: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        deviceType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        deviceName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        imei: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        autoObserver: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 获取企业列表
    getEnterpriseList (search) {
      this.$http.get(`/aep/enterprise/list?limit=2&search=` + search).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.enterpriseList = res.data
      }).catch(() => {
      })
    },
    // 获取产品列表
    getProductList () {
      this.$http.get('/aep/product/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.productList = res.data
        if (!this.dataForm.id && this.productList.length > 0) {
          // 新增
          this.dataForm.productId = this.productList[0].productId
        }
      }).catch(() => {
      })
    },
    init () {
      this.formLoading = true
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        Promise.all([
          this.getProductList(),
          this.getEnterpriseList('')
        ]).then(() => {
          this.initFormData()
        })
      })
    }
  }
}
</script>
