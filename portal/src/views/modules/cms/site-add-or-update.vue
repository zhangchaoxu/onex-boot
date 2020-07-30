<template>
    <el-drawer :visible.sync="visible" :title="title" size="50%" :wrapperClosable="false" :close-on-press-escape="false" custom-class="drawer">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-form-item label="编码" prop="code">
                <el-input v-model="dataForm.code" placeholder="编码" :disabled="dataFormMode === 'view'"></el-input>
            </el-form-item>
            <el-form-item label="名称" prop="name">
                <el-input v-model="dataForm.name" placeholder="名称"></el-input>
            </el-form-item>
            <el-form-item label="标题" prop="title">
                <el-input v-model="dataForm.title" placeholder="标题"></el-input>
            </el-form-item>
            <el-form-item label="描述" prop="description">
                <el-input v-model="dataForm.descr" placeholder="描述"></el-input>
            </el-form-item>
            <el-form-item label="网址" prop="domain">
                <el-input v-model="dataForm.domain" placeholder="网址"></el-input>
            </el-form-item>
            <el-form-item label="LOGO" prop="logo">
                <el-input v-model="dataForm.logo" placeholder="LOGO"></el-input>
            </el-form-item>
            <el-form-item label="版权信息" prop="copyright">
                <el-input v-model="dataForm.copyright" placeholder="版权信息"></el-input>
            </el-form-item>
            <el-form-item label="关键词" prop="keywords">
                <multi-tags-input ref="multiTagsInput" v-model="dataForm.keywords"/>
            </el-form-item>
            <el-form-item label="图片" prop="imgs">
                <el-input v-model="dataForm.imgs" placeholder="图片"></el-input>
            </el-form-item>
            <el-form-item label="状态" prop="status">
                <el-select v-model="dataForm.status" placeholder="选择状态" class="w-percent-100">
                    <el-option
                            v-for="item in statusOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>
        </el-form>
        <div class="drawer__footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
        </div>
    </el-drawer>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import MultiTagsInput from '@/components/multi-tags-input'

export default {
  mixins: [mixinFormModule],
  components: { MultiTagsInput },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/cms/site/save`,
        dataFormUpdateURL: `/cms/site/update`,
        dataFormInfoURL: `/cms/site/info?id=`
      },
      statusOptions: [{
        value: 0,
        label: '下线'
      }, {
        value: 1,
        label: '上线'
      }],
      dataForm: {
        code: '',
        name: '',
        title: '',
        descr: '',
        domain: '',
        logo: '',
        copyright: '',
        keywords: '',
        imgs: '',
        status: 1
      }
    }
  },
  computed: {
    dataRule () {
      return {
        code: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        title: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        status: [
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
        this.initFormData()
      })
    }
  }
}
</script>
