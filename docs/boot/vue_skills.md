# 键盘事件
为了在页面上支持键盘快捷键操作数据，可以增加事件监听

```javascript
mounted() {
    window.addEventListener('keyup', this.handleKeyEvent)
},
beforeDestroy() {
    // 在页面销毁的时候记得解除注册
    window.removeEventListener('keydown', this.handleKeyEvent) 
},
methods: {
    // 处理键盘事件
    async handleKeyEvent(event) {
        console.log(event)
        if (event.altKey) {
            // 已按下alt
            // keyCode 48-57是上排数字，96-105是数字键盘
            let keyIndex = -1
            if (event.keyCode >= 96 && event.keyCode <= 105) {
                keyIndex = event.keyCode - 96
            } if (event.keyCode >= 48 && event.keyCode <= 57) {
                keyIndex = event.keyCode - 48
            }
            if (keyIndex >= 0) {
                // 执行业务
                console.log(keyIndex)
            }
        }
    }
}
```
