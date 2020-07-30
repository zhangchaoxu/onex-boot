/**
 * 配置参考: https://cli.vuejs.org/zh/config/
 */
module.exports = {
  publicPath: process.env.NODE_ENV === 'production' ? './' : '/',
  chainWebpack: config => {
    // 按需引入jquery
    // config.externals({ jquery: 'jQuery' })
    // 按需引入svg
    const svgRule = config.module.rule('svg')
    svgRule.uses.clear()
    svgRule
      .test(/\.svg$/)
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
  },
  productionSourceMap: false,
  devServer: {
    open: true,
    port: 8000,
    overlay: {
      errors: true,
      warnings: true
    }
  }
}
