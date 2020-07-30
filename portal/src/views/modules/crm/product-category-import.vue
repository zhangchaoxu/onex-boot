<template>
  <el-dialog :visible.sync="visible" title="产品类别导入" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item label="EXCEL上传" prop="file">
        <el-upload
                ref="upload"
                :action="uploadUrl"
                :before-upload="beforeExcelUpload"
                :on-change="(file) => this.dataForm.file = file"
                :on-remove="() => this.dataForm.file = null"
                :accept="acceptExcelFormat"
                :multiple="false"
                :limit="1"
                :on-exceed="uploadExceedHandle"
                :auto-upload="false">
          <el-button size="small" type="primary">{{ $t('upload.button') }}</el-button>
          <div slot="tip" class="el-upload__tip">{{ $t('upload.tip', { 'format': 'xls、xlsx' }) }}
            <el-link type="primary" :underline="false" href="" target="_blank" style="font-size: 12px;margin-left: 4px;">{{ $t('upload.templatedownload') }}</el-link>
          </div>
        </el-upload>
      </el-form-item>
    </el-form>
    <template slot="footer">
      <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
    </template>
    <el-dialog title="导入结果" :visible.sync="importResultVisible" append-to-body>
      <el-table :data="importResultList">
        <el-table-column prop="code" label="结果" header-align="center" align="center" width="200">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.code=='0'" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="msg" label="提示消息"/>
      </el-table>
      <template slot="footer">
        <el-button type="primary" @click="importResultVisible = false">{{ $t('confirm') }}</el-button>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import { beforeExcelUpload } from '@/utils/upload'

export default {
  mixins: [mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/crm/productCategory/import`
      },
      dataForm: {
        file: null
      },
      // 导入结果
      importResultList: [],
      importResultVisible: false
    }
  },
  computed: {
    dataRule () {
      return {
        file: [
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
        this.$refs.upload.clearFiles()
        this.resetForm()
        this.initFormData()
      })
    },
    // 上传前检查
    beforeExcelUpload (file) {
      beforeExcelUpload(file)
    },
    beforeDateFormSubmit () {
      // 先校验一遍
      this.$refs['dataForm'].validate((valid) => {
        if (!valid) {
          this.formLoading = false
          return false
        }
      })
      // 封装FormData自己提交，不使用upload的上传功能
      this.dataFormSubmitParam = new FormData()
      this.dataFormSubmitParam.append('file', this.dataForm.file.raw)
      return true
    },
    // 表单提交成功
    onFormSubmitSuccess (res) {
      // 关闭导入对话框
      this.visible = false
      this.$emit('refreshDataList')
      // 显示导入结果对话框
      this.importResultList = res.data
      this.importResultVisible = true
    }
  }
}
</script>
