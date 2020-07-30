<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form  v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-row>
        <el-col :span="12">
          <el-form-item prop="name" :label="$t('base.name')">
            <el-input v-model="dataForm.name" :placeholder="$t('base.name')"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="code" :label="$t('base.code')">
            <el-input v-model="dataForm.code" :placeholder="$t('base.code')"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="remark" :label="$t('base.remark')">
        <el-input v-model="dataForm.remark" :placeholder="$t('base.remark')"/>
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item size="mini" :label="$t('role.menuList')">
            <el-tree
              :data="menuList"
              :props="{ label: 'name', children: 'children' }"
              node-key="id"
              ref="menuListTree"
              accordion
              check-strictly
              show-checkbox>
            </el-tree>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item size="mini" :label="$t('role.deptList')">
            <el-tree
              :data="deptList"
              :props="{ label: 'name', children: 'children' }"
              node-key="id"
              ref="deptListTree"
              accordion
              show-checkbox>
            </el-tree>
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
        dataFormSaveURL: `/uc/role/save`,
        dataFormUpdateURL: `/uc/role/update`,
        dataFormInfoURL: `/uc/role/info?id=`
      },
      menuList: [],
      deptList: [],
      dataForm: {
        id: '',
        name: '',
        code: '',
        menuIdList: [],
        deptIdList: [],
        remark: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        code: [
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
        this.$refs['dataForm'].resetFields()
        this.$refs.menuListTree.setCheckedKeys([])
        this.$refs.deptListTree.setCheckedKeys([])
        Promise.all([
          this.getMenuList(),
          this.getDeptList()
        ]).then(() => {
          this.initFormData()
        })
      })
    },
    // 获取菜单列表
    getMenuList () {
      return this.$http.get('/uc/menu/userTree').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.menuList = res.data
      }).catch(() => {})
    },
    // 获取部门列表
    getDeptList () {
      return this.$http.get('/uc/dept/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.deptList = res.data
      }).catch(() => {})
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      this.dataForm.menuIdList.forEach(item => this.$refs.menuListTree.setChecked(item, true))
      this.$refs.deptListTree.setCheckedKeys(this.dataForm.deptIdList)
    },
    // 表单提交之前的数据处理
    beforeDateFormSubmit () {
      this.dataForm.menuIdList = [
        ...this.$refs.menuListTree.getHalfCheckedKeys(),
        ...this.$refs.menuListTree.getCheckedKeys()
      ]
      this.dataForm.deptIdList = this.$refs.deptListTree.getCheckedKeys()
      this.dataFormSubmitParam = this.dataForm
      return true
    }
  }
}
</script>
