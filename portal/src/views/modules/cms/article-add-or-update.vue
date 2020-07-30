<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" :label-width="$i18n.locale === 'en-US' ? '120px' : '80px'">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="类目" prop="articleCategoryId">
                        <el-select v-model="dataForm.articleCategoryId" placeholder="选择文章类目" class="w-percent-100">
                            <el-option v-for="item in articleCategoryList" :key="item.id" :label="item.name"
                                       :value="item.id"/>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="标题" prop="name">
                        <el-input v-model="dataForm.name" placeholder="标题" maxlength="100" show-word-limit/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="8">
                    <el-form-item label="作者" prop="author">
                        <el-input v-model="dataForm.author" placeholder="作者" maxlength="100" show-word-limit/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="排序" prop="sort">
                        <el-input-number controls-position="right" :min="0" v-model="dataForm.sort" placeholder="排序"
                                         class="w-percent-100"/>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="首页推荐" prop="top">
                        <el-select v-model="dataForm.top" placeholder="选择是否首页推荐" class="w-percent-100">
                            <el-option
                                    v-for="item in topOptions"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="来源" prop="source">
                        <el-input v-model="dataForm.source" placeholder="来源" maxlength="200" show-word-limit/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="来源链接" prop="sourceLink">
                        <el-input v-model="dataForm.sourceLink" placeholder="来源链接" maxlength="500" show-word-limit/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="是否发布" prop="status">
                        <el-select v-model="dataForm.status" placeholder="选择是否发布" class="w-percent-100">
                            <el-option
                                    v-for="item in statusOptions"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item prop="pubDate" label="发布时间">
                        <el-date-picker v-model="dataForm.pubDate" type="datetime" value-format="yyyy-MM-dd HH:mm:ss"
                                        placeholder="选择发布时间" style="width: 100%"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item prop="imgs" :label="$t('base.cover')">
                <file-upload ref="fileUpload" v-model="dataForm.imgs" :limit="1" mode="image"/>
            </el-form-item>
            <el-form-item prop="content" label="文章内容">
                <el-form-item prop="content">
                    <quill-editor ref="editorContent" containerHeight="200px" v-model="dataForm.content"/>
                </el-form-item>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="dataForm.remark" placeholder="备注" type="textarea" maxlength="300"/>
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
        dataFormSaveURL: `/cms/article/save`,
        dataFormUpdateURL: `/cms/article/update`,
        dataFormInfoURL: `/cms/article/info?id=`
      },
      // 文章分类列表
      articleCategoryList: [],
      statusOptions: [{
        value: 0,
        label: '未发布'
      }, {
        value: 1,
        label: '发布'
      }],
      topOptions: [{
        value: 0,
        label: '否'
      }, {
        value: 1,
        label: '是'
      }],
      dataForm: {
        articleCategoryId: '',
        id: '',
        sort: '',
        status: '',
        type: '',
        remark: '',
        name: '',
        author: '',
        content: '',
        lat: '',
        lng: '',
        address: '',
        pubDate: '',
        validFromDate: '',
        validToDate: '',
        source: '',
        top: '',
        sourceLink: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        sort: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        author: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        content: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        articleCategoryId: [
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
          this.getArticleCategoryList()
        ]).then(() => {
          this.initFormData()
        })
      })
    },
    // 获取文章分类列表
    getArticleCategoryList () {
      this.$http.get('/cms/articleCategory/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.articleCategoryList = res.data
        this.formLoading = false
      }).catch(resp => {
        this.formLoading = false
        this.$message.error(this.$t('prompt.apierror') + resp)
      })
    }
  }
}
</script>
