<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="类型" prop="type">
                        <el-radio-group v-model="dataForm.type" size="small">
                            <el-radio-button :label="1">成人检测</el-radio-button>
                            <el-radio-button :label="2">孩子检测</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="排序" prop="sort">
                        <el-input-number controls-position="right" :min="0" v-model="dataForm.sort" placeholder="排序" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="题目" prop="question">
                <el-input v-model="dataForm.question" placeholder="题目"/>
            </el-form-item>
            <el-form-item label="选项" prop="options">
                <el-input v-model="dataForm.options" placeholder="选项"/>
            </el-form-item>
            <el-form-item label="答案" prop="answer">
                <el-input v-model="dataForm.answer" placeholder="答案"/>
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

export default {
  mixins: [mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/ba/subject/save`,
        dataFormUpdateURL: `/ba/subject/update`,
        dataFormInfoURL: `/ba/subject/info?id=`
      },
      dataForm: {
        id: '',
        question: '',
        sort: '',
        type: '',
        answer: '',
        options: '',
        explain: '',
        status: 1
      }
    }
  },
  computed: {
    dataRule () {
      return {
        question: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        sort: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        answer: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        options: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        explain: [
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
