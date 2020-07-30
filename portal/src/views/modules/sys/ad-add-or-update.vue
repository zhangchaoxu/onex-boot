<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="位置" prop="position">
                        <el-input v-model="dataForm.position" placeholder="位置" maxlength="100"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="标题" prop="name">
                        <el-input v-model="dataForm.name" placeholder="标题" maxlength="100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item prop="imgs" label="图片">
                <file-upload ref="fileUpload" v-model="dataForm.imgs" :limit="1" mode="image"/>
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="登录" prop="needLogin">
                        <el-radio-group v-model="dataForm.needLogin" size="small">
                            <el-radio-button :label="1">需要</el-radio-button>
                            <el-radio-button :label="0">不需要</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="排序" prop="sort">
                        <el-input-number controls-position="right" :min="0" v-model="dataForm.sort" placeholder="排序" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="链接" prop="link">
                <el-input v-model="dataForm.link" placeholder="链接"/>
            </el-form-item>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="dataForm.remark" placeholder="备注" type="textarea"></el-input>
            </el-form-item>
        </el-form>
        <template slot="footer">
            <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
            <el-button type="primary" @click="dataFormSubmitHandle()">{{ $t('confirm') }}</el-button>
        </template>
    </el-dialog>
</template>

<script>
import mixinFormModule from '@/mixins/form-module'
import FileUpload from '@/components/file-upload'

export default {
  mixins: [mixinFormModule],
  components: { FileUpload },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/sys/axd/save`,
        dataFormUpdateURL: `/sys/axd/update`,
        dataFormInfoURL: `/sys/axd/info?id=`
      },
      dataForm: {
        id: '',
        name: '',
        position: '',
        link: '',
        remark: '',
        imgs: '',
        sort: 0,
        needLogin: 0
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        position: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sort: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        needLogin: [
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
