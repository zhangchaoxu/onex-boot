// 引入js文件
// import './antdesign'

// 引入svg中的图标文件
const svgFiles = require.context('./svg', true, /\.svg$/)
svgFiles.keys().map(item => svgFiles(item))
