<template>
    <div>
        <el-upload
                :class="{ hide: uploadFileLength >= limit }"
                :before-upload="beforeFileUpload"
                :on-success="uploadSuccessHandle"
                :list-type="selfListType"
                :limit="limit"
                :accept="selfAcceptFileFormat"
                :drag="drag"
                :file-list="uploadFileList"
                :data="{ paramCode : ossParamCode }"
                :multiple="false"
                :on-exceed="uploadExceedHandle"
                :on-remove="uploadRemoveHandle"
                :action="uploadUrl">
            <i v-if="drag" class="el-icon-upload"/>
            <div v-if="drag" class="el-upload__text" v-html="$t('uploadText')"/>
            <i v-else-if="selfListType === 'picture-card'" class="el-icon-plus"/>
            <el-button v-else size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">{{selfTips}}</div>
        </el-upload>
    </div>
</template>

<script>
/**
 * 文件上传组件
 * 更多接口见 {https://element.eleme.cn/#/zh-CN/component/upload}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
import Cookies from 'js-cookie'
import FileViewer from '@/components/file-viewer'

export default {
  name: 'file-upload',
  components: { FileViewer },
  props: {
    // 绑定的v-model,必须用value
    // 参考https://cn.vuejs.org/v2/guide/components-custom-events.html#%E8%87%AA%E5%AE%9A%E4%B9%89%E7%BB%84%E4%BB%B6%E7%9A%84-v-model
    // 绑定的v-model,必须用value
    value: String,
    content: String,
    // 提示文字
    tips: {
      type: String,
      default: ''
    },
    // 拖拽支持
    drag: {
      type: Boolean,
      default: false
    },
    // 模式,支持image、file、excel、custom
    mode: {
      type: String,
      default: 'file'
    },
    // oss 参数
    ossParamCode: {
      type: String,
      default: ''
    },
    listType: {
      type: String,
      default: ''
    },
    // 接收的文件格式
    acceptFileFormat: {
      type: String,
      default: ''
    },
    // 接受的文件类型
    acceptFileType: {
      type: Array,
      default: function () {
        return ['image/jpeg', 'image/jpeg', 'image/png', 'application/vnd.ms-excel', 'application/msword', 'text/html', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet']
      }
    },
    // 限制个数
    limit: {
      type: Number,
      default: 9
    },
    // 限制大小M
    sizeLimit: {
      type: Number,
      default: 10
    },
    // 上传地址
    uploadUrl: {
      type: String,
      default: `${process.env.VUE_APP_API_URL}/sys/oss/upload?token=${Cookies.get('token')}`
    }
  },
  data () {
    return {
      uploadFileList: [],
      // 已上传文件个数
      uploadFileLength: 0,
      // eslint-disable-next-line vue/no-reserved-keys
      _content: '',
      selfAcceptFileFormat: '',
      selfListType: '',
      selfTips: ''
    }
  },
  mounted () {
    this.init()
  },
  watch: {
    // 监听prop传的value
    value (newVal, oldVal) {
      if (newVal && newVal !== this._content) {
        this.uploadFileList = this.getUploadFileListFromString(newVal)
        this.uploadFileLength = this.uploadFileList.length
      } else if (!newVal) {
        this.uploadFileList = []
        this.uploadFileLength = 0
      }
      this._content = newVal
    },
    content (newVal, oldVal) {
      if (newVal && newVal !== this._content) {
        this.uploadFileList = this.getUploadFileListFromString(newVal)
        this.uploadFileLength = this.uploadFileList.length
      } else if (!newVal) {
        this.uploadFileList = []
        this.uploadFileLength = 0
      }
      this._content = newVal
    }
  },
  methods: {
    // 初始化编辑器
    init () {
      if (this.$el) {
        // 设置内容
        if (this.value || this.content) {
          this.uploadFileList = this.getUploadFileListFromString(this.value || this.content)
        }
        if (this.mode === 'file') {
          this.selfAcceptFileFormat = this.acceptFileFormat || '.jpg,.jpeg,.png,.bmp,.doc,.docx,.xls.xlsx,.ppt,.pptx,.txt,.pdf,.zip,.rar,.7z,.tar'
          this.selfListType = this.listType || 'text'
          this.selfTips = this.tips || '支持图片、文档和压缩包,大小不超过10M'
        } else if (this.mode === 'image') {
          this.selfAcceptFileFormat = this.acceptFileFormat || '.jpg,.jpeg,.png,.bmp'
          this.selfListType = this.listType || 'picture-card'
          this.selfTips = this.tips || '支持图片,大小不超过10M'
        } else if (this.mode === 'excel') {
          this.selfAcceptFileFormat = this.acceptFileFormat || '.xls.xlsx'
          this.selfListType = this.listType || 'text'
          this.selfTips = this.tips || '支持Excel文件,大小不超过10M,最大支持1000条记录'
        }
      }
    },
    getUploadFileListFromString (files) {
      let fileList = []
      if (files) {
        files.split(',').forEach(item => {
          let split = item.split('/')
          fileList.push({ url: item, name: split[split.length - 1] })
        })
      }
      return fileList
    },
    // 获得上传文件路径拼接
    getUploadFileString (fileList) {
      if (!fileList) {
        fileList = this.uploadFileList
      }
      let files = []
      fileList.forEach(item => {
        if (item.status === 'success') {
          if (item.url) {
            // 原先已上传文件
            files.push(item.url)
          } else if (item.response && item.response.code === 0 && item.response.data) {
            // 未上传文件
            files.push(item.response.data.src)
          }
        }
      })
      return files.join(',')
    },
    // 文件超出数量限制
    uploadExceedHandle (files, fileList) {
      this.$message.warning(`共选择了 ${files.length + fileList.length} 个文件,超出数量限制`)
    },
    // 文件上传成功
    uploadSuccessHandle (res, file, fileList) {
      if (res.code !== 0) {
        return this.$message.error(res.toast)
      } else {
        this._content = this.getUploadFileString(fileList)
        this.uploadFileLength = fileList.length
        this.$emit('input', this._content)
      }
    },
    // 文件上传失败
    uploadErrorHandle (err, file, fileList) {
      console.log(err)
      console.log(file)
      console.log(fileList)
      this._content = this.getUploadFileString(fileList)
      this.uploadFileLength = fileList.length
      this.$emit('input', this._content)
    },
    // 文件发生变化
    uploadChangeHandle (file, fileList) {
    },
    // 文件移除成功
    uploadRemoveHandle (file, fileList) {
      this._content = this.getUploadFileString(fileList)
      this.uploadFileLength = fileList.length
      this.$emit('input', this._content)
    },
    // 预览上传文件
    uploadPreviewHandle () {
      if (this.mode !== 'image') {
        // 只有image模式支持预览
        return
      }
      this.$refs.fileViewer.selfFile = { url: this.uploadFileList[0].url, name: 'ssss' }
      this.$refs.fileViewer.imageViewerVisible = true
      /* this.$refs.fileViewer.$el.click()
      let imgList = []
      this.uploadFileList.forEach(item => {
        imgList.push(item.url)
      })
      this.imageViewerHandle(imgList) */
    },
    // 文件上传检查
    beforeFileUpload (file) {
      console.log(file.type)
      // 是否允许格式
      const isAllowType = true // (file.type === 'image/jpeg' || file.type === 'image/jpeg' || file.type === 'image/png')
      // 是否大小范围内
      const isLtLimit = file.size / 1024 / 1024 < this.sizeLimit
      if (!isAllowType) {
        this.$message.error('只支持' + this.acceptFileFormat + '格式文件!')
        return false
      }
      if (!isLtLimit) {
        this.$message.error('上传文件大小不能超过 ' + this.sizeLimit + 'MB')
        return false
      }
      return true
    }
  }
}
</script>
