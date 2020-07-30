<template>
  <el-dialog :visible.sync="visible" title="跟进" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="8">
                <el-form-item label="商机名称" prop="businessName">
                    <span>{{ dataForm.businessName }}</span>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="当前状态" prop="businessStatus">
                    <el-select v-model="dataForm.businessStatus" class="w-percent-100" disabled>
                        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                            <span style="float: left">{{ item.label }}</span>
                            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.tip }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="最新状态" prop="status">
                    <el-select v-model="dataForm.status" placeholder="请选择商机新状态" class="w-percent-100">
                        <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value">
                            <span style="float: left">{{ item.label }}</span>
                            <span style="float: right; color: #8492a6; font-size: 13px">{{ item.tip }}</span>
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="跟进日期" prop="logDate">
                    <el-date-picker v-model="dataForm.logDate" type="date" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd" placeholder="选择跟进日期" style="width: 100%"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="下次跟进日期" prop="nextFollowDate">
                    <el-date-picker v-model="dataForm.nextFollowDate" type="date" value-format="yyyy-MM-dd HH:mm:ss" format="yyyy-MM-dd" placeholder="选择下次跟进日期" style="width: 100%"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-form-item label="内容" prop="content">
            <el-input v-model="dataForm.content" placeholder="请输入跟进内容" type="textarea"/>
        </el-form-item>
        <el-form-item label="附件" prop="attachment">
            <file-upload ref="fileUpload" v-model="dataForm.attachment" :limit="4" ossParamCode="OSS_CFG_PRI" :tips="`支持图片、文档和压缩包,大小不超过10M`"/>
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
import FileUpload from '@/components/file-upload'
import dayjs from 'dayjs'

export default {
  mixins: [mixinFormModule],
  components: { FileUpload },
  data: function () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/crm/businessLog/save`,
        dataFormUpdateURL: `/crm/businessLog/update`,
        dataFormInfoURL: `/crm/businessLog/info?id=`
      },
      // 状态选项
      statusOptions: [{
        value: 1,
        label: '阶段1',
        tip: '赢单率10%'
      }, {
        value: 2,
        label: '阶段2',
        tip: '赢单率30%'
      }, {
        value: 3,
        label: '阶段3',
        tip: '赢单率50%'
      }, {
        value: 10,
        label: '赢单',
        tip: '赢得订单'
      }, {
        value: -10,
        label: '输单',
        tip: '输了订单'
      }, {
        value: 0,
        label: '无效',
        tip: '商机无效'
      }],
      dataForm: {
        businessId: '',
        businessName: '',
        type: 'followup',
        status: '',
        businessStatus: '',
        logDate: '',
        nextFollowDate: '',
        content: '',
        attachment: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        logDate: [
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
        Promise.all([
          this.getBusinessInfo()
        ]).then(() => {
          this.initFormData()
          if (this.dataForm[this.mixinFormModuleOptions.idKey]) {
            // 修改
          } else {
            // 新增
            // 给一个默认跟进时间
            this.dataForm.logDate = dayjs().format('YYYY-MM-DD HH:mm:ss')
          }
        })
      })
    },
    // 获取商机信息
    getBusinessInfo () {
      this.$http.get(`crm/business/info?id=` + this.dataForm.businessId).then(({ data: res }) => {
        if (res.code !== 0) {
          this.$message.error(res.toast)
        } else {
          this.dataForm.status = res.data.status
          this.dataForm.businessStatus = res.data.status
          this.dataForm.businessName = res.data.name
        }
      }).catch(resp => {
        console.error(resp)
        this.$message.error(this.$t('prompt.apierror') + resp)
      })
    }
  }
}
</script>
