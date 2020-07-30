<template>
    <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="用户" prop="userId">
                        <el-input v-model="dataForm.userName" placeholder="用户" clearable readonly>
                            <user-pick class="small-button" slot="append" :userId="dataForm.userId" @onUserPicked="onUserPicked"/>
                        </el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="类型" prop="type">
                        <el-select v-model="dataForm.type" placeholder="类型" class="w-percent-100">
                            <el-option label="账户" value="balance"/>
                            <el-option label="收入" value="income"/>
                            <el-option label="积分" value="points"/>
                        </el-select>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="操作类型" prop="optType">
                        <el-input v-model="dataForm.optType" placeholder="操作类型"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="金额" prop="amount">
                        <el-input-number controls-position="right" :min="0" :max="99999" :precision="2" :step="1" v-model="dataForm.amount" placeholder="金额" class="w-percent-100"/>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="备注" prop="remark">
                <el-input v-model="dataForm.remark" placeholder="备注" type="textarea"/>
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
import UserPick from './user-pick'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  components: { UserPick },
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/uc/bill/save`,
        dataFormUpdateURL: `/uc/bill/update`,
        dataFormInfoURL: `/uc/bill/info?id=`
      },
      dataForm: {
        id: '',
        userId: '',
        userName: '',
        optType: '',
        type: '',
        remark: '',
        amount: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        userId: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        userName: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        optType: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        amount: [
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
