/**
 * 商品选择器 module
 *
 * @author Charles zhangchaoxu@gmail.com
 */
export default {
  data () {
    return {
      goodsList: []
    }
  },
  methods: {
    // 商品列表
    getGoodsList (search) {
      return this.$http.get(`/shop/goods/list?limit=20&search=` + search).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.goodsList = res.data
      }).catch(() => {
      })
    }
  }
}
