<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="编码" prop="code">
                        <el-input v-model="dataForm.code" placeholder="编码"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="版本号" prop="versionNo">
                        <el-input-number v-model="dataForm.versionNo" controls-position="right" :min="0" label="版本号" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="版本名称" prop="versionName">
                        <el-input v-model="dataForm.versionName" placeholder="版本名称"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="下载链接" prop="downloadLink">
                <el-input v-model="dataForm.downloadLink" placeholder="下载链接">
                    <el-button slot="append" icon="el-icon-upload" @click="$refs.uploadBtn.$el.click()">
                        <el-upload
                                :action="uploadUrl"
                                :show-file-list="false"
                                :data="{paramCode : 'OSS_CFG_PUB'}"
                                :on-success="uploadSuccessHandle"
                                style="display: none;">
                            <el-button ref="uploadBtn"/>
                        </el-upload>
                    </el-button>
                </el-input>
            </el-form-item>
            <el-form-item label="更新内容" prop="content">
                <el-input v-model="dataForm.content" type="textarea" placeholder="更新记录"/>
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="强制更新">
                        <el-switch v-model="dataForm.forceUpdate" :active-value="1" :inactive-value="0"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="显示在下载页面">
                        <el-switch v-model="dataForm.showInPage" :active-value="1" :inactive-value="0"/>
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
        dataFormSaveURL: `/log/release/save`,
        dataFormUpdateURL: `/log/release/update`,
        dataFormInfoURL: `/log/release/info?id=`
      },
      dataForm: {
        id: '',
        code: '',
        name: '',
        versionNo: '',
        versionName: '',
        content: '',
        downloadLink: '',
        forceUpdate: 0,
        showInPage: 0
      }
    }
  },
  computed: {
    dataRule () {
      return {
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        versionNo: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        versionName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        content: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        downloadLink: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        forceUpdate: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        showInPage: [
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
        this.setUploadUrl()
        this.resetForm()
        this.initFormData()
      })
    },
    // 文件上传成功
    uploadSuccessHandle (res) {
      if (res.code !== 0) {
        return this.$message.error(res.toast)
      } else {
        this.dataForm.downloadLink = res.data.src
      }
    }
  }
}
</script>
