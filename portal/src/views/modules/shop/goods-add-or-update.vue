<template>
    <el-card>
        <el-tabs tab-position="left" v-model="step" @tab-click="tab => this.onStep(tab.name)" :before-leave="() => !!this.dataForm.id">
            <el-tab-pane name="1" label="基本信息">
                <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
                    <el-form-item label="类型" prop="type">
                        <el-radio-group v-model="dataForm.type" disabled>
                            <el-radio :label="1">商品</el-radio>
                            <el-radio :label="2">积分兑换</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="供应商" prop="supplierId">
                                <el-autocomplete
                                        class="w-percent-100"
                                        value-key="name"
                                        v-model="dataForm.supplierName"
                                        :fetch-suggestions="getSupplierList"
                                        placeholder="请选择供应商"
                                        @select="item => dataForm.supplierId = item.id"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="品牌" prop="brandId">
                                <el-select v-model="dataForm.brandId" filterable placeholder="请选择品牌" class="w-percent-100">
                                    <el-option v-for="item in brandList" :key="item.id" :label="item.name" :value="item.id"/>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="分类" prop="categoryId">
                                <el-cascader v-model="categorySelected" :options="goodsCategoryList" clearable
                                             :props="{ emitPath: false, checkStrictly: false, value: 'id', label: 'name'}"
                                             @change="(value) => this.dataForm.categoryId = value" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item prop="sort" :label="$t('base.sort')">
                                <el-input-number v-model="dataForm.sort" controls-position="right" :min="0" :max="9999" :label="$t('base.sort')" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="名称" prop="name">
                                <el-input v-model="dataForm.name" placeholder="输入名称"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="编号" prop="sn">
                                <el-input v-model="dataForm.sn" placeholder="编号"/>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item label="标题" prop="title">
                        <el-input v-model="dataForm.title" placeholder="标题"/>
                    </el-form-item>
                    <el-form-item label="规格类型" prop="specType">
                        <el-radio-group v-model="dataForm.specType" size="small" disabled>
                            <el-radio-button :label="0">单规格</el-radio-button>
                            <el-radio-button :label="1">多规格</el-radio-button>
                        </el-radio-group>
                        <br/>
                        <span style="color: red">注意:规格类型和规格内容保存后无法修改,规格留空表示不启用</span>
                    </el-form-item>
                    <el-row v-if="dataForm.specType === 0">
                        <el-col :span="12">
                            <el-form-item label="市场价" prop="marketPrice">
                                <el-input-number v-model="dataForm.marketPrice" placeholder="输入市场价" controls-position="right" :min="0" :max="99999" :precision="2" :step="1" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="销售价" prop="salePrice">
                                <el-input-number v-model="dataForm.salePrice" placeholder="输入销售价" controls-position="right" :min="0" :max="99999" :precision="2" :step="1" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="限购类型" prop="limitType">
                                <el-select v-model="dataForm.limitType" filterable placeholder="请选择限购类型" class="w-percent-100">
                                    <el-option label="不限购" :value="0"/>
                                    <el-option label="永久限购" :value="1"/>
                                    <el-option label="按日限购" :value="2"/>
                                    <el-option label="按周限购" :value="3"/>
                                    <el-option label="按月限购" :value="4"/>
                                    <el-option label="按年限购" :value="5"/>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12" v-if="dataForm.limitType > 0">
                            <el-form-item label="限购数量" prop="limitCount">
                                <el-input-number v-model="dataForm.limitCount" placeholder="输入限购数量" controls-position="right" :min="0" :max="99999" :step="1" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="6">
                            <el-form-item label="在架" prop="marketable">
                                <el-radio-group v-model="dataForm.marketable" size="small">
                                    <el-radio-button :label="1">上架</el-radio-button>
                                    <el-radio-button :label="0">下架</el-radio-button>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="购物车" prop="cartable">
                                <el-radio-group v-model="dataForm.cartable" size="small">
                                    <el-radio-button :label="1">支持</el-radio-button>
                                    <el-radio-button :label="0">不支持</el-radio-button>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="物流" prop="delivery">
                                <el-radio-group v-model="dataForm.delivery" size="small">
                                    <el-radio-button :label="1">需要</el-radio-button>
                                    <el-radio-button :label="0">不需要</el-radio-button>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item label="置顶" prop="top">
                                <el-radio-group v-model="dataForm.top" size="small">
                                    <el-radio-button :label="1">置顶</el-radio-button>
                                    <el-radio-button :label="0">不置顶</el-radio-button>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-form-item label="标签" prop="tags">
                        <multi-tags-input ref="multiTagsInput" v-model="dataForm.tags" :max="3"/>
                    </el-form-item>
                </el-form>
                <div style="text-align: center;">
                    <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('save') }}</el-button>
                </div>
            </el-tab-pane>
            <el-tab-pane name="2" label="商品图片" v-if="!!dataForm.id">
                <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm">
                    <el-form-item prop="imgs">
                        <file-upload ref="fileUpload" v-model="dataForm.imgs" :limit="4" mode="image"/>
                    </el-form-item>
                </el-form>
                <div style="text-align: center;">
                    <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('save') }}</el-button>
                </div>
            </el-tab-pane>
            <el-tab-pane name="3" label="详情介绍" v-if="!!dataForm.id">
                <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm">
                    <el-form-item prop="content">
                        <quill-editor ref="editorContent" containerHeight="350px" v-model="dataForm.content"/>
                    </el-form-item>
                </el-form>
                <div style="text-align: center;">
                    <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('save') }}</el-button>
                </div>
            </el-tab-pane>
            <el-tab-pane name="4" label="参数管理" v-if="!!dataForm.id">
                <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="70px">
                    <el-row v-for="(attrGroup, index) in dataForm.attrGroups" :key="index" :prop="'attrGroup.' + index + '.value'">
                        <el-col :span="6">
                            <el-form-item :label="'参数组' + (index + 1)">
                                <el-input v-model="attrGroup.name" placeholder="参数组名称" maxlength="50"/>
                                <el-button @click.prevent="addAttrGroup(index + 1)" style="margin-left: 10px;" type="text">添加参数组</el-button>
                                <el-button @click.prevent="removeAttrGroup(attrGroup)" style="margin-left: 10px;color:#f56c6c;" type="text">删除参数组</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="18">
                            <el-row v-for="(attr, attrIndex) in attrGroup.items" :key="attrIndex" :prop="'attr.' + attrIndex + '.value'">
                                <el-col :span="9">
                                    <el-form-item :label="'参数' + (attrIndex + 1)">
                                        <el-input v-model="attr.name" placeholder="参数名" maxlength="50"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="9">
                                    <el-form-item label="参数值">
                                        <el-input v-model="attr.value" placeholder="参数值" maxlength="50"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="6">
                                    <el-button @click.prevent="addAttr(attrGroup, attrIndex + 1)" style="margin-left: 10px;" type="text">添加参数</el-button>
                                    <el-button @click.prevent="removeAttr(attrGroup, attr)" style="margin-left: 10px;color:#f56c6c;" type="text" v-if="attrGroup.items.length > 1">删除参数</el-button>
                                </el-col>
                            </el-row>
                        </el-col>
                    </el-row>
                </el-form>
                <div style="text-align: center;">
                    <el-button type="success" @click="addAttrGroup(dataForm.attrGroups.length + 1)">添加参数组</el-button>
                    <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('save') }}</el-button>
                </div>
            </el-tab-pane>
            <el-tab-pane name="5" label="规格管理" v-if="!!dataForm.id && dataForm.specType === 1">暂不支持多规格</el-tab-pane>
            <el-tab-pane name="6" label="分销管理" v-if="!!dataForm.id">
                <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="分销类型" prop="distType">
                                <el-select v-model="dataForm.distType" filterable placeholder="请选择分销类型" class="w-percent-100">
                                    <el-option label="不参与" :value="0"/>
                                    <el-option label="按比例" :value="1"/>
                                    <el-option label="固定金额" :value="2"/>
                                    <el-option label="随机金额" :value="3"/>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12" v-if="dataForm.distType === 1">
                            <el-form-item label="提成比例" prop="distScale">
                                <el-input-number v-model="dataForm.distScale" placeholder="输入提成比例" controls-position="right" :min="0" :max="1" :step="0.1" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12" v-if="dataForm.distType === 2">
                            <el-form-item label="提成金额" prop="distVal">
                                <el-input-number v-model="dataForm.distVal" placeholder="输入提成金额" controls-position="right" :min="0" :max="99999" :step="1" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6" v-if="dataForm.distType === 3">
                            <el-form-item label="提成最小金额" prop="distMinVal">
                                <el-input-number v-model="dataForm.distMinVal" placeholder="输入提成最小金额" controls-position="right" :min="0" :max="99999" :step="1" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6" v-if="dataForm.distType === 3">
                            <el-form-item label="提成最大金额" prop="distMaxVal">
                                <el-input-number v-model="dataForm.distMaxVal" placeholder="输入提成最大金额" controls-position="right" :min="0" :max="99999" :step="1" class="w-percent-100"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12" v-if="dataForm.distType !== 0">
                            <el-form-item label="活动时间" prop="activityStartDate">
                                <el-date-picker
                                        v-model="dateRange"
                                        type="datetimerange"
                                        @change="dateRangeChangeHandle"
                                        :picker-options="dateRangePickerOptions"
                                        value-format="yyyy-MM-dd HH:mm:ss"
                                        :range-separator="$t('datePicker.range')"
                                        :start-placeholder="$t('datePicker.start')"
                                        :end-placeholder="$t('datePicker.end')">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
                <div style="text-align: center;">
                    <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('save') }}</el-button>
                </div>
            </el-tab-pane>
        </el-tabs>
    </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinFormModule from '@/mixins/form-module'
