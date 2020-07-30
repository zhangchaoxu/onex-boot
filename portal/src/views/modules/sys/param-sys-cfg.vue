<template>
    <el-dialog :visible.sync="visible" title="系统配置" :close-on-click-modal="false" :close-on-press-escape="false">
        <el-form v-loading="formLoading" :model="paramDataForm" :rules="dataRule" ref="dataForm" label-width="120px">
            <el-form-item label="系统标题" prop="title">
                <el-input v-model="paramDataForm.title" placeholder="系统标题" maxlength="100" show-word-limit/>
            </el-form-item>
            <el-form-item label="系统简称" prop="titleBrand">
                <el-input v-model="paramDataForm.titleBrand" placeholder="系统简称" type="textarea"/>
            </el-form-item>
            <el-form-item label="简称(缩起)" prop="titleBrandMini">
                <el-input v-model="paramDataForm.titleBrandMini" placeholder="简称(缩起)" type="textarea"/>
            </el-form-item>
            <el-form-item label="登录标题" prop="loginTitle">
                <el-input v-model="paramDataForm.loginTitle" placeholder="登录标题" maxlength="100" show-word-limit/>
            </el-form-item>
            <el-form-item label="版权声明" prop="copyright">
                <el-input v-model="paramDataForm.copyright" placeholder="版权声明" maxlength="200" show-word-limit/>
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
        dataFormUpdateURL: `/sys/param/update`,
        dataFormInfoURL: `/sys/param/info?id=`
      },
      dataForm: {
        id: '',
        code: '',
        remark: '',
        content: ''
      },
      paramDataForm: {
        title: '',
        titleBrandMini: '',
        titleBrand: '',
        loginTitle: '',
        copyright: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {}
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
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // json序列化content
      this.paramDataForm = JSON.parse(this.dataForm.content)
    },
    // 表单提交之前的操作
    beforeDateFormSubmit () {
      // 将form转为content的json
      this.dataForm.content = JSON.stringify(this.paramDataForm)
      this.dataFormSubmitParam = this.dataForm
      return true
    }
  }
}
</script>
