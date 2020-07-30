<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="dataForm.name" placeholder="名称"/>
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
                    <el-form-item label="消费金额" prop="amount">
                        <el-input-number v-model="dataForm.amount" placeholder="输入消费金额" controls-position="right" :min="0" :max="10000000" :precision="2" :step="1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="优惠比例" prop="scale">
                        <el-input-number v-model="dataForm.scale" placeholder="输入优惠比例" controls-position="right" :min="0" :max="1" :precision="2" :step="0.1" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="默认项" prop="defaultItem">
                        <el-radio-group v-model="dataForm.defaultItem" size="small">
                            <el-radio-button :label="1">默认</el-radio-button>
                            <el-radio-button :label="0">非默认</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="dataForm.status" size="small">
                            <el-radio-button :label="1">正常</el-radio-button>
                            <el-radio-button :label="0">停用</el-radio-button>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
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
        dataFormSaveURL: `/shop/userRank/save`,
        dataFormUpdateURL: `/shop/userRank/update`,
        dataFormInfoURL: `/shop/userRank/info?id=`
      },
      dataForm: {
        id: '',
        name: '',
        amount: '',
        defaultItem: 0,
        special: '',
        scale: 1,
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
        ],
        amount: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        defaultItem: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        special: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        scale: [
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
