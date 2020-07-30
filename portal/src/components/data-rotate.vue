<template>
    <div class="data-rotate">
        <el-row :span="24">
            <el-col :md="span" :xs="24" :sm="12" v-for="(item,index) in data" :key="index">
                <div class="item" :style="{backgroundColor:item.color}">
                    <div class="item-box">
                        <span class="item-count">{{item.count}}</span>
                        <span class="item-title">{{item.title}}</span>
                        <i class="item-icon" :class="item.icon"></i>
                    </div>
                    <a :href="item.href?item.href:'javascript:void(0);'" @click="item.click ? item.click(item) : ''">
                        <p class="item-more">更多<i class="el-icon-arrow-right"/></p>
                    </a>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
  name: 'data-rotate',
  props: {
    option: {
      type: Object,
      default: () => {
      }
    }
  },
  computed: {
    span () {
      return this.option.span || 8
    },
    data () {
      return this.option.data || []
    }
  }
}
</script>

<style>
    .data-rotate .item {
        margin: 5px 10px;
        -webkit-box-sizing: border-box;
        box-sizing: border-box;
        position: relative;
        color: #fff;
        border-radius: 3px;
        line-height: 2;
    }

    .data-rotate .item-box {
        padding: 10px 20px
    }

    .data-rotate .item-count {
        margin-bottom: 5px;
        font-size: 38px;
        white-space: nowrap;
        font-weight: 300;
        display: block
    }

    .data-rotate .item-title {
        font-size: 15px
    }

    .data-rotate .item-icon {
        -webkit-transition: all .3s linear;
        transition: all .3s linear;
        position: absolute;
        top: 20px;
        right: 20px;
        font-size: 65px;
        color: rgba(0, 0, 0, .15)
    }

    .data-rotate .item-icon:hover {
        font-size: 70px
    }

    .data-rotate .item-more {
        position: relative;
        text-align: center;
        padding: 3px 0;
        color: #fff;
        color: rgba(255, 255, 255, .8);
        display: block;
        z-index: 10;
        font-size: 14px;
        letter-spacing: 2px;
        background: rgba(0, 0, 0, .1);
        text-decoration: none
    }
</style>
