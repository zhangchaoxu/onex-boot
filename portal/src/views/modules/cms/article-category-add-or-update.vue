<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="dataForm.name" placeholder="名称" maxlength="100"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item :label="$t('base.sort')" prop="sort">
                        <el-input-number controls-position="right" :min="0" v-model="dataForm.sort" :placeholder="$t('base.remark')" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item prop="imgs" :label="$t('base.cover')">
                <file-upload ref="fileUpload" v-model="dataForm.imgs" :limit="1" mode="image"/>
            </el-form-item>
            <el-form-item :label="$t('base.remark')" prop="remark">
                <el-input v-model="dataForm.remark" :placeholder="$t('base.remark')" type="textarea" maxlength="500"/>
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
import FileUpload from '@/components/file-upload'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  components: { FileUpload },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/cms/articleCategory/save`,
        dataFormUpdateURL: `/cms/articleCategory/update`,
        dataFormInfoURL: `/cms/articleCategory/info?id=`
      },
      dataForm: {
        id: '',
        name: '',
        sort: '',
        remark: '',
        imgs: '',
        status: 1
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sort: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    init () {
      this.visible = true
      this.formLoading = true
      this.$nextTick(() => {
        this.resetForm()
        this.initFormData()
      })
    }
  }
}
</script>
