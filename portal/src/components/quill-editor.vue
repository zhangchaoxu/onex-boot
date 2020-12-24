<template>
    <div class="quill-editor">
        <slot name="toolbar"/>
        <div ref="editor"/>
        <!-- 图片上传对话框 -->
        <el-dialog width="30%" :visible.sync="insertImageDialogVisible" append-to-body style="padding: 0 10px 15px 10px;">
            <el-tabs v-model="insertImageType" type="card">
                <el-tab-pane label="上传图片" name="upload">
                    <el-upload ref="imageUpload" style="text-align: center;" :action="uploadUrl" drag :before-upload="beforeImageUpload" :on-success="imageUploadSuccessHandle">
                        <i class="el-icon-upload"/>
                        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                        <div class="el-upload__tip" slot="tip">只能上传jpg/png文件,且不超过2M</div>
                    </el-upload>
                </el-tab-pane>
                <el-tab-pane label="网络图片" name="url">
                    <el-input placeholder="请输入图片链接" v-model="insertImageUrl" style="text-align: center;"/>
                </el-tab-pane>
            </el-tabs>
            <template slot="footer" v-if="insertImageType === 'url'">
                <el-button @click="insertImageDialogVisible = false">{{ $t('cancel') }}</el-button>
                <el-button type="primary" @click="insertImageSubmitHandle()">{{ $t('confirm') }}</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script>
/**
 * quill 富文本编辑器
 * 更多接口见{https://quilljs.com/docs/quickstart/}
 * 参考{https://github.com/surmon-china/vue-quill-editor}
 *
 * @author Charles zhangchaoxu@gmail.com
 */
import 'quill/dist/quill.snow.css'
import Quill from 'quill'
import Cookies from 'js-cookie'

// 默认配置项
const defaultOptions = {
  theme: 'snow',
  boundary: document.body,
  modules: {
    toolbar: {
      container: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ 'header': 1 }, { 'header': 2 }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'font': [] }],
        [{ 'align': [] }],
        ['clean'],
        ['link', 'image', 'video']
      ]
    }
  },
  placeholder: '',
  readOnly: false
}