import { removeEmptyChildren } from '@/utils'
import QuillEditor from '@/components/quill-editor'
import FileUpload from '@/components/file-upload'
import MultiTagsInput from '@/components/multi-tags-input'

export default {
  inject: ['refresh'],
  mixins: [mixinBaseModule, mixinFormModule],
  components: { FileUpload, QuillEditor, MultiTagsInput },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/shop/goods/save`,
        dataFormUpdateURL: `/shop/goods/update`,
        dataFormInfoURL: `/shop/goods/info?id=`
      },
      // 当前激活tab
      step: '1',
      // 品牌列表
      brandList: [],
      // 分类列表
      goodsCategoryList: [],
      // 已选中分类
      categorySelected: [],
      dataForm: {
        id: '',
        brandId: '',
        limitType: 0,
        distMaxVal: 0,
        distMinVal: 0,
        distVal: 0,
        distScale: 0,
        distType: 0,
        limitCount: 0,
        memberDiscount: 0,
        categoryId: '',
        supplierId: '',
        supplierName: '',
        sort: '',
        sn: '',
        delivery: 1,
        marketable: 1,
        top: 1,
        type: 1,
        name: '',
        title: '',
        tags: '',
        marketPrice: 0,
        salePrice: 0,
        attrs: '',
        attrGroups: [],
        specs: '',
        specType: 0,
        cartable: 1,
        status: '',
        hits: '',
        imgs: '',
        content: '',
        score: '',
        validStartTime: '',
        validEndTime: ''
      },
      dateRange: ''
    }
  },
  activated () {
    let queryId = this.$route.query.id
    let queryStep = this.$route.query.step || '1'
    if (this.dataForm.id !== queryId || this.step !== queryStep) {
      // 参数发生了变化
      if (!queryId && queryStep > 1) {
        this.$message.error(this.$t('addneedstep'))
        return
      } else {
        this.dataForm.id = queryId
        this.step = queryStep
      }
      // 根据id刷新tab名称
      let tab = this.$store.state.contentTabs.filter(item => item.name === this.$route.name)[0]
      if (tab) {
        tab.title = queryId ? '编辑商品' : '新增商品'
      }
      // 根据step刷新数据
      this.init()
    }
  },
  computed: {
    dataRule () {
      return {
        limitType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        specType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        brandId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        supplierId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sort: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sn: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        delivery: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        marketable: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        top: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        title: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        marketPrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        salePrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        attrs: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        specs: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        hits: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        imgs: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        score: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        content: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        distType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        cartable: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        validStartTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        validEndTime: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        distScale: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        distMinVal: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        distVal: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        distMaxVal: [
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
        if (this.step === '1') {
          // 详情
          this.resetForm()
          Promise.all([
            this.getBrandList(''),
            this.getGoodsCategoryList('')
          ]).then(() => {
            this.initFormData()
          })
        } else if (this.step === '2') {
          // 图片
          this.initFormData()
        } else if (this.step === '3') {
          // 详情
          this.initFormData()
        } else if (this.step === '4') {
          // 参数
          this.initFormData()
        } else if (this.step === '6') {
          this.dateRange = ''
          // 分销管理
          this.initFormData()
        }
      })
    },
    // 跳转步骤
    onStep (result) {
      if (!this.dataForm.id) {
        this.$message.error('基本信息保存后才能切换')
        return false
      }
      if (result === 100) {
        // 下一步
        this.step = String(Number.parseInt(this.step) + 1)
      } else if (result === -100) {
        // 上一步
        this.step = String(Number.parseInt(this.step) - 1)
      } else {
        this.step = String(result)
      }
      this.$router.replace({ name: this.$route.name, query: { id: this.dataForm.id, step: this.step } })
      // 跳转后刷新一下，激活activated
      this.refresh()
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      if (this.step === '1') {
        // 基本信息
      } else if (this.step === '2') {
        // 图片
      } else if (this.step === '3') {
        // 详情
      } else if (this.step === '4') {
        // 参数管理
        if (this.dataForm.attrs) {
          this.dataForm.attrGroups = JSON.parse(this.dataForm.attrs)
        } else {
          this.dataForm.attrGroups = []
        }
      } else if (this.step === '6') {
        // 分销
        if (this.dataForm.validStartTime !== null) {
          this.dateRange = [this.dataForm.validStartTime, this.dataForm.validEndTime]
        }
      }
    },
    // 表单提交之前的操作
    beforeDateFormSubmit () {
      if (this.step === '1') {
        // 基本信息
      } else if (this.step === '2') {
        // 图片
      } else if (this.step === '3') {
        // 图文详情
      } else if (this.step === '4') {
        // 参数管理
        this.dataForm.attrs = JSON.stringify(this.dataForm.attrGroups)
      } else if (this.step === '6') {
        // 分销
      }
      this.dataFormSubmitParam = this.dataForm
      return true
    },
    // 表单提交成功
    onFormSubmitSuccess (res) {
      // 跳到下一步
      this.dataForm.id = res.data.id
      this.onStep(100)
      // 弹出提示框
      this.$message({
        message: '保存成功',
        type: 'success',
        duration: 500,
        onClose: () => {
        }
      })
    },
    // 插入参数组
    addAttrGroup (index) {
      // 插入指定位置
      this.dataForm.attrGroups.splice(index, 0, { 'name': '', 'items': [ { 'name': '', 'value': '' } ] })
    },
    // 删除参数组
    removeAttrGroup (item) {
      const index = this.dataForm.attrGroups.indexOf(item)
      if (index !== -1) {
        this.dataForm.attrGroups.splice(index, 1)
      }
    },
    // 插入参数
    addAttr (attrGroup, index) {
      // 插入指定位置
      attrGroup.items.splice(index, 0, { 'name': '', 'value': '' })
    },
    // 删除参数
    removeAttr (attrGroup, item) {
      const index = attrGroup.items.indexOf(item)
      if (index !== -1) {
        attrGroup.items.splice(index, 1)
      }
    },
    // 品牌列表
    getBrandList (name) {
      return this.$http.get(`/shop/brand/list?name=` + name).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.brandList = res.data
      }).catch(() => {
      })
    },
    // 供应商列表
    getSupplierList (name, callback) {
      return this.$http.get(`/shop/supplier/list?name=` + name).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        callback(res.data)
      }).catch(() => {
      })
    },
    // 分类列表
    getGoodsCategoryList (name) {
      return this.$http.get(`/shop/goodsCategory/tree?filterEmptyChild=true&name=` + name).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.goodsCategoryList = removeEmptyChildren(res.data)
      }).catch(() => {
      })
    },
    // 时间区间选择器变化
    dateRangeChangeHandle (value) {
      if (value && value.length === 2) {
        this.dataForm.validStartTime = value[0]
        this.dataForm.validEndTime = value[1]
      } else {
        this.dataForm.validStartTime = ''
        this.dataForm.validEndTime = ''
      }
    }
  }
}
</script>
