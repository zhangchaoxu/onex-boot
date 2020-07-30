<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="dataForm.name" placeholder="名称"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="dataForm.status" size="small">
                            <el-radio-button :label="0">停用</el-radio-button>
                            <el-radio-button :label="1">正常</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="联系人" prop="contacts">
                        <el-input v-model="dataForm.contacts" placeholder="联系人"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="联系电话" prop="telephone">
                        <el-input v-model="dataForm.telephone" placeholder="联系电话"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="标签" prop="tags">
                <multi-tags-input ref="multiTagsInput" v-model="dataForm.tags" :max="5"/>
            </el-form-item>
            <el-form-item label="地址" prop="address">
                <el-input v-model="dataForm.address" placeholder="详细地址">
                    <template slot="prepend">{{ dataForm.regionName }}</template>
                    <!-- 位置选择器 -->
                    <amap-loc-pick slot="append" ref="ampLocPick" :poi="{ regionName: dataForm.regionName, regionCd: dataForm.regionCd, address: dataForm.address, lat: dataForm.lat, lng:
                    dataForm.lng }" @onLocPicked="onLocPicked"/>
                </el-input>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="dataForm.remark" placeholder="请输入备注" type="textarea"/>
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
import AmapLocPick from '@/components/amap-loc-pick'
import MultiTagsInput from '@/components/multi-tags-input'

export default {
  mixins: [mixinFormModule],
  components: { AmapLocPick, MultiTagsInput },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/aep/enterprise/save`,
        dataFormUpdateURL: `/aep/enterprise/update`,
        dataFormInfoURL: `/aep/enterprise/info?id=`
      },
      dataForm: {
        id: '',
        name: '',
        contacts: '',
        telephone: '',
        remark: '',
        status: '',
        regionName: '',
        regionCode: '',
        address: '',
        lat: '',
        lng: '',
        tags: '',
        tenantId: '',
        tenantName: '',
        alarmPush: '',
        interruptRule: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        alarmPush: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        interruptRule: [
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
