<template>
    <div>
        <el-link :underline="false" type="primary" @click="filePreviewHandle(file.url)">{{ file.name }}</el-link>
        <!-- 弹窗, 图片查看 -->
        <el-image-viewer :z-index="imageViewerZIndex" :url-list="imageViewerPreviewSrcList" ref="imageViewer" v-show="imageViewerVisible" :on-close="closeImageViewerHandle"/>
    </div>
</template>

<script>
import ElImageViewer from 'element-ui/packages/image/src/image-viewer'
import { isImage } from '@/utils/validate'

export default {
  name: 'file-viewer',
  props: {
    file: {
      type: Object,
      default: () => {
      }
    },
    // public or private
    type: {
      type: String,
      default: 'public'
    },
    privateParamCode: {
      type: String,
      default: ''
    }
  },
  components: { ElImageViewer },
  data () {
    return {
      imageViewerZIndex: 2000, // 图片查看器zIndex
      imageViewerPreviewSrcList: [], // 图片查看文件列表
      prevOverflow: '', // 原先的overflow样式
      imageViewerVisible: false // 图片查看器,弹窗visible状态
    }
  },
  methods: {
    // 文件查看器
    filePreviewHandle (url) {
      if (this.type === 'private') {
        this.$http.get(`/sys/oss/presignedUrl?paramCode=` + this.privateParamCode + `&objectName=` + url.split('aliyuncs.com/')[1]).then(({ data: res }) => {
          if (res.code !== 0) {
            return this.$message.error(res.toast)
          } else {
            if (isImage(url)) {
              this.imageViewerHandle(res.data)
            } else {
              this.openLinkHandle(res.data)
            }
          }
        }).catch(resp => {
          return this.$message.error(resp)
        })
      } else {
        if (isImage(url)) {
          this.imageViewerHandle(url)
        } else {
          this.openLinkHandle(url)
        }
      }
    },
    // 图片查看器
    imageViewerHandle (url) {
      // 保留prevent body scroll现场
      this.prevOverflow = document.body.style.overflow
      document.body.style.overflow = 'hidden'
      this.imageViewerVisible = true
      this.imageViewerPreviewSrcList = [url]
    },
    // 新窗口打开链接
    openLinkHandle (url) {
      window.open(url)
    },
    // 关闭图片查看器
    closeImageViewerHandle () {
      // 还原prevent body scroll现场
      document.body.style.overflow = this.prevOverflow
      this.imageViewerVisible = false
    }
  }
}
</script>
