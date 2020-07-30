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
                    <el-form-item label="级别" prop="level">
                        <el-select v-model="dataForm.level" placeholder="级别" clearable class="w-percent-100">
                            <el-option label="重点" :value="1"/>
                            <el-option label="普通" :value="2"/>
                            <el-option label="非优先" :value="3"/>
                        </el-select>
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
                    <el-form-item label="联系电话" prop="mobile">
                        <el-input v-model="dataForm.mobile" placeholder="联系电话"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="客户来源" prop="source">
                        <el-select v-model="dataForm.source" filterable allow-create default-first-option placeholder="请选择客户来源" class="w-percent-100">
                            <el-option v-for="item in sourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="成交状态" prop="dealStatus">
                        <el-radio-group v-model="dataForm.dealStatus" size="small">
                            <el-radio-button :label="0">未成交</el-radio-button>
                            <el-radio-button :label="1">已成交</el-radio-button>
                        </el-radio-group>
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
        dataFormSaveURL: `/crm/customer/save`,
        dataFormUpdateURL: `/crm/customer/update`,
        dataFormInfoURL: `/crm/customer/info?id=`
      },
      // 来源选项
      sourceOptions: [{
        value: '促销',
        label: '促销'
      }, {
        value: '广告',
        label: '广告'
      }, {
        value: '转介绍',
        label: '转介绍'
      }, {
        value: '陌拜',
        label: '陌拜'
      }, {
        value: '电话咨询',
        label: '电话咨询'
      }, {
        value: '网上咨询',
        label: '网上咨询'
      }],
      dataForm: {
        id: '',
        name: '',
        sort: 0,
        level: '',
        source: '',
        dealStatus: '',
        contacts: '',
        telephone: '',
        mobile: '',
        content: '',
        status: 1,
        regionName: '',
        regionCode: '',
        address: '',
        lat: '',
        remark: '',
        lng: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        level: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        source: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        dealStatus: [
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
