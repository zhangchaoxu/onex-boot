import CryptoJS from 'crypto-js'

/**
 * AES加密
 * @param raw 明文
 * @param key 加密key
 * @returns {string}
 */
export function aesEncrypt (raw, key) {
  key = key || '1234567890adbcde'
  return CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse(raw), CryptoJS.enc.Utf8.parse(key), {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  }).toString()
}

/**
 * 解密
 * @param cipher 密文
 * @param key 加密key
 * @returns {string}
 */
export function aesDecrypt (cipher, key) {
  key = key || '1234567890adbcde'
  return CryptoJS.enc.Utf8.stringify(CryptoJS.AES.decrypt(cipher, CryptoJS.enc.Utf8.parse(key), {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  })).toString()
}
