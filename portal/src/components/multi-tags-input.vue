<template>
    <div>
        <el-tag class="multi-tags" :key="item" v-for="item in tags" closable :disable-transitions="false" @close="closeTagHandle(item)">{{ item }}</el-tag>
        <el-input v-if="tagInputVisible" class="input-new-tag"  v-model="tagInputValue" ref="tagInput" size="small" @keyup.enter.native="saveTagInputHandle" @blur="saveTagInputHandle"/>
        <el-button v-else-if="tags.length < max" class="button-new-tag" size="small" @click="showTagInput">+ 添加</el-button>
    </div>
</template>

<script>
export default {
  name: 'multi-tags-input',
  props: {
    // 绑定的v-model,必须用value
    value: String,
    content: String,
    // 分隔符
    split: {
      type: String,
      default: ','
    },
    // 最大个数
    max: {
      type: Number,
      default: 9
    }
  },
  data () {
    return {
      // eslint-disable-next-line vue/no-reserved-keys
      _content: '',
      // 标签列表
      tags: [],
      tagInputVisible: false,
      tagInputValue: ''
    }
  },
  watch: {
    // Watch content change
    content (newVal, oldVal) {
      if (newVal && newVal !== this._content) {
        this._content = newVal
        this.tags = this._content.split(this.split).filter(item => item !== '')
      } else if (!newVal) {
        this.tags = []
      }
    },
    // Watch content change
    value (newVal, oldVal) {
      if (newVal && newVal !== this._content) {
        this._content = newVal
        this.tags = this._content.split(this.split).filter(item => item !== '')
      } else if (!newVal) {
        this.tags = []
      }
    }
  },
  methods: {
    // 重置数据
    resetTags () {
      this.tags = []
      this.tagInputVisible = false
      this.tagInputValue = ''
    },
    showTagInput () {
      this.tagInputVisible = true
      this.$nextTick(() => {
        this.$refs.tagInput.$refs.input.focus()
      })
    },
    closeTagHandle (item) {
      this.tags.splice(this.tags.indexOf(item), 1)
      this._content = this.tags.join(this.split)
      this.$emit('input', this._content)
    },
    saveTagInputHandle () {
      let inputValue = this.tagInputValue
      if (inputValue) {
        inputValue = inputValue.trim()
        if (!inputValue) {
          this.$message.error('不允许为空')
        } else if (inputValue.indexOf(this.split) > -1) {
          this.$message.error('不允许出现' + this.split)
        } else if (this.tags.indexOf(inputValue) > -1) {
          this.$message.error('不允许重复添加')
        } else {
          this.tags.push(inputValue)
          this._content = this.tags.join(this.split)
          this.$emit('input', this._content)
        }
      }
      this.tagInputVisible = false
      this.tagInputValue = ''
    }
  }
}

</script>

<style scoped>

    /**
    带添加按钮的tag添加
     */
    .multi-tags + .el-tag {
        margin-left: 10px;
    }

    .button-new-tag {
        margin-left: 10px;
        height: 32px;
        line-height: 30px;
        padding-top: 0;
        padding-bottom: 0;
    }

    .input-new-tag {
        width: 90px;
        margin-left: 10px;
        vertical-align: bottom;
    }

</style>
