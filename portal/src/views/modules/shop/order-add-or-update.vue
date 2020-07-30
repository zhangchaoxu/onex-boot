<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-divider>收件信息</el-divider>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="会员" prop="userId">
                        <el-input v-model="dataForm.userName" placeholder="用户" clearable readonly>
                            <user-pick class="small-button" slot="append" :userId="dataForm.userId" @onUserPicked="onUserPicked"/>
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="收货地址" prop="receiverId">
                        <el-select v-model="dataForm.receiverId" filterable placeholder="请选择收货地址" class="w-percent-100" @change="onReceiverChangeHandle">
                            <el-option v-for="item in receiverList" :key="item.id" :label="item.address" :value="item.id">
                                <span style="float: left">{{ item.address }}</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="收件人" prop="receiverConsignee">
                        <el-input v-model="dataForm.receiverConsignee" placeholder="收件人"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="联系电话" prop="receiverMobile">
                        <el-input v-model="dataForm.receiverMobile" placeholder="收件人手机号"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="地址" prop="receiverAddress">
                <el-input v-model="dataForm.receiverAddress" placeholder="详细地址">
                    <template slot="prepend">{{ dataForm.receiverRegionName }}</template>
                    <!-- 位置选择器 -->
                    <amap-loc-pick slot="append" ref="ampLocPick" :poi="{ regionName: dataForm.receiverRegionName, regionCode: dataForm.receiverRegionCode, address: dataForm.receiverAddress, lat:
                    dataForm.receiverLat, lng: dataForm.receiverLng }" @onLocPicked="onLocPicked"/>
                </el-input>
            </el-form-item>
            <el-divider>商品</el-divider>
            <el-row v-for="(item, index) in dataForm.items" :key="index" :prop="'item.' + index + '.value'">
                <el-col :span="12">
                    <el-form-item label="商品">
                        <el-input v-model="item.goodsName" placeholder="选择商品" readonly>
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="数量" prop="num" label-width="50px">
                        <el-input-number v-model="item.qty" placeholder="输入数量" :disabled="!item.goodsId" controls-position="right" :min="1" :max="item.stock" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
                <el-col :span="4">
                    <el-button @click.prevent="addItem(index + 1)" style="margin-left: 10px;" type="text">添加</el-button>
                    <el-button @click.prevent="removeItem(item)" style="margin-left: 10px;color:#f56c6c;" type="text" v-if="index !== 0">删除</el-button>
                </el-col>
            </el-row>
            <el-divider>合计费用{{ dataForm.price }}</el-divider>
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinFormModule from '@/mixins/form-module'
import AmapLocPick from '@/components/amap-loc-pick'
import UserPick from '../uc/user-pick'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  components: { UserPick, AmapLocPick },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/shop/order/place`,
        dataFormUpdateURL: `/shop/order/update`,
        dataFormInfoURL: `/shop/order/info?id=`
      },
      // 收件地址
      receiverList: [],
      dataForm: {
        userId: '',
        userName: '',
        receiverId: '',
        receiverConsignee: '',
        receiverMobile: '',
        receiverAddress: '',
        receiverRegionCode: '',
        receiverRegionName: '',
        price: 0,
        items: [{
          goodsName: '',
          goodsId: '',
          price: 0,
          stock: 0,
          qty: 0
        }]
      }
    }
  },
  computed: {
    dataRule () {
      return {
        userId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        receiverConsignee: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        receiverMobile: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        receiverAddress: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.formLoading = true
      this.visible = true
      this.$nextTick(() => {
        this.receiverList = []
        this.resetForm()
        this.initFormData()
      })
    },
    // 选中用户
    onUserPicked (result) {
      if (result && result.length > 0) {
        this.dataForm.userId = result[0].id
        this.dataForm.userName = result[0].username
        this.getReceiverList('')
      }
    },
    // 接受位置选择返回结果
    onLocPicked (result) {
      if (result) {
        this.dataForm.receiverRegionCode = result.regionCode
        this.dataForm.receiverRegionName = result.regionName
        this.dataForm.receiverAddress = result.address
        this.dataForm.receiverLat = result.lat
        this.dataForm.receiverLng = result.lng
      }
    },
    // 插入item
    addItem (index) {

    },
    // 删除站点
    removeItem (item) {
      const index = this.dataForm.items.indexOf(item)
      if (index !== -1) {
        this.dataForm.items.splice(index, 1)
      }
    },
    // 根据用户选择，获取对应地址
    getReceiverList (search) {
      // 将原来二级菜单中的默认值清空
      return this.$http.get(`/shop/receiver/list?limit=20&search=` + search + `&userId=` + this.dataForm.userId).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.receiverList = res.data
      }).catch(() => {
      })
    },
    // 地址选中
    onReceiverChangeHandle (id) {
      const item = this.receiverList.filter(item => item.id === id)[0]
      if (item) {
        this.dataForm.receiverConsignee = item.consignee
        this.dataForm.receiverMobile = item.mobile
        this.dataForm.receiverAddress = item.address
        this.dataForm.receiverRegionName = item.regionName
        this.dataForm.receiverRegionCode = item.regionCode
      }
    }
  }
}
</script>
