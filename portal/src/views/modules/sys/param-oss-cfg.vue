<template>
  <el-dialog :visible.sync="visible" title="云存储配置" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="paramDataForm" :rules="dataRule" ref="dataForm"  label-width="120px">
      <el-form-item :label="$t('base.type')" size="mini">
        <el-radio-group v-model="paramDataForm.type">
          <el-radio label="aliyun">阿里云</el-radio>
          <el-radio label="local">本地上传</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item prop="code" :label="$t('base.code')">
        <el-input v-model="dataForm.code" :placeholder="$t('base.code')"/>
      </el-form-item>
      <el-form-item prop="remark" :label="$t('base.remark')">
        <el-input v-model="dataForm.remark" :placeholder="$t('base.remark')"/>
      </el-form-item>
      <el-form-item prop="domain" label="域名">
        <el-input v-model="paramDataForm.domain" placeholder="OSS域名或自定义域名"/>
      </el-form-item>
      <el-form-item prop="prefix" label="路径前缀">
        <el-input v-model="paramDataForm.prefix" placeholder="上传路径前缀,可为空"/>
      </el-form-item>
      <template v-if="paramDataForm.type === 'aliyun'">
        <el-form-item prop="endPoint" label="EndPoint">
          <el-input v-model="paramDataForm.endPoint" placeholder="阿里云EndPoint,可使用内网地址"/>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item prop="accessKeyId" label="KeyId">
              <el-input v-model="paramDataForm.accessKeyId" placeholder="AccessKeyId"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="accessKeySecret" label="KeySecret">
              <el-input v-model="paramDataForm.accessKeySecret" placeholder="AccessKeySecret"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item prop="bucketName" label="Bucket">
              <el-input v-model="paramDataForm.bucketName" placeholder="BucketName"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="region" label="区域">
              <el-input v-model="paramDataForm.region" placeholder="区域 如cn-hangzhou"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item prop="roleArn" label="角色ARN">
              <el-input v-model="paramDataForm.roleArn" placeholder="角色ARN"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="roleSessionName" label="角色SN">
              <el-input v-model="paramDataForm.roleSessionName" placeholder="角色SessionName"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item prop="stsDurationSeconds" label="STS有效秒数">
              <el-input-number v-model="paramDataForm.stsDurationSeconds" placeholder="STS有效秒数" controls-position="right" :min="900" :max="3600" class="w-percent-100"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="secure" label="STS安全访问">
              <el-switch v-model="paramDataForm.secure"/>
            </el-form-item>
          </el-col>
        </el-row>
      </template>
      <template v-else-if="paramDataForm.type === 'local'">
        <el-form-item prop="localPath" label="存储目录">
          <el-input v-model="paramDataForm.localPath" placeholder="上传的存储目录"/>
        </el-form-item>
      </template>
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
        dataFormUpdateURL: `/sys/param/update`,
        dataFormInfoURL: `/sys/param/info?id=`
      },
      dataForm: {
        id: '',
        code: '',
        remark: '',
        content: ''
      },
      paramDataForm: {
        type: '',
        domain: '',
        prefix: '',
        endPoint: '',
        accessKeyId: '',
        accessKeySecret: '',
        bucketName: '',
        localPath: '',
        secure: false,
        stsDurationSeconds: 3600,
        roleSessionName: '',
        roleArn: '',
        region: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        domain: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        endPoint: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        accessKeyId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        accessKeySecret: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        bucketName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        localPath: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    'dataForm.type' (val) {
      this.$refs['dataForm'].clearValidate()
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
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // json序列化content
      this.paramDataForm = JSON.parse(this.dataForm.content)
    },
    // 表单提交之前的操作
    beforeDateFormSubmit () {
      // 将form转为content的json
      this.dataForm.content = JSON.stringify(this.paramDataForm)
      this.dataFormSubmitParam = this.dataForm
      return true
    }
  }
}
</script>
