<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-form-item label="用户" prop="userId">
                <el-input v-model="dataForm.userName" placeholder="会员" readonly>
                    <!-- 用户选择器 -->
                    <user-pick slot="append" :userId="dataForm.userId" @onUserPicked="onUserPicked"/>
                </el-input>
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="收件人" prop="consignee">
                        <el-input v-model="dataForm.consignee" placeholder="收件人"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="联系电话" prop="mobile">
                        <el-input v-model="dataForm.mobile" placeholder="收件人手机号"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="地址" prop="address">
                <el-input v-model="dataForm.address" placeholder="详细地址">
                    <template slot="prepend">{{ dataForm.regionName }}</template>
                    <!-- 位置选择器 -->
                    <amap-loc-pick slot="append" ref="ampLocPick" :poi="{ regionName: dataForm.regionName, regionCd: dataForm.regionCd, address: dataForm.address, lat: dataForm.lat, lng:
                    dataForm.lng }" @onLocPicked="onLocPicked"/>
                </el-input>
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="标签" prop="tag">
                        <el-input v-model="dataForm.tag" placeholder="请输入标签"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="默认" prop="defaultItem">
                        <el-radio-group v-model="dataForm.defaultItem" size="small">
                            <el-radio-button :label="1">默认</el-radio-button>
                            <el-radio-button :label="0">非默认</el-radio-button>
                        </el-radio-group>
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
import mixinBaseModule from '@/mixins/base-module'
import AmapLocPick from '@/components/amap-loc-pick'
import UserPick from '../uc/user-pick'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  components: { AmapLocPick, UserPick },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/shop/receiver/save`,
        dataFormUpdateURL: `/shop/receiver/update`,
        dataFormInfoURL: `/shop/receiver/info?id=`
      },
      dataForm: {
        id: '',
        userId: '',
        regionName: '',
        tag: '',
        regionCode: '',
        address: '',
        consignee: '',
        zipCode: '',
        mobile: '',
        defaultItem: 0
      }
    }
  },
  computed: {
    dataRule () {
      return {
        userId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        regionName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        regionCode: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        address: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        consignee: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        defaultItem: [
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
    },
    // 接受位置选择返回结果
    onLocPicked (result, key) {
      if (result) {
        this.dataForm.regionCode = result.regionCode
        this.dataForm.regionName = result.regionName
        this.dataForm.address = result.address
        this.dataForm.lat = result.lat
        this.dataForm.lng = result.lng
      }
    }
  }
}
</script>
