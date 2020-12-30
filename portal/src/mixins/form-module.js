/**
 * 表单基础
 */
import Cookies from 'js-cookie'
import debounce from 'lodash/debounce'
import { aesEncrypt } from '@/utils/crypto'
import { isURL } from '@/utils/validate'
import { beforeImageUpload } from '@/utils/upload'

export default {
  data () {
    return {
      // 属性
      mixinFormModuleOptions: {
        dataFormInfoURL: '', // 数据信息接口，API地址
        dataFormSaveURL: '', // 表单保存接口，API地址
        dataFormUpdateURL: '', // 表单修改接口，API地址
        dataFormSubmitConfig: null, // 表单提交配置
        dataFormParamEncrypt: false, // 是否加密参数
        idKey: 'id' // 默认表主键
      },
      // 接口提交参数
      dataFormSubmitParam: {},
      // 默认属性
      dataFormMode: 'save', // 表单模式,save/update/view
      dataForm: {}, // 表单
      visible: false, // 表单visible状态
      title: '', // 表单标题
      fullscreen: false, // 对话框是否全屏
      formLoading: true, // 表单是否加载中
      // 文件上传
      uploadUrl: '', // 文件上传地址
      acceptImageFormat: '.jpg,.jpeg,.png,.bmp', // 支持的图片文件类型
      acceptExcelFormat: '.xls,.xlsx', // 支持的Excel文件类型
      uploadFileList: [], // 已上传的文件
      // 隐藏upload最后的按钮
      hideUpload: false
    }
  },
  activated () {
  },
  methods: {
    // 初始化表单
    initFormData () {
      if (this.dataFormMode === 'update') {
        // 修改
        this.title = this.$t('update')
        this.getInfo()
      } else if (this.dataFormMode === 'save') {
        // 新增
        this.title = this.$t('add')
        this.formLoading = false
      } else if (this.dataFormMode === 'view') {
        // 查看
        this.title = this.$t('view')
        this.getInfo()
      }
    },
    // 获取信息
    getInfo () {
      this.formLoading = true
      this.$http.get(this.mixinFormModuleOptions.dataFormInfoURL + this.dataForm.id).then(({ data: res }) => {
        if (res.code !== 0) {
          this.onGetInfoError(res)
        } else {
          this.onGetInfoSuccess(res)
        }
        this.formLoading = false
      }).catch(resp => {
        console.error(resp)
        this.$message.error(this.$t('prompt.apierror') + resp)
        this.formLoading = false
      })
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
    },
    // form信息获取失败
    onGetInfoError (res) {
      return this.$message.error(res.toast)
    },
    // 表单提交之前的操作
    beforeDateFormSubmit () {
      if (this.mixinFormModuleOptions.dataFormParamEncrypt) {
        // 对参数做加密处理,注意要urlencode
        this.dataFormSubmitParam = encodeURIComponent(aesEncrypt(JSON.stringify(this.dataForm)))
      } else {
        this.dataFormSubmitParam = this.dataForm
      }
      return true
    },
    // 表单提交
    dataFormSubmitHandle: debounce(function () {
      this.formLoading = true
      if (this.beforeDateFormSubmit()) {
        // 验证表单
        this.$refs['dataForm'].validate((valid) => {
          if (!valid) {
            this.formLoading = false
            return false
          }
          // 验证通过,提交表单
          this.$http[this.dataFormMode === 'save' ? 'post' : 'put'](this.dataFormMode === 'save' ? this.mixinFormModuleOptions.dataFormSaveURL : this.mixinFormModuleOptions.dataFormUpdateURL, this.dataFormSubmitParam, this.mixinFormModuleOptions.dataFormSubmitConfig).then(({ data: res }) => {
            if (res.code !== 0) {
              this.onFormSubmitError(res)
            } else {
              this.onFormSubmitSuccess(res)
            }
            this.formLoading = false
          }).catch(resp => {
            this.formLoading = false
          })
        })
      } else {
        this.formLoading = false
      }
    }, 1000, { 'leading': true, 'trailing': false }),
    // 表单提交失败
    onFormSubmitError (res) {
      this.$message.error(res.toast)
    },
    // 表单提交成功
    onFormSubmitSuccess (res) {
      this.$message({
        message: this.$t('prompt.success'),
        type: 'success',
        duration: 500,
        onClose: () => {
          this.visible = false
          this.$emit('refreshDataList')
        }
      })
    },
    // 重置data中的数据,不充重置可能会有一些非form的字段一直保存的问题
    clear () {
      // 使用Object.assign(this.$data, this.$options.data())会把所有数据都清空掉
      // 使用Object.assign(this.$data.dataForm, this.$options.data(.dataForm))不知道为什么不工作
      this.$data.dataForm = this.$options.data().dataForm
    },
    // 重置表单
    resetForm (formName) {
      if (undefined === formName) {
        formName = 'dataForm'
      }
      this.$refs[formName].resetFields()
    },
    // [+] 回调选择器相关
    // 选中用户
    onUserPicked (result) {
      if (result && result.length > 0) {
        this.dataForm.userId = result[0].id
        this.dataForm.userName = result[0].username
      } else {
        this.dataForm.userId = ''
        this.dataForm.userName = ''
      }
    },
    // 选中租户
    onTenantPicked (result) {
      if (result && result.length > 0) {
        this.dataForm.tenantId = result[0].id
        this.dataForm.tenantName = result[0].name
      } else {
        this.dataForm.tenantId = ''
        this.dataForm.tenantName = ''
      }
    },
    // 选中客户
    onCustomerPicked (result) {
      if (result && result.length > 0) {
        this.dataForm.customerId = result[0].id
        this.dataForm.customerName = result[0].name
      } else {
        this.dataForm.customerId = ''
        this.dataForm.customerName = ''
      }
    },
    // [-] 搜索条件相关
    // [+] 图片相关
    // 初始化文件上传
    initUpload () {
      this.setUploadUrl()
      this.uploadFileList = []
    },
    // 设置文件上传地址
    setUploadUrl () {
      this.uploadUrl = `${process.env.VUE_APP_API_URL}/sys/oss/upload?token=${Cookies.get('token')}`
    },
    // 图片上传前检查
    beforeImageUpload (file) {
      return beforeImageUpload(file)
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
        this.uploadFileList = fileList
      }
    },
    // 图片上传失败
    uploadErrorHandle (err, file, fileList) {
      console.log(err)
      console.log(file)
      console.log(fileList)
      this.uploadFileList = fileList
    },
    // 文件发生变化
    uploadChangeHandle (file, fileList) {

    },
    // 图片移除成功
    uploadRemoveHandle (file, fileList) {
      this.uploadFileList = fileList
    },
    // 获得上传文件路径拼接
    getUploadFileString (fileList) {
      if (!fileList) {
        fileList = this.uploadFileList
      }
      let files = []
      fileList.forEach(function (item) {
        if (item.status === 'success') {
          if (isURL(item.url)) {
            files.push(item.url)
          } else if (item.response && item.response.code === 0 && item.response.data) {
            files.push(item.response.data)
          }
        }
      })
      return files.join(',')
    },
    // 设置uploadFileList
    setUploadFileList (imgs) {
      if (imgs) {
        const that = this
        imgs.split(',').forEach(function (item) {
          that.uploadFileList.push({ url: item, name: item })
        })
      }
    },
    // 设置uploadFileList
    setImgsToUploadFileList (uploadFileList, imgs) {
      if (imgs) {
        imgs.split(',').forEach(function (item) {
          uploadFileList.push({ url: item, name: item })
        })
      }
    },
    // 新链接中打开文件
    openFileHandle (file) {
      window.open(file.url)
    },
    // 改变全屏
    fullscreenHandle () {
      this.fullscreen = !this.fullscreen
    }
  }
}
