/**
 * 列表基础
 */
import Cookies from 'js-cookie'
import qs from 'qs'
import dayjs from 'dayjs'

export default {
  data () {
    return {
      // 设置属性
      mixinListModuleOptions: {
        activatedIsNeed: true, // 此页面是否在激活（进入）时，调用查询数据列表接口
        getDataListURL: '', // 数据列表接口，API地址
        getDataListIsPage: false, // 数据列表接口，是否需要分页
        deleteURL: '', // 删除接口，API地址
        deleteBatchURL: '', // 删除接口，API地址
        deleteIsBatch: false, // 删除接口，是否需要批量
        deleteIsBatchKey: 'id', // 删除接口，批量状态下由那个key进行标记操作？比如：pid，uid...
        exportURL: '', // 导出接口，API地址
        idKey: 'id' // 默认表主键
      },
      // 默认属性
      searchDataForm: {}, // 查询条件
      dataList: [], // 数据列表
      order: 'desc', // 排序，asc/desc
      orderField: 'create_time', // 排序，字段
      page: 1, // 当前页码
      limit: 10, // 每页数
      total: 0, // 总条数
      dataListLoading: false, // 数据列表，loading状态
      dataListSelections: [], // 数据列表，多选项
      addOrUpdateVisible: false, // 新增/更新，弹窗visible状态
      uploadUrl: '', // 文件上传地址
      changeStatusVisible: false, // 修改状态,弹窗visible状态
      cancelVisible: false, // 取消操作,弹窗visible状态
      importVisible: false, // 导入操作,弹窗visible状态
      dateRange: null // 时间范围
    }
  },
  props: {
    // 加载模式, dialog/tab/embed
    mode: {
      type: String,
      default: 'tab'
    },
    // 懒加载,手动加载数据
    lazyLoad: {
      type: Boolean,
      default: false
    }
  },
  activated () {
    if (this.mixinListModuleOptions.activatedIsNeed && !this.lazyLoad) {
      this.getDataList()
    }
  },
  methods: {
    // 获取数据列表
    getDataList () {
      if (!this.beforeGetDataList()) {
        return
      }
      this.dataListLoading = true
      this.$http.get(
        this.mixinListModuleOptions.getDataListURL,
        {
          params: {
            order: this.order,
            orderField: this.orderField,
            page: this.mixinListModuleOptions.getDataListIsPage ? this.page : null,
            limit: this.mixinListModuleOptions.getDataListIsPage ? this.limit : null,
            ...this.searchDataForm
          }
        }
      ).then(({ data: res }) => {
        if (res.code !== 0) {
          this.onGetListError(res)
        } else {
          this.onGetListSuccess(res)
        }
        this.dataListLoading = false
      }).catch(() => {
        this.dataListLoading = false
      })
    },
    // 查询数据列表
    queryDataList () {
      this.page = 1
      this.getDataList()
    },
    // 获取list之前的操作
    beforeGetDataList () {
      return true
    },
    // list信息获取成功
    onGetListSuccess (res) {
      this.dataList = this.mixinListModuleOptions.getDataListIsPage ? res.data.list : res.data
      this.total = this.mixinListModuleOptions.getDataListIsPage ? res.data.total : res.data.length
    },
    // list信息获取失败
    onGetListError (res) {
      this.dataList = []
      this.total = 0
      return this.$message.error(res.toast)
    },
    // 多选
    dataListSelectionChangeHandle (val) {
      this.dataListSelections = val
    },
    // 排序
    dataListSortChangeHandle (data) {
      if (!data.order || !data.prop) {
        this.order = ''
        this.orderField = ''
        return false
      }
      this.order = data.order.replace(/ending$/, '')
      this.orderField = data.prop.replace(/([A-Z])/g, '_$1').toLowerCase()
      this.getDataList()
    },
    // 分页, 每页条数
    pageSizeChangeHandle (val) {
      this.page = 1
      this.limit = val
      this.getDataList()
    },
    // 分页, 当前页
    pageCurrentChangeHandle (val) {
      this.page = val
      this.getDataList()
    },
    // 新增 / 修改
    addOrUpdateHandle (id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.clear()
        this.$refs.addOrUpdate.dataForm.id = id
        this.$refs.addOrUpdate.dataFormMode = !id ? 'save' : 'update'
        this.$refs.addOrUpdate.init()
      })
    },
    // 查看
    previewHandle (id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.dataForm.id = id
        this.$refs.addOrUpdate.dataFormMode = 'view'
        this.$refs.addOrUpdate.init()
      })
    },
    // 修改状态
    changeStatusHandle (id) {
      this.changeStatusVisible = true
      this.$nextTick(() => {
        this.$refs.changeStatus.dataForm.id = id
        this.$refs.changeStatus.init()
      })
    },
    // 删除
    deleteHandle (id) {
      if (this.mixinListModuleOptions.deleteIsBatch && !id && this.dataListSelections.length <= 0) {
        // 批量删除先检查已选中个数
        return this.$message({
          message: this.$t('prompt.deleteBatch'),
          type: 'warning',
          duration: 500
        })
      }
      // 对话框提示是否删除
      this.$confirm(this.$t('prompt.info', { 'handle': this.$t('delete') }), this.$t('prompt.title'), {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        type: 'warning'
      }).then(() => {
        if (this.mixinListModuleOptions.deleteIsBatch) {
          // 批量删除
          this.$http.delete(`${this.mixinListModuleOptions.deleteBatchURL}`, { 'data': id ? [id] : this.dataListSelections.map(item => item[this.mixinListModuleOptions.deleteIsBatchKey]) })
            .then(({ data: res }) => {
              if (res.code !== 0) {
                return this.$message.error(res.toast)
              }
              this.$message({
                message: this.$t('prompt.success'),
                type: 'success',
                duration: 500,
                onClose: () => {
                  this.getDataList()
                }
              })
            })
        } else {
          // 单个删除
          this.$http.delete(`${this.mixinListModuleOptions.deleteURL}?` + this.mixinListModuleOptions.deleteIsBatchKey + '=' + id)
            .then(({ data: res }) => {
              if (res.code !== 0) {
                return this.$message.error(res.toast)
              }
              this.$message({
                message: this.$t('prompt.success'),
                type: 'success',
                duration: 500,
                onClose: () => {
                  this.getDataList()
                }
              })
            })
        }
      }).catch(() => {
      })
    },
    // 导入
    importHandle () {
      this.importVisible = true
      this.$nextTick(() => {
        this.$refs.import.dataForm.id = null
        this.$refs.import.init()
      })
    },
    // 导出
    exportHandle () {
      const params = qs.stringify({
        'token': Cookies.get('token'),
        ...this.dataForm
      })
      window.location.href = `${process.env.VUE_APP_API_URL}${this.mixinListModuleOptions.exportURL}?${params}`
    },
    // [+] 搜索条件相关
    // 时间区间选择器变化
    dateRangeChangeHandle (value) {
      if (value !== null && value.length === 2) {
        this.searchDataForm.startCreateTime = value[0]
        this.searchDataForm.endCreateTime = value[1]
      } else {
        this.searchDataForm.startCreateTime = ''
        this.searchDataForm.endCreateTime = ''
      }
    },
    // 选中用户
    onUserPicked (result) {
      if (result && result.length > 0) {
        this.searchDataForm.userId = result[0].id
        this.searchDataForm.userName = result[0].username
      } else {
        this.searchDataForm.userId = ''
        this.searchDataForm.userName = ''
      }
    },
    // 选中租户
    onTenantPicked (result) {
      if (result && result.length > 0) {
        this.searchDataForm.tenantId = result[0].id
        this.searchDataForm.tenantName = result[0].name
      } else {
        this.searchDataForm.tenantId = ''
        this.searchDataForm.tenantName = ''
      }
    },
    // 选中客户
    onCustomerPicked (result) {
      if (result && result.length > 0) {
        this.searchDataForm.customerId = result[0].id
        this.searchDataForm.customerName = result[0].name
      } else {
        this.searchDataForm.customerId = ''
        this.searchDataForm.customerName = ''
      }
    },
    // [-] 搜索条件相关
    // [+] 表格内容格式化
    // 日期格式化
    dateDayFmt (row, column, cellValue) {
      return cellValue ? dayjs(cellValue).format('YYYY-MM-DD') : ''
    },
    dateTimeShortFmt (row, column, cellValue) {
      return cellValue ? dayjs(cellValue).format('YYYY-MM-DD HH:mm') : ''
    },
    dateTimeUnixFmt (row, column, cellValue) {
      return cellValue ? dayjs.unix(cellValue / 1000).format('YYYY-MM-DD HH:mm:ss') : ''
    },
    // 时间格式化
    dateTimeFmt (row, column, cellValue) {
      return cellValue ? dayjs(cellValue).format('HH:mm') : ''
    },
    // html格式化
    htmlFmt (row, column, cellValue) {
      return cellValue ? cellValue.replace(/<[^>]*>/g, '') : '无内容'
    },
    // [-] 表格内容格式化
    // 格式化下拉菜单
    composeEditCommandValue (command, row) {
      return {
        'command': command,
        'row': row
      }
    },
    // cell 点击
    cellClickHandle (row, column, cell, event) {
      this.cellViewHandle(row, column)
    },
    cellViewHandle (row, column) {
      // 通过className来区分点击内容
      if (column.className && row[column.property]) {
        if (column.className.indexOf('link') !== -1) {
          if (column.className.indexOf('html') !== -1) {
            this.htmlViewHandle(row[column.property])
          } if (column.className.indexOf('text') !== -1) {
            this.textViewHandle(row[column.property])
          } if (column.className.indexOf('json') !== -1) {
            this.jsonViewHandle(row[column.property])
          }
        }
      }
    },
    // 右侧操作按钮
    editActionHandle (command) {
      if (command.command === 'addOrUpdate') {
        // 新增/修改
        this.addOrUpdateHandle(command.row[this.mixinListModuleOptions.idKey])
      } else if (command.command === 'delete') {
        // 删除
        this.deleteHandle(command.row[this.mixinListModuleOptions.idKey])
      } else {
        this.moreEditActionHandle(command)
      }
    },
    // 其它更多按钮操作
    moreEditActionHandle (command) {

    }
  }
}
