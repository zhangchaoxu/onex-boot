<template>
  <el-dialog :visible.sync="visible" :title="!dataForm.id ? $t('add') : $t('update')" :close-on-click-modal="false" :close-on-press-escape="false">
    <el-form v-loading="formLoading" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px">
      <el-form-item prop="type" :label="$t('menu.type')">
        <el-radio-group v-model="dataForm.type" :disabled="!!dataForm.id">
          <el-radio :label="0">{{ $t('menu.type0') }}</el-radio>
          <el-radio :label="1">{{ $t('menu.type1') }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item prop="name" :label="$t('menu.name')">
        <el-input v-model="dataForm.name" :placeholder="$t('menu.name')"/>
      </el-form-item>
      <el-form-item prop="pid" :label="$t('menu.parentName')">
        <el-cascader v-model="menuSelected" :options="menuList" clearable :props="{ emitPath: false, checkStrictly: true, value: 'id', label: 'name', children: 'children'}"
                     @change="(value) => this.dataForm.pid = value ? value : '0'" class="w-percent-100"/>
      </el-form-item>
        <el-form-item prop="url" :label="$t('menu.url')" v-if="dataForm.type === 0">
          <el-input v-model="dataForm.url" :placeholder="$t('menu.url')"/>
        </el-form-item>
      <el-row v-if="dataForm.type === 0">
        <el-col :span="12">
          <el-form-item prop="urlNewBlank" label="打开方式">
            <el-radio-group v-model="dataForm.urlNewBlank" size="mini">
              <el-radio-button :label="0">本窗口</el-radio-button>
              <el-radio-button :label="1">新窗口</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="showMenu" label="菜单">
            <el-radio-group v-model="dataForm.showMenu" size="mini">
              <el-radio-button :label="0">不显示</el-radio-button>
              <el-radio-button :label="1">显示</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="permissions" :label="$t('menu.permissions')">
        <el-input v-model="dataForm.permissions" :placeholder="$t('menu.permissionsTips')"/>
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item prop="sort" :label="$t('menu.sort')" >
            <el-input-number v-model="dataForm.sort" controls-position="right" :min="0" :label="$t('menu.sort')" class="w-percent-100"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item v-if="dataForm.type === 0" prop="icon" :label="$t('menu.icon')" class="icon-list">
            <el-popover v-model="iconListVisible" ref="iconListPopover" placement="bottom-start" trigger="click" popper-class="mod-uc__menu-icon-popover">
              <div class="mod-uc__menu-icon-inner">
                <div class="mod-uc__menu-icon-list">
                  <el-button v-for="(item, index) in iconList" :key="index" @click="iconListCurrentChangeHandle(item)" :class="{ 'is-active': dataForm.icon === item }">
                    <i :class="item"/>
                  </el-button>
                </div>
              </div>
            </el-popover>
            <el-input v-model="dataForm.icon" v-popover:iconListPopover readonly :placeholder="$t('menu.icon')" :suffix-icon="dataForm.icon"/>
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
import { getIconList, removeEmptyChildren } from '@/utils'

export default {
  mixins: [mixinFormModule],
  data () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormSaveURL: `/uc/menu/save`,
        dataFormUpdateURL: `/uc/menu/update`,
        dataFormInfoURL: `/uc/menu/info?id=`
      },
      // 已选中菜单
      menuSelected: ['0'],
      // 菜单列表
      menuListVisible: false,
      menuList: [],
      // 图标列表
      iconList: [],
      iconListVisible: false,
      dataForm: {
        id: '',
        type: 0,
        urlNewBlank: 0,
        showMenu: 1,
        name: '',
        pid: '0',
        parentName: '',
        url: '',
        permissions: '',
        sort: 0,
        icon: ''
      }
    }
  },
  computed: {
    dataRule () {
      return {
        name: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ],
        pid: [
          { required: true, message: this.$t('validate.required'), trigger: 'change' }
        ],
        type: [
          { required: true, message: this.$t('validate.required'), trigger: 'change' }
        ],
        sort: [
          { required: true, message: this.$t('validate.required'), trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    'dataForm.type' (val) {
      this.$refs['dataForm'].clearValidate()
    }
  },
  methods: {
    init () {
      this.formLoading = true
      this.visible = true
      this.$nextTick(() => {
        this.resetForm()
        this.menuSelected = ['0']
        this.menuList = []
        this.iconList = getIconList()
        Promise.all([
          this.getMenuList()
        ]).then(() => {
          this.initFormData()
        })
      })
    },
    // 获取菜单列表
    getMenuList () {
      return this.$http.get('/uc/menu/tree?type=0').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        // 加一个根菜单,用于选择
        this.menuList = [{ 'id': '0', 'name': '根菜单', 'children': removeEmptyChildren(res.data) }]
      }).catch(() => {
      })
    },
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      // 置空
      this.menuSelected = ['0']
      res.data.parentMenuList.forEach(item => this.menuSelected.push(item.id))
    },
    // 图标, 选中
    iconListCurrentChangeHandle (icon) {
      this.dataForm.icon = icon
      this.iconListVisible = false
    }
  }
}
</script>

<style lang="scss">
  .mod-uc__menu {
    .menu-list,
    .icon-list {
      .el-input__inner,
      .el-input__suffix {
        cursor: pointer;
      }
    }
    &-icon-popover {
      width: 458px;
      overflow: hidden;
    }
    &-icon-inner {
      width: 478px;
      max-height: 258px;
      overflow-x: hidden;
      overflow-y: auto;
    }
    &-icon-list {
      width: 458px;
      padding: 0;
      margin: -8px 0 0 -8px;
      > .el-button {
        padding: 8px;
        margin: 8px 0 0 8px;
        > span {
          display: inline-block;
          vertical-align: middle;
          width: 18px;
          height: 18px;
          font-size: 18px;
        }
      }
    }
  }
</style>
