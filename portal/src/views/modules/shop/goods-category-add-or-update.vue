<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
        <el-row>
            <el-col :span="12">
                <el-form-item prop="depth" :label="$t('base.type')">
                    <el-radio-group v-model="dataForm.depth" @change="dataForm.pid = null">
                        <el-radio :label="1">大类</el-radio>
                        <el-radio :label="2">小类</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12" v-if="dataForm.depth === 2">
                <el-form-item label="上级类别" prop="pid">
                    <el-select v-model="dataForm.pid" placeholder="选择类别" class="w-percent-100">
                        <el-option v-for="item in pidList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row>
            <el-col :span="12">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="dataForm.name" placeholder="名称"></el-input>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item prop="sort" :label="$t('base.sort')">
                    <el-input-number v-model="dataForm.sort" controls-position="right" :min="0" :max="9999" :label="$t('base.sort')"/>
                </el-form-item>
            </el-col>
        </el-row>
      <el-form-item prop="logo" label="图标">
          <file-upload ref="fileUpload" v-model="dataForm.logo" :limit="1" mode="image"/>
      </el-form-item>
      <el-form-item label="描述" prop="content">
          <el-input v-model="dataForm.content" placeholder="描述" type="textarea"></el-input>
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
        dataFormSaveURL: `/shop/goodsCategory/save`,
        dataFormUpdateURL: `/shop/goodsCategory/update`,
        dataFormInfoURL: `/shop/goodsCategory/info?id=`
      },
      // 一级列表
      pidList: [],
      dataForm: {
        id: '',
        depth: 1,
        pid: 0,
        name: '',
        logo: '',
        sort: '',
        content: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        pid: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        depth: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
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
      this.formLoading = true
      this.visible = true
      this.$nextTick(() => {
        this.pidList = []
        this.resetForm()
        Promise.all([
          this.getPidList()
        ]).then(() => {
          this.initFormData()
        })
      })
    },
    // 获取一级列表
    getPidList () {
      return this.$http.get('/shop/goodsCategory/list?pid=0').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.pidList = res.data
      }).catch(() => {
      })
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      if (this.dataForm.pid === '0') {
        this.dataForm.depth = 1
      } else {
        this.dataForm.depth = 2
      }
    }
  }
}
</script>
