/**
 * 邮箱
 * @param {*} s
 */
export function isEmail (s) {
  return /^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(s)
}

/**
 * 身份证号码
 * @param {*} s
 */
export function isIDNo (s) {
  return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(s)
}

/**
 * 车牌号
 * @param {*} s
 */
export function isVehicleNo (s) {
  return /(^[\u4E00-\u9FA5]{1}[A-Z0-9]{6}$)|(^[A-Z]{2}[A-Z0-9]{2}[A-Z0-9\u4E00-\u9FA5]{1}[A-Z0-9]{4}$)|(^[\u4E00-\u9FA5]{1}[A-Z0-9]{5}[挂学警军港澳]{1}$)|(^[A-Z]{2}[0-9]{5}$)|(^(08|38){1}[A-Z0-9]{4}[A-Z0-9挂学警军港澳]{1}$)/.test(s)
}

/**
 * 真实姓名
 * @param {*} s
 */
export function isRealName (s) {
  return /^[\u4e00-\u9fa5]{2,16}$/.test(s)
}

/**
 * 手机号码
 * @param {*} s
 */
export function isMobile (s) {
  return /^1[0-9]{10}$/.test(s)
}

/**
 * 电话号码
 * @param {*} s
 */
export function isPhone (s) {
  return /^([0-9]{3,4}-)?[0-9]{7,8}$/.test(s)
}

/**
 * URL地址
 * @param {*} s
 */
export function isURL (s) {
  return /^http[s]?:\/\/.*/.test(s)
}

/**
 * 图片
 * @param {*} s
 */
export function isImage (s) {
  return /\w(\.gif|\.jpeg|\.png|\.jpg|\.bmp)/i.test(s)
}

/**
 * 视频
 * @param {*} s
 */
export function isVideo (s) {
  return /\w(\.mp4|\.avi|\.rmvb|\.flv|\.mpg|\.mov|\.mkv)/i.test(s)
}
