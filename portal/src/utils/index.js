import Cookies from 'js-cookie'
import store from '@/store'
import router from '@/router'

/**
 * 权限
 * @param {*} key
 */
export function hasPermission (key) {
  return window.SITE_CONFIG['permissions'].indexOf(key) !== -1 || false
}

/**
 * 角色
 * @param {*} key
 */
export function hasRole (key) {
  return window.SITE_CONFIG['roles'].indexOf(key) !== -1 || false
}

/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  store.commit('resetStore')
  Cookies.remove('token')
  window.SITE_CONFIG['dynamicMenuRoutesHasAdded'] = false
}

/**
 * 跳转到登录页面
 */
export function redirectLogin () {
  // 清除登录信息
  clearLoginInfo()
  Promise.all([
    // 跳转到登录页面
    router.replace({ name: 'login' })
  ]).then(() => {
    // 为避免出现Duplicate named routes definition
    // 刷新一下页面,达到清空路由的目地
    // 注意需要在replace login完成后执行,要不然会出现刷新原页面的情况
    location.reload()
  })
}

/**
 * 获取uuid
 */
export function getUUID () {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
    return (c === 'x' ? (Math.random() * 16 | 0) : ('r&0x3' | '0x8')).toString(16)
  })
}

/**
 * 获取icon图标(id)列表
 */