export default {
  name: 'quill-editor',
  props: {
    // 绑定的v-model,必须用value
    value: String,
    content: String,
    // 编辑器高度
    containerHeight: {
      type: String,
      default: '350px'
    },
    // 上传地址
    uploadUrl: {
      type: String,
      default: `${process.env.VUE_APP_API_URL}/sys/oss/upload?token=${Cookies.get('token')}`
    },
    // 是否激活
    disabled: {
      type: Boolean,
      default: false
    },
    // 编辑器属性
    options: {
      type: Object,
      required: false,
      default: () => ({})
    }
  },
  data () {
    return {
      // 图片插入对话框
      insertImageDialogVisible: false,
      // 图片插入方式
      insertImageType: 'upload',
      // 图片插入url
      insertImageUrl: null,
      // 图片插入位置
      insertImageSelectIndex: 0,
      // 富文本编辑器
      quillEditor: null,
      // 配置参数
      // eslint-disable-next-line vue/no-reserved-keys
      _options: {},
      // eslint-disable-next-line vue/no-reserved-keys
      _content: '',
      defaultOptions
    }
  },
  mounted () {
    this.init()
  },
  beforeDestroy () {
    this.quillEditor = null
    delete this.quillEditor
  },
  watch: {
    // Watch content change
    content (newVal, oldVal) {
      if (this.quillEditor) {
        if (newVal && newVal !== this._content) {
          this._content = newVal
          this.quillEditor.pasteHTML(newVal)
        } else if (!newVal) {
          this.quillEditor.setText('')
        }
      }
    },
    // Watch content change
    value (newVal, oldVal) {
      if (this.quillEditor) {
        if (newVal && newVal !== this._content) {
          this._content = newVal
          this.quillEditor.pasteHTML(newVal)
        } else if (!newVal) {
          this.quillEditor.setText('')
        }
      }
    },
    // Watch disabled change
    disabled (newVal, oldVal) {
      if (this.quillEditor) {
        this.quillEditor.enable(!newVal)
      }
    }
  },
  methods: {
    // 初始化编辑器
    init () {
      if (this.$el) {
        // 合并默认配置
        this._options = Object.assign({}, this.defaultOptions, this.options)
        // 初始化定义
        this.quillEditor = new Quill(this.$refs.editor, this._options)
        // 设置enable
        this.quillEditor.enable(false)
        // 设置内容
        if (this.value || this.content) {
          this.quillEditor.pasteHTML(this.value || this.content)
        }
        // Disabled editor
        if (!this.disabled) {
          this.quillEditor.enable(true)
        }
        // 设置高度
        this.quillEditor.container.style.height = this.containerHeight
        // 自定义上传图片功能 (使用element upload组件)
        this.quillEditor.getModule('toolbar').addHandler('image', () => {
          if (this.quillEditor.getSelection()) {
            this.insertImageSelectIndex = this.quillEditor.getSelection().index
            this.insertImageDialogVisible = true
          } else {
            this.$message.error('请将光标放在插入位置')
          }
        })
        // 监听选中内容变化
        this.quillEditor.on('selection-change', range => {
          if (!range) {
            this.$emit('blur', this.quillEditor)
          } else {
            this.$emit('focus', this.quillEditor)
          }
        })
        // 监听内容变化,动态赋值
        this.quillEditor.on('text-change', () => {
          let html = this.$refs.editor.children[0].innerHTML
          const quill = this.quillEditor
          const text = this.quillEditor.getText()
          if (html === '<p><br></p>') html = ''
          this._content = html
          this.$emit('input', this._content)
          this.$emit('change', { html, text, quill })
        })
        // 监听ready事件
        this.$emit('ready', this.quillEditor)
      }
    },
    // 获取内容长度
    getContentLength () {
      // 即使是空,也会有一个\n’, 所以长度是1
      return this.quillEditor.getLength()
    },
    // 是否为空
    isEmpty () {
      // 即使是空,也会有一个\n’, 所以长度是1
      return this.quillEditor.getLength() <= 1
    },
    // 确认插入图片
    insertImageSubmitHandle () {
      if (/^http[s]?:\/\/.*/.test(this.insertImageUrl)) {
        // 将图片插入指定位置
        this.quillEditor.insertEmbed(this.insertImageSelectIndex, 'image', this.insertImageUrl)
        this.insertImageUrl = null
        this.insertImageDialogVisible = false
      } else {
        this.$message.error('请输入正确的图片地址')
      }
    },
    // 富文本编辑器 上传图片成功
    imageUploadSuccessHandle (res, file, fileList) {
      if (res.code !== 0) {
        return this.$message.error(res.toast)
      }
      // 将图片插入指定位置
      this.quillEditor.insertEmbed(this.insertImageSelectIndex, 'image', res.data.src)
      this.$refs.imageUpload.clearFiles()
      this.insertImageDialogVisible = false
    },
    // 图片上传检查
    beforeImageUpload (file) {
      // 默认限制最大8M
      let size = 8
      // 是否允许格式
      const isAllowType = (file.type === 'image/jpeg' || file.type === 'image/jpeg' || file.type === 'image/png')
      // 是否大小范围内
      const isLtLimit = file.size / 1024 / 1024 < size
      if (!isAllowType) {
        this.$message.error('只支持jpg,png,jpeg格式文件!')
        return false
      }
      if (!isLtLimit) {
        this.$message.error('上传文件大小不能超过 ' + size + 'MB')
        return false
      }
      return true
    }
  }
}
</script>

<style>
    /** 汉化语言包,对应css中的文件 */
    .ql-snow .ql-tooltip a.ql-action::after {
        content: '编辑';
    }
    .ql-snow .ql-tooltip a.ql-remove::before {
        content: '移除';
    }

    .ql-snow .ql-tooltip::before {
        content: "访问地址:";
    }

    .ql-snow .ql-tooltip[data-mode=link]::before {
        content: "请输入链接地址:";
    }
    .ql-snow .ql-tooltip.ql-editing a.ql-action::after {
        content: '保存';
    }

    .ql-snow .ql-tooltip[data-mode=formula]::before {
        content: "输入公式";
    }

    .ql-snow .ql-tooltip[data-mode=video]::before {
        content: "请输入视频地址:";
    }
</style>
