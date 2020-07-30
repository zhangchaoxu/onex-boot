<template>
  <el-dialog :visible.sync="visible" title="直传阿里云OSS" :close-on-click-modal="false" :close-on-press-escape="false" width="30%">
    <el-form label-width="120px">
      <el-form-item prop="paramCode" label="OSS参数编码">
        <el-input v-model="paramCode" placeholder="请输入OSS参数编码"/>
      </el-form-item>
      <el-form-item prop="file" label="上传文件">
        <el-upload
                :action="``"
                drag
                ref="upload"
                :disabled="uploadDisabled"
                :http-request="ossUploadRequest"
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
import oss from 'ali-oss'
import dayjs from 'dayjs'

export default {
  data () {
    return {
      visible: false,
      sts: null,
      uploadDisabled: false,
      paramCode: 'OSS_CFG_PUB'
    }
  },
  methods: {
    // 覆盖默认的上传行为，实现自定义上传
    async ossUploadRequest (option) {
      this.uploadDisabled = true
      if (!this.sts || dayjs(this.sts.expiration).isBefore(dayjs().add(5, 'minute'))) {
        // 没有sts,或者expiration有效时间小于5分钟
        // 重新获取sts
        let stsData = await this.$http.get(`/sys/oss/getSts?paramCode=` + this.paramCode).catch(() => {})
        if (stsData.data.code !== 0) {
          return stsData.data
        }
        this.sts = stsData.data.data
      }
      try {
        // 获取OSS配置信息
        let client = oss({
          accessKeyId: this.sts.accessKeyId,
          accessKeySecret: this.sts.accessKeySecret,
          stsToken: this.sts.securityToken,
          bucket: this.sts.bucketName,
          region: this.sts.region,
          secure: this.sts.secure
        })
        let file = option.file
        // 分片同步上传文件
        var ret = await client.multipartUpload(this.sts.prefix + '/' + dayjs().format('YYYYMMDD') + '/' + file.name, file, {
          progress: async function (p) {
            // 返回上传进度
            option.onProgress({ percent: p * 100 })
          }
        })
        if (ret.res.statusCode === 200) {
          return { 'data': this.sts.domain + ret.name, 'code': 0, 'msg': 'OK' }
        } else {
          return { 'code': 500, 'msg': 'upload fail' }
        }
      } catch (error) {
        console.error(error)
        return { 'code': 500, 'msg': 'upload exception' }
      }
    },
    init () {
      this.visible = true
      this.uploadDisabled = false
      this.$nextTick(() => {
        this.$refs.upload.clearFiles()
      })
    },
    // 上传之前
    beforeUploadHandle (file) {
      /* if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
        this.$message.error(this.$t('uploadTip', { 'format': 'jpg、png、gif' }))
        return false
      } */
    },
    // 上传成功
    successHandle (res) {
      this.uploadDisabled = false
      if (res.code !== 0) {
        this.$refs.upload.clearFiles()
        return this.$message.error(res.toast)
      }
      this.$alert(res.data, {
        type: 'success',
        callback: () => {
          this.visible = false
          this.$emit('refreshDataList')
        }
      })
    }
  }
}
</script>
