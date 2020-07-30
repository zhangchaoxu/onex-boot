<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="产品类别" prop="categoryId">
                        <el-select v-model="dataForm.categoryId" placeholder="选择产品类别" class="w-percent-100">
                            <el-option v-for="item in productCategoryList" :key="item.id" :label="item.name" :value="item.id"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="在架" prop="marketable">
                        <el-radio-group v-model="dataForm.marketable" size="small">
                            <el-radio-button :label="1">上架</el-radio-button>
                            <el-radio-button :label="0">下架</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="dataForm.name" placeholder="产品名称"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="编码" prop="sn">
                        <el-input v-model="dataForm.sn" placeholder="产品编码"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="单位" prop="unit">
                        <el-select v-model="dataForm.unit" filterable allow-create default-first-option placeholder="请选择单位" class="w-percent-100">
                            <el-option label="个" value="个"/>
                            <el-option label="件" value="件"/>
                            <el-option label="套" value="套"/>
                            <el-option label="只" value="只"/>
                            <el-option label="盒" value="盒"/>
                            <el-option label="箱" value="箱"/>
                            <el-option label="千克" value="千克"/>
                            <el-option label="米" value="米"/>
                            <el-option label="台" value="台"/>
                            <el-option label="吨" value="吨"/>
                            <el-option label="瓶" value="瓶"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="标准价" prop="salePrice">
                        <el-input-number v-model="dataForm.salePrice" placeholder="输入标准价" controls-position="right" :min="0" :max="99999" :precision="2" :step="1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="描述" prop="content">
                <quill-editor ref="editorContent" containerHeight="150px" v-model="dataForm.content"/>
            </el-form-item>
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
import QuillEditor from '@/components/quill-editor'
import FileUpload from '@/components/file-upload'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  components: { QuillEditor, FileUpload },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/crm/product/save`,
        dataFormUpdateURL: `/crm/product/update`,
        dataFormInfoURL: `/crm/product/info?id=`
      },
      // 产品类别
      productCategoryList: [],
      // 是否上架
      marketableOptions: [{
        value: 0,
        label: '否'
      }, {
        value: 1,
        label: '是'
      }],
      // 是否上架
      unitOptions: [
        {
          value: '个',
          label: '个'
        }, {
          value: '件',
          label: '件'
        }, {
          value: '套',
          label: '套'
        }
      ],
      dataForm: {
        id: '',
        name: '',
        sn: '',
        unit: '',
        salePrice: '',
        content: '',
        categoryId: '',
        marketable: 1
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sn: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        salePrice: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        marketable: [
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
        this.resetForm()
        Promise.all([
          this.getProductCategoryList()
        ]).then(() => {
          this.initFormData()
        })
      })
    },
    // 获取产品分类列表
    getProductCategoryList () {
      this.$http.get('/crm/productCategory/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.productCategoryList = res.data
        this.formLoading = false
      }).catch(resp => {
        this.formLoading = false
        this.$message.error(this.$t('prompt.apierror') + resp)
      })
    }
  }
}
</script>
