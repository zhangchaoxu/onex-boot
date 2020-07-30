<template>
  <el-dialog :visible.sync="visible" title="用户导入" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item label="选择单位" prop="deptId">
        <el-select v-model="dataForm.deptId" clearable placeholder="选择导入单位" @change="(value) => this.dataForm.deptId = value" class="w-percent-100">
          <el-option v-for="item in deptList" :key="item.id" :label="item.name" :value="item.id"/>
        </el-select>
      </el-form-item>
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
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">支持xls,xlsx文件</div>
        </el-upload>
      </el-form-item>
    </el-form>
    <template slot="footer">
      <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
      <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
    </template>
    <el-dialog title="导入结果" :visible.sync="importResultVisible" append-to-body>
      <el-table :data="importResultList">
        <el-table-column prop="result" label="结果" header-align="center" align="center" width="200">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.result" type="success">成功</el-tag>
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
        dataFormUpdateURL: `/uc/user/import`
      },
      // 单位列表
      deptList: [],
      dataForm: {
        deptId: null,
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
        deptId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
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
        Promise.all([
          this.getDeptList()
        ]).then(() => {
          this.initFormData()
        })
      })
    },
    // 获得单位列表
    getDeptList () {
      this.$http.get('/uc/dept/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.deptList = res.data
      }).catch(() => {})
    },
    // 图片上传前检查
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
      // 封装formdata自己提交，不使用upload的上传功能
      this.dataFormSubmitParam = new FormData()
      this.dataFormSubmitParam.append('file', this.dataForm.file.raw)
      this.dataFormSubmitParam.append('deptId', this.dataForm.deptId)
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
