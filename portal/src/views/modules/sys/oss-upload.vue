<template>
  <el-dialog :visible.sync="visible" title="OSS上传" :close-on-click-modal="false" :close-on-press-escape="false" width="30%">
    <el-form label-width="120px">
      <el-form-item prop="paramCode" label="OSS参数编码">
        <el-input v-model="paramCode" placeholder="请输入OSS参数编码"/>
      </el-form-item>
      <el-form-item prop="file" label="上传文件">
        <el-upload
                :action="url"
                :file-list="fileList"
                drag
                multiple
                :data="{paramCode : paramCode}"
                :headers="{token: token}"
                :before-upload="beforeUploadHandle"
                :on-success="successHandle">
          <i class="el-icon-upload"/>
          <div class="el-upload__text" v-html="$t('uploadText')"></div>
          <div class="el-upload__tip" slot="tip">{{ $t('uploadTip', { 'format': 'jpg、png、gif' }) }}</div>
        </el-upload>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
import Cookies from 'js-cookie'
export default {
  data () {
    return {
      visible: false,
      url: '',
      paramCode: 'OSS_CFG_PUB',
      num: 0,
      token: '',
      fileList: []
    }
  },
  methods: {
    init () {
      this.visible = true
      this.token = Cookies.get('token')
      this.url = `${process.env.VUE_APP_API_URL}/sys/oss/upload`
      this.num = 0
      this.fileList = []
    },
    // 上传之前
    beforeUploadHandle (file) {
      /* if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
        this.$message.error(this.$t('uploadTip', { 'format': 'jpg、png、gif' }))
        return false
      } */
      this.num++
    },
    // 上传成功
    successHandle (res, file, fileList) {
      if (res.code !== 0) {
        return this.$message.error(res.toast)
      }
      this.fileList = fileList
      this.num--
      if (this.num === 0) {
        this.$message({
          message: this.$t('prompt.success'),
          type: 'success',
          duration: 500,
          onClose: () => {
            this.visible = false
            this.$emit('refreshDataList')
          }
        })
      }
    }
  }
}
</script>