export function getIconList () {
  // 获取el-icon
  // 在https://element.eleme.cn/#/zh-CN/component/icon执行
  // var text = '';document.querySelectorAll('.icon-name').forEach(item => text = text + "'" + item.innerText + "',");console.log(text);
  let elIcons = ['el-icon-platform-eleme', 'el-icon-eleme', 'el-icon-delete-solid', 'el-icon-delete', 'el-icon-s-tools', 'el-icon-setting', 'el-icon-user-solid', 'el-icon-user', 'el-icon-phone', 'el-icon-phone-outline', 'el-icon-more', 'el-icon-more-outline', 'el-icon-star-on', 'el-icon-star-off', 'el-icon-s-goods', 'el-icon-goods', 'el-icon-warning', 'el-icon-warning-outline', 'el-icon-question', 'el-icon-info', 'el-icon-remove', 'el-icon-circle-plus', 'el-icon-success', 'el-icon-error', 'el-icon-zoom-in', 'el-icon-zoom-out', 'el-icon-remove-outline', 'el-icon-circle-plus-outline', 'el-icon-circle-check', 'el-icon-circle-close', 'el-icon-s-help', 'el-icon-help', 'el-icon-minus', 'el-icon-plus', 'el-icon-check', 'el-icon-close', 'el-icon-picture', 'el-icon-picture-outline', 'el-icon-picture-outline-round', 'el-icon-upload', 'el-icon-upload2', 'el-icon-download', 'el-icon-camera-solid', 'el-icon-camera', 'el-icon-video-camera-solid', 'el-icon-video-camera', 'el-icon-message-solid', 'el-icon-bell', 'el-icon-s-cooperation', 'el-icon-s-order', 'el-icon-s-platform', 'el-icon-s-fold', 'el-icon-s-unfold', 'el-icon-s-operation', 'el-icon-s-promotion', 'el-icon-s-home', 'el-icon-s-release', 'el-icon-s-ticket', 'el-icon-s-management', 'el-icon-s-open', 'el-icon-s-shop', 'el-icon-s-marketing', 'el-icon-s-flag', 'el-icon-s-comment', 'el-icon-s-finance', 'el-icon-s-claim', 'el-icon-s-custom', 'el-icon-s-opportunity', 'el-icon-s-data', 'el-icon-s-check', 'el-icon-s-grid', 'el-icon-menu', 'el-icon-share', 'el-icon-d-caret', 'el-icon-caret-left', 'el-icon-caret-right', 'el-icon-caret-bottom', 'el-icon-caret-top', 'el-icon-bottom-left', 'el-icon-bottom-right', 'el-icon-back', 'el-icon-right', 'el-icon-bottom', 'el-icon-top', 'el-icon-top-left', 'el-icon-top-right', 'el-icon-arrow-left', 'el-icon-arrow-right', 'el-icon-arrow-down', 'el-icon-arrow-up', 'el-icon-d-arrow-left', 'el-icon-d-arrow-right', 'el-icon-video-pause', 'el-icon-video-play', 'el-icon-refresh', 'el-icon-refresh-right', 'el-icon-refresh-left', 'el-icon-finished', 'el-icon-sort', 'el-icon-sort-up', 'el-icon-sort-down', 'el-icon-rank', 'el-icon-loading', 'el-icon-view', 'el-icon-c-scale-to-original', 'el-icon-date', 'el-icon-edit', 'el-icon-edit-outline', 'el-icon-folder', 'el-icon-folder-opened', 'el-icon-folder-add', 'el-icon-folder-remove', 'el-icon-folder-delete', 'el-icon-folder-checked', 'el-icon-tickets', 'el-icon-document-remove', 'el-icon-document-delete', 'el-icon-document-copy', 'el-icon-document-checked', 'el-icon-document', 'el-icon-document-add', 'el-icon-printer', 'el-icon-paperclip', 'el-icon-takeaway-box', 'el-icon-search', 'el-icon-monitor', 'el-icon-attract', 'el-icon-mobile', 'el-icon-scissors', 'el-icon-umbrella', 'el-icon-headset', 'el-icon-brush', 'el-icon-mouse', 'el-icon-coordinate', 'el-icon-magic-stick', 'el-icon-reading', 'el-icon-data-line', 'el-icon-data-board', 'el-icon-pie-chart', 'el-icon-data-analysis', 'el-icon-collection-tag', 'el-icon-film', 'el-icon-suitcase', 'el-icon-suitcase-1', 'el-icon-receiving', 'el-icon-collection', 'el-icon-files', 'el-icon-notebook-1', 'el-icon-notebook-2', 'el-icon-toilet-paper', 'el-icon-office-building', 'el-icon-school', 'el-icon-table-lamp', 'el-icon-house', 'el-icon-no-smoking', 'el-icon-smoking', 'el-icon-shopping-cart-full', 'el-icon-shopping-cart-1', 'el-icon-shopping-cart-2', 'el-icon-shopping-bag-1', 'el-icon-shopping-bag-2', 'el-icon-sold-out', 'el-icon-sell', 'el-icon-present', 'el-icon-box', 'el-icon-bank-card', 'el-icon-money', 'el-icon-coin', 'el-icon-wallet', 'el-icon-discount', 'el-icon-price-tag', 'el-icon-news', 'el-icon-guide', 'el-icon-male', 'el-icon-female', 'el-icon-thumb', 'el-icon-cpu', 'el-icon-link', 'el-icon-connection', 'el-icon-open', 'el-icon-turn-off', 'el-icon-set-up', 'el-icon-chat-round', 'el-icon-chat-line-round', 'el-icon-chat-square', 'el-icon-chat-dot-round', 'el-icon-chat-dot-square', 'el-icon-chat-line-square', 'el-icon-message', 'el-icon-postcard', 'el-icon-position', 'el-icon-turn-off-microphone', 'el-icon-microphone', 'el-icon-close-notification', 'el-icon-bangzhu', 'el-icon-time', 'el-icon-odometer', 'el-icon-crop', 'el-icon-aim', 'el-icon-switch-button', 'el-icon-full-screen', 'el-icon-copy-document', 'el-icon-mic', 'el-icon-stopwatch', 'el-icon-medal-1', 'el-icon-medal', 'el-icon-trophy', 'el-icon-trophy-1', 'el-icon-first-aid-kit', 'el-icon-discover', 'el-icon-place', 'el-icon-location', 'el-icon-location-outline', 'el-icon-location-information', 'el-icon-add-location', 'el-icon-delete-location', 'el-icon-map-location', 'el-icon-alarm-clock', 'el-icon-timer', 'el-icon-watch-1', 'el-icon-watch', 'el-icon-lock', 'el-icon-unlock', 'el-icon-key', 'el-icon-service', 'el-icon-mobile-phone', 'el-icon-bicycle', 'el-icon-truck', 'el-icon-ship', 'el-icon-basketball', 'el-icon-football', 'el-icon-soccer', 'el-icon-baseball', 'el-icon-wind-power', 'el-icon-light-rain', 'el-icon-lightning', 'el-icon-heavy-rain', 'el-icon-sunrise', 'el-icon-sunrise-1', 'el-icon-sunset', 'el-icon-sunny', 'el-icon-cloudy', 'el-icon-partly-cloudy', 'el-icon-cloudy-and-sunny', 'el-icon-moon', 'el-icon-moon-night', 'el-icon-dish', 'el-icon-dish-1', 'el-icon-food', 'el-icon-chicken', 'el-icon-fork-spoon', 'el-icon-knife-fork', 'el-icon-burger', 'el-icon-tableware', 'el-icon-sugar', 'el-icon-dessert', 'el-icon-ice-cream', 'el-icon-hot-water', 'el-icon-water-cup', 'el-icon-coffee-cup', 'el-icon-cold-drink', 'el-icon-goblet', 'el-icon-goblet-full', 'el-icon-goblet-square', 'el-icon-goblet-square-full', 'el-icon-refrigerator', 'el-icon-grape', 'el-icon-watermelon', 'el-icon-cherry', 'el-icon-apple', 'el-icon-pear', 'el-icon-orange', 'el-icon-coffee', 'el-icon-ice-tea', 'el-icon-ice-drink', 'el-icon-milk-tea', 'el-icon-potato-strips', 'el-icon-lollipop', 'el-icon-ice-cream-square', 'el-icon-ice-cream-round']
  // 获取ant-desion-icon
  // 在https://www.iconfont.cn/manage/index?spm=a313x.7781069.1998910419.11&manage_type=myprojects&projectId=1717993执行
  // var text = '';document.querySelectorAll('.icon-code-show').forEach(item => text = text + "'" + item.innerText + "',");console.log(text);
  let adIcons = ['ad-icon-scan', 'ad-icon-wrench-fill', 'ad-icon-select', 'ad-icon-tags-fill', 'ad-icon-boxplot', 'ad-icon-bank-fill', 'ad-icon-build', 'ad-icon-camera-fill', 'ad-icon-sliders', 'ad-icon-error-fill', 'ad-icon-laptop', 'ad-icon-crown-fill', 'ad-icon-barcode', 'ad-icon-mail-fill', 'ad-icon-camera', 'ad-icon-car-fill', 'ad-icon-cluster', 'ad-icon-printer-fill', 'ad-icon-gateway', 'ad-icon-shop-fill', 'ad-icon-car', 'ad-icon-setting-fill', 'ad-icon-printer', 'ad-icon-USB-fill', 'ad-icon-read', 'ad-icon-golden-fill', 'ad-icon-cloud-server', 'ad-icon-build-fill', 'ad-icon-cloud-upload', 'ad-icon-boxplot-fill', 'ad-icon-cloud', 'ad-icon-sliders-fill', 'ad-icon-cloud-download', 'ad-icon-alibaba', 'ad-icon-cloud-sync', 'ad-icon-alibabacloud', 'ad-icon-video', 'ad-icon-antdesign', 'ad-icon-notification', 'ad-icon-ant-cloud', 'ad-icon-sound', 'ad-icon-behance', 'ad-icon-radarchart', 'ad-icon-googleplus', 'ad-icon-qrcode', 'ad-icon-medium', 'ad-icon-fund', 'ad-icon-google', 'ad-icon-image', 'ad-icon-IE', 'ad-icon-mail', 'ad-icon-amazon', 'ad-icon-table', 'ad-icon-slack', 'ad-icon-idcard', 'ad-icon-alipay', 'ad-icon-creditcard', 'ad-icon-taobao', 'ad-icon-heart', 'ad-icon-zhihu', 'ad-icon-block', 'ad-icon-HTML', 'ad-icon-error', 'ad-icon-linkedin', 'ad-icon-star', 'ad-icon-yahoo', 'ad-icon-gold', 'ad-icon-facebook', 'ad-icon-heatmap', 'ad-icon-skype', 'ad-icon-wifi', 'ad-icon-CodeSandbox', 'ad-icon-attachment', 'ad-icon-chrome', 'ad-icon-edit', 'ad-icon-codepen', 'ad-icon-key', 'ad-icon-aliwangwang', 'ad-icon-api', 'ad-icon-apple', 'ad-icon-disconnect', 'ad-icon-android', 'ad-icon-highlight', 'ad-icon-sketch', 'ad-icon-monitor', 'ad-icon-Gitlab', 'ad-icon-link', 'ad-icon-dribbble', 'ad-icon-man', 'ad-icon-instagram', 'ad-icon-percentage', 'ad-icon-reddit', 'ad-icon-pushpin', 'ad-icon-windows', 'ad-icon-phone', 'ad-icon-yuque', 'ad-icon-shake', 'ad-icon-Youtube', 'ad-icon-tag', 'ad-icon-Gitlab-fill', 'ad-icon-wrench', 'ad-icon-dropbox', 'ad-icon-tags', 'ad-icon-dingtalk', 'ad-icon-scissor', 'ad-icon-android-fill', 'ad-icon-mr', 'ad-icon-apple-fill', 'ad-icon-share', 'ad-icon-HTML-fill', 'ad-icon-branches', 'ad-icon-windows-fill', 'ad-icon-fork', 'ad-icon-QQ', 'ad-icon-shrink', 'ad-icon-twitter', 'ad-icon-arrawsalt', 'ad-icon-skype-fill', 'ad-icon-verticalright', 'ad-icon-weibo', 'ad-icon-verticalleft', 'ad-icon-yuque-fill', 'ad-icon-right', 'ad-icon-Youtube-fill', 'ad-icon-left', 'ad-icon-yahoo-fill', 'ad-icon-up', 'ad-icon-wechat-fill', 'ad-icon-down', 'ad-icon-chrome-fill', 'ad-icon-fullscreen', 'ad-icon-alipay-circle-fill', 'ad-icon-fullscreen-exit', 'ad-icon-aliwangwang-fill', 'ad-icon-doubleleft', 'ad-icon-behance-circle-fill', 'ad-icon-doubleright', 'ad-icon-amazon-circle-fill', 'ad-icon-arrowright', 'ad-icon-codepen-circle-fill', 'ad-icon-arrowup', 'ad-icon-CodeSandbox-circle-f', 'ad-icon-arrowleft', 'ad-icon-dropbox-circle-fill', 'ad-icon-arrowdown', 'ad-icon-github-fill', 'ad-icon-upload', 'ad-icon-dribbble-circle-fill', 'ad-icon-colum-height', 'ad-icon-googleplus-circle-f', 'ad-icon-vertical-align-botto', 'ad-icon-medium-circle-fill', 'ad-icon-vertical-align-middl', 'ad-icon-QQ-circle-fill', 'ad-icon-totop', 'ad-icon-IE-circle-fill', 'ad-icon-vertical-align-top', 'ad-icon-google-circle-fill', 'ad-icon-download', 'ad-icon-dingtalk-circle-fill', 'ad-icon-sort-descending', 'ad-icon-sketch-circle-fill', 'ad-icon-sort-ascending', 'ad-icon-slack-circle-fill', 'ad-icon-fall', 'ad-icon-twitter-circle-fill', 'ad-icon-swap', 'ad-icon-taobao-circle-fill', 'ad-icon-stock', 'ad-icon-weibo-circle-fill', 'ad-icon-rise', 'ad-icon-zhihu-circle-fill', 'ad-icon-indent', 'ad-icon-reddit-circle-fill', 'ad-icon-outdent', 'ad-icon-alipay-square-fill', 'ad-icon-menu', 'ad-icon-dingtalk-square-fill', 'ad-icon-unorderedlist', 'ad-icon-CodeSandbox-square-f', 'ad-icon-orderedlist', 'ad-icon-behance-square-fill', 'ad-icon-align-right', 'ad-icon-amazon-square-fill', 'ad-icon-align-center', 'ad-icon-codepen-square-fill', 'ad-icon-align-left', 'ad-icon-dribbble-square-fill', 'ad-icon-pic-center', 'ad-icon-dropbox-square-fill', 'ad-icon-pic-right', 'ad-icon-facebook-fill', 'ad-icon-pic-left', 'ad-icon-googleplus-square-f', 'ad-icon-bold', 'ad-icon-google-square-fill', 'ad-icon-font-colors', 'ad-icon-instagram-fill', 'ad-icon-exclaimination', 'ad-icon-IE-square-fill', 'ad-icon-check-circle', 'ad-icon-font-size', 'ad-icon-medium-square-fill', 'ad-icon-CI', 'ad-icon-infomation', 'ad-icon-linkedin-fill', 'ad-icon-Dollar', 'ad-icon-line-height', 'ad-icon-QQ-square-fill', 'ad-icon-compass', 'ad-icon-strikethrough', 'ad-icon-reddit-square-fill', 'ad-icon-close-circle', 'ad-icon-underline', 'ad-icon-twitter-square-fill', 'ad-icon-frown', 'ad-icon-number', 'ad-icon-sketch-square-fill', 'ad-icon-info-circle', 'ad-icon-italic', 'ad-icon-slack-square-fill', 'ad-icon-left-circle', 'ad-icon-code', 'ad-icon-taobao-square-fill', 'ad-icon-down-circle', 'ad-icon-column-width', 'ad-icon-weibo-square-fill', 'ad-icon-EURO', 'ad-icon-check', 'ad-icon-zhihu-square-fill', 'ad-icon-copyright', 'ad-icon-ellipsis', 'ad-icon-zoomout', 'ad-icon-minus-circle', 'ad-icon-dash', 'ad-icon-apartment', 'ad-icon-meh', 'ad-icon-close', 'ad-icon-audio', 'ad-icon-plus-circle', 'ad-icon-enter', 'ad-icon-audio-fill', 'ad-icon-play-circle', 'ad-icon-line', 'ad-icon-robot', 'ad-icon-question-circle', 'ad-icon-minus', 'ad-icon-zoomin', 'ad-icon-Pound', 'ad-icon-question', 'ad-icon-robot-fill', 'ad-icon-right-circle', 'ad-icon-rollback', 'ad-icon-bug-fill', 'ad-icon-smile', 'ad-icon-small-dash', 'ad-icon-bug', 'ad-icon-trademark', 'ad-icon-pause', 'ad-icon-audiostatic', 'ad-icon-time-circle', 'ad-icon-bg-colors', 'ad-icon-comment', 'ad-icon-timeout', 'ad-icon-crown', 'ad-icon-signal-fill', 'ad-icon-earth', 'ad-icon-drag', 'ad-icon-verified', 'ad-icon-YUAN', 'ad-icon-desktop', 'ad-icon-shortcut-fill', 'ad-icon-up-circle', 'ad-icon-gift', 'ad-icon-videocameraadd', 'ad-icon-warning-circle', 'ad-icon-stop', 'ad-icon-switchuser', 'ad-icon-sync', 'ad-icon-fire', 'ad-icon-whatsapp', 'ad-icon-transaction', 'ad-icon-thunderbolt', 'ad-icon-appstoreadd', 'ad-icon-undo', 'ad-icon-check-circle-fill', 'ad-icon-caret-down', 'ad-icon-redo', 'ad-icon-left-circle-fill', 'ad-icon-backward', 'ad-icon-reload', 'ad-icon-down-circle-fill', 'ad-icon-caret-up', 'ad-icon-reloadtime', 'ad-icon-minus-circle-fill', 'ad-icon-caret-right', 'ad-icon-message', 'ad-icon-close-circle-fill', 'ad-icon-caret-left', 'ad-icon-dashboard', 'ad-icon-info-circle-fill', 'ad-icon-fast-backward', 'ad-icon-issuesclose', 'ad-icon-up-circle-fill', 'ad-icon-forward', 'ad-icon-poweroff', 'ad-icon-right-circle-fill', 'ad-icon-fast-forward', 'ad-icon-logout', 'ad-icon-plus-circle-fill', 'ad-icon-search', 'ad-icon-piechart', 'ad-icon-question-circle-fill', 'ad-icon-retweet', 'ad-icon-setting', 'ad-icon-EURO-circle-fill', 'ad-icon-login', 'ad-icon-eye', 'ad-icon-frown-fill', 'ad-icon-step-backward', 'ad-icon-location', 'ad-icon-copyright-circle-fil', 'ad-icon-step-forward', 'ad-icon-edit-square', 'ad-icon-CI-circle-fill', 'ad-icon-swap-right', 'ad-icon-export', 'ad-icon-compass-fill', 'ad-icon-swap-left', 'ad-icon-save', 'ad-icon-Dollar-circle-fill', 'ad-icon-woman', 'ad-icon-Import', 'ad-icon-poweroff-circle-fill', 'ad-icon-plus', 'ad-icon-appstore', 'ad-icon-meh-fill', 'ad-icon-eyeclose-fill', 'ad-icon-close-square', 'ad-icon-play-circle-fill', 'ad-icon-eye-close', 'ad-icon-down-square', 'ad-icon-Pound-circle-fill', 'ad-icon-clear', 'ad-icon-layout', 'ad-icon-smile-fill', 'ad-icon-collapse', 'ad-icon-left-square', 'ad-icon-stop-fill', 'ad-icon-expand', 'ad-icon-play-square', 'ad-icon-warning-circle-fill', 'ad-icon-deletecolumn', 'ad-icon-control', 'ad-icon-time-circle-fill', 'ad-icon-merge-cells', 'ad-icon-codelibrary', 'ad-icon-trademark-circle-fil', 'ad-icon-subnode', 'ad-icon-detail', 'ad-icon-YUAN-circle-fill', 'ad-icon-rotate-left', 'ad-icon-minus-square', 'ad-icon-heart-fill', 'ad-icon-rotate-right', 'ad-icon-plus-square', 'ad-icon-piechart-circle-fil', 'ad-icon-insertrowbelow', 'ad-icon-right-square', 'ad-icon-dashboard-fill', 'ad-icon-insertrowabove', 'ad-icon-project', 'ad-icon-message-fill', 'ad-icon-table1', 'ad-icon-wallet', 'ad-icon-check-square-fill', 'ad-icon-solit-cells', 'ad-icon-up-square', 'ad-icon-down-square-fill', 'ad-icon-formatpainter', 'ad-icon-calculator', 'ad-icon-minus-square-fill', 'ad-icon-insertrowright', 'ad-icon-interation', 'ad-icon-close-square-fill', 'ad-icon-formatpainter-fill', 'ad-icon-check-square', 'ad-icon-codelibrary-fill', 'ad-icon-insertrowleft', 'ad-icon-border', 'ad-icon-left-square-fill', 'ad-icon-translate', 'ad-icon-border-outer', 'ad-icon-play-square-fill', 'ad-icon-deleterow', 'ad-icon-border-top', 'ad-icon-up-square-fill', 'ad-icon-sisternode', 'ad-icon-border-bottom', 'ad-icon-right-square-fill', 'ad-icon-Field-number', 'ad-icon-border-left', 'ad-icon-plus-square-fill', 'ad-icon-Field-String', 'ad-icon-border-right', 'ad-icon-accountbook-fill', 'ad-icon-Function', 'ad-icon-border-inner', 'ad-icon-carryout-fill', 'ad-icon-Field-time', 'ad-icon-border-verticle', 'ad-icon-calendar-fill', 'ad-icon-GIF', 'ad-icon-border-horizontal', 'ad-icon-calculator-fill', 'ad-icon-Partition', 'ad-icon-radius-bottomleft', 'ad-icon-interation-fill', 'ad-icon-index', 'ad-icon-radius-bottomright', 'ad-icon-project-fill', 'ad-icon-Storedprocedure', 'ad-icon-radius-upleft', 'ad-icon-detail-fill', 'ad-icon-Field-Binary', 'ad-icon-radius-upright', 'ad-icon-save-fill', 'ad-icon-Console-SQL', 'ad-icon-radius-setting', 'ad-icon-wallet-fill', 'ad-icon-icon-test', 'ad-icon-adduser', 'ad-icon-control-fill', 'ad-icon-aim', 'ad-icon-deleteteam', 'ad-icon-layout-fill', 'ad-icon-compress', 'ad-icon-deleteuser', 'ad-icon-appstore-fill', 'ad-icon-expend', 'ad-icon-addteam', 'ad-icon-mobile-fill', 'ad-icon-folder-view', 'ad-icon-user', 'ad-icon-tablet-fill', 'ad-icon-file-GIF', 'ad-icon-team', 'ad-icon-book-fill', 'ad-icon-group', 'ad-icon-areachart', 'ad-icon-redenvelope-fill', 'ad-icon-send', 'ad-icon-linechart', 'ad-icon-safetycertificate-f', 'ad-icon-Report', 'ad-icon-barchart', 'ad-icon-propertysafety-fill', 'ad-icon-View', 'ad-icon-pointmap', 'ad-icon-insurance-fill', 'ad-icon-shortcut', 'ad-icon-container', 'ad-icon-securityscan-fill', 'ad-icon-ungroup', 'ad-icon-database', 'ad-icon-file-exclamation-fil', 'ad-icon-sever', 'ad-icon-file-add-fill', 'ad-icon-mobile', 'ad-icon-file-fill', 'ad-icon-tablet', 'ad-icon-file-excel-fill', 'ad-icon-redenvelope', 'ad-icon-file-markdown-fill', 'ad-icon-book', 'ad-icon-file-text-fill', 'ad-icon-filedone', 'ad-icon-file-ppt-fill', 'ad-icon-reconciliation', 'ad-icon-file-unknown-fill', 'ad-icon-file-exception', 'ad-icon-file-word-fill', 'ad-icon-filesync', 'ad-icon-file-zip-fill', 'ad-icon-filesearch', 'ad-icon-file-pdf-fill', 'ad-icon-solution', 'ad-icon-file-image-fill', 'ad-icon-fileprotect', 'ad-icon-diff-fill', 'ad-icon-file-add', 'ad-icon-file-copy-fill', 'ad-icon-file-excel', 'ad-icon-snippets-fill', 'ad-icon-file-exclamation', 'ad-icon-batchfolding-fill', 'ad-icon-file-pdf', 'ad-icon-reconciliation-fill', 'ad-icon-file-image', 'ad-icon-folder-add-fill', 'ad-icon-file-markdown', 'ad-icon-folder-fill', 'ad-icon-file-unknown', 'ad-icon-folder-open-fill', 'ad-icon-file-ppt', 'ad-icon-database-fill', 'ad-icon-file-word', 'ad-icon-container-fill', 'ad-icon-file', 'ad-icon-sever-fill', 'ad-icon-file-zip', 'ad-icon-calendar-check-fill', 'ad-icon-file-text', 'ad-icon-image-fill', 'ad-icon-file-copy', 'ad-icon-idcard-fill', 'ad-icon-snippets', 'ad-icon-creditcard-fill', 'ad-icon-audit', 'ad-icon-fund-fill', 'ad-icon-diff', 'ad-icon-read-fill', 'ad-icon-Batchfolding', 'ad-icon-contacts-fill', 'ad-icon-securityscan', 'ad-icon-delete-fill', 'ad-icon-propertysafety', 'ad-icon-notification-fill', 'ad-icon-safetycertificate', 'ad-icon-flag-fill', 'ad-icon-insurance', 'ad-icon-moneycollect-fill', 'ad-icon-alert', 'ad-icon-medicinebox-fill', 'ad-icon-delete', 'ad-icon-rest-fill', 'ad-icon-hourglass', 'ad-icon-shopping-fill', 'ad-icon-bulb', 'ad-icon-skin-fill', 'ad-icon-experiment', 'ad-icon-video-fill', 'ad-icon-bell', 'ad-icon-sound-fill', 'ad-icon-trophy', 'ad-icon-bulb-fill', 'ad-icon-rest', 'ad-icon-bell-fill', 'ad-icon-USB', 'ad-icon-filter-fill', 'ad-icon-skin', 'ad-icon-fire-fill', 'ad-icon-home', 'ad-icon-funnelplot-fill', 'ad-icon-bank', 'ad-icon-gift-fill', 'ad-icon-filter', 'ad-icon-hourglass-fill', 'ad-icon-funnelplot', 'ad-icon-home-fill', 'ad-icon-like', 'ad-icon-trophy-fill', 'ad-icon-unlike', 'ad-icon-location-fill', 'ad-icon-unlock', 'ad-icon-cloud-fill', 'ad-icon-lock', 'ad-icon-customerservice-fill', 'ad-icon-customerservice', 'ad-icon-experiment-fill', 'ad-icon-flag', 'ad-icon-eye-fill', 'ad-icon-moneycollect', 'ad-icon-like-fill', 'ad-icon-medicinebox', 'ad-icon-lock-fill', 'ad-icon-shop', 'ad-icon-unlike-fill', 'ad-icon-rocket', 'ad-icon-star-fill', 'ad-icon-shopping', 'ad-icon-unlock-fill', 'ad-icon-folder', 'ad-icon-alert-fill', 'ad-icon-folder-open', 'ad-icon-api-fill', 'ad-icon-folder-add', 'ad-icon-highlight-fill', 'ad-icon-deploymentunit', 'ad-icon-phone-fill', 'ad-icon-accountbook', 'ad-icon-edit-fill', 'ad-icon-contacts', 'ad-icon-pushpin-fill', 'ad-icon-carryout', 'ad-icon-rocket-fill', 'ad-icon-calendar-check', 'ad-icon-thunderbolt-fill', 'ad-icon-calendar', 'ad-icon-tag-fill']
  elIcons.push(...adIcons)
  return elIcons
}

