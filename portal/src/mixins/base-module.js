import dayjs from 'dayjs'

/**
 * 基础module
 *
 * @author Charles zhangchaoxu@gmail.com
 */
export default {
  data () {
    return {
      // vue模板中,指向的对象是this,而this是没有window这个属性的,需要自定义
      window: window,
      // 日期范围选择器
      dateRangePickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick (picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick (picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }]
      }
    }
  },
  filters: {
    // html格式化
    htmlFilter (cellValue) {
      return cellValue ? cellValue.replace(/<[^>]*>/g, '') : ''
    },
    // 日期格式化
    dateDayFilter (cellValue) {
      return cellValue ? dayjs(cellValue).format('YYYY-MM-DD') : ''
    },
    // 时间格式化
    dateTimeFilter (cellValue) {
      return cellValue ? dayjs(cellValue).format('HH:mm') : ''
    },
    // url文件名格式化,只显示最后的
    urlFileNameFilter (cellValue) {
      let split = cellValue.split('/')
      return split[split.length - 1]
    },
    // 数字格式化,fractionDigits小数位数
    numberFmt (cellValue, fractionDigits) {
      if (isNaN(cellValue)) {
        return cellValue
      } else {
        return cellValue.toFixed(fractionDigits)
      }
    }
  },
  methods: {
    // 文本查看器
    textViewHandle (content, title, customClass) {
      this.$msgbox({
        title: title || '查看',
        message: content,
        cancelButtonText: '关闭',
        showCancelButton: true,
        showConfirmButton: false,
        customClass: customClass || 'el-message-w-60'
      }).then().catch(() => {})
    },
    // html查看器
    htmlViewHandle (content, title, customClass) {
      this.$msgbox({
        title: title || '查看',
        message: content,
        dangerouslyUseHTMLString: true,
        cancelButtonText: '关闭',
        showCancelButton: true,
        showConfirmButton: false,
        customClass: customClass || 'el-message-w-60'
      }).then().catch(() => {})
    },
    // json查看器
    // [vue-json-viewer](https://github.com/chenfengjw163/vue-json-viewer)
    jsonViewHandle (content, title, customClass) {
      let json = null
      if (typeof content === 'string') {
        try {
          json = JSON.parse(content)
        } catch (e) {
          console.error(e.toString())
        }
      }
      if (json === null || !json || typeof json !== 'object') {
        this.textViewHandle(content, title, customClass)
      } else {
        this.$msgbox({
          title: title || '查看',
          message: this.$createElement('json-viewer', { attrs: { value: json, copyable: true } }),
          cancelButtonText: '关闭',
          showCancelButton: true,
          showConfirmButton: false,
          customClass: customClass || 'el-message-w-60'
        }).then().catch(() => {})
      }
    },
    // 新窗口打开链接
    openLinkHandle (url) {
      window.open(url)
    },
    /**
     * 关闭当前tab
     * @param rowLocation 新的打开页面
     */
    removeCurrentTab (rowLocation) {
      let tabName = this.$store.state.contentTabsActiveName
      if (tabName === 'home') {
        return false
      }
      this.$store.state.contentTabs = this.$store.state.contentTabs.filter(item => item.name !== tabName)
      if (this.$store.state.contentTabs.length <= 0) {
        this.$store.state.sidebarMenuActiveName = this.$store.state.contentTabsActiveName = 'home'
        return false
      }
      let tab = this.$store.state.contentTabs[this.$store.state.contentTabs.length - 1]
      if (rowLocation) {
        this.$router.push(rowLocation)
      } else {
        this.$router.push({ name: tab.name, query: tab.query })
      }
    }
  }
}
