/***
 * 上传工具
 *
 * @author Charles(zhangchaoxu@gmail.com)
 */
import Vue from 'vue'

let vue = new Vue()

/**
 * 图片上传之前的格式和大小检查
 * @param file 文件
 * @param size 文件大小尺寸
 * @returns {boolean}
 */
export function beforeImageUpload (file, size) {
  // 默认限制最大8M
  if (undefined === size) {
    size = 8
  }
  // 是否允许格式
  const isAllowType = (file.type === 'image/jpeg' || file.type === 'image/jpeg' || file.type === 'image/png')
  // 是否大小范围内
  const isLtLimit = file.size / 1024 / 1024 < size
  if (!isAllowType) {
    vue.$message.error('只支持jpg,png,jpeg格式文件!')
    return false
  }
  if (!isLtLimit) {
    vue.$message.error('上传文件大小不能超过 ' + size + 'MB')
    return false
  }
  return true
}

/**
 * Apk上传之前的格式和大小检查
 * @param file 文件
 * @param size 文件大小尺寸
 * @returns {boolean}
 */
export function beforeApkUpload (file, size) {
  // 默认限制最大200M
  if (undefined === size) {
    size = 200
  }
  // 是否图片格式
  const isAllowType = file.type === 'application/vnd.android.package-archive'
  // 是否大小范围内
  const isLtLimit = file.size / 1024 / 1024 < size
  if (!isAllowType) {
    vue.$message.error('只支持apk格式文件!')
    return false
  }
  if (!isLtLimit) {
    vue.$message.error('上传文件大小不能超过 ' + size + 'MB')
    return false
  }
  return true
}

/**
 * Excel上传之前的格式和大小检查
 * @param file 文件
 * @param size 文件大小尺寸
 * @returns {boolean}
 */
export function beforeExcelUpload (file, size) {
  // 默认限制最大200M
  if (undefined === size) {
    size = 200
  }
  // 是否图片格式
  const isAllowType = (file.type === 'application/vnd.ms-excel' || file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet')
  // 是否大小范围内
  const isLtLimit = file.size / 1024 / 1024 < size
  if (!isAllowType) {
    vue.$message.error('只支持xls,xlsx格式文件!')
    return false
  }
  if (!isLtLimit) {
    vue.$message.error('上传文件大小不能超过 ' + size + 'MB')
    return false
  }
  return true
}

/**
 * 文件上传超过数量
 * @param files
 * @param fileList
 */
export function fileUploadLimitExceedHandle (files, fileList) {
  vue.$message.warning(`当前限制选择 9 个文件,本次选择了 ${files.length} 个文件,共选择了 ${files.length + fileList.length} 个文件`)
}