/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 */
export function treeDataTranslate (data, id = 'id', pid = 'pid') {
  var res = []
  var temp = {}
  for (var i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (var k = 0; k < data.length; k++) {
    if (!temp[data[k][pid]] || data[k][id] === data[k][pid]) {
      res.push(data[k])
      continue
    }
    if (!temp[data[k][pid]]['children']) {
      temp[data[k][pid]]['children'] = []
    }
    temp[data[k][pid]]['children'].push(data[k])
    data[k]['_level'] = (temp[data[k][pid]]._level || 0) + 1
  }
  return res
}

/**
 * 去除空children
 * 在如cascader等控件中，对于空数组会认为是有效节点
 * 需要设置为undefined
 */
export function removeEmptyChildren (data) {
  for (let itm of data) {
    if (!itm.children || itm.children.length < 1) {
      itm.children = undefined
    } else {
      removeEmptyChildren(itm.children)
    }
  }
  return data
}

/**
 * 是否json字符串
 *  * @param {*} str
 */
export function isJson (str) {
  if (typeof str === 'string') {
    try {
      const obj = JSON.parse(str)
      return typeof obj === 'object' && obj
    } catch (e) {
      return false
    }
  }
}

/**
 * 是否dom
 * @param obj
 * @returns {*}
 */
export function isDom (obj) {
  return typeof HTMLElement === 'object' ? (function () { return obj instanceof HTMLElement })() : (function () { return obj && typeof obj === 'object' && obj.nodeType === 1 && typeof obj.nodeName === 'string' })()
}
