<template>
    <el-button icon="el-icon-user" @click="openPickHandle()">
        <el-dialog title="选择用户" :visible.sync="visible" append-to-body modal-append-to-body
                   :close-on-click-modal="false" :close-on-press-escape="false"
                   @close="closeHandle"
                   width="80%" :fullscreen="fullscreen">
                <div class="mod-uc__user">
                    <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
                        <el-form-item class="small-item">
                            <el-input v-model="searchDataForm.username" :placeholder="$t('user.username')" clearable/>
                        </el-form-item>
                        <el-form-item class="small-item">
                            <el-input v-model="searchDataForm.realName" :placeholder="$t('user.realName')" clearable/>
                        </el-form-item>
                        <el-form-item class="small-item">
                            <el-input v-model="searchDataForm.mobile" :placeholder="$t('user.mobile')" clearable/>
                        </el-form-item>
                        <el-form-item>
                            <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                        </el-form-item>
                    </el-form>
                    <el-table
                            v-loading="dataListLoading"
                            :data="dataList"
                            ref="dataTable"
                            :select-on-indeterminate="false"
                            @selection-change="dataListSelectionChangeHandle"
                            @sort-change="dataListSortChangeHandle"
                            style="width: 100%;">
                        <el-table-column type="selection" header-align="center" align="center" width="50"/>
                        <el-table-column prop="deptName" label="单位" header-align="center" align="center" min-width="120"/>
                        <el-table-column prop="username" :label="$t('user.username')" header-align="center" align="center">
                            <template slot-scope="scope">
                                <el-link type="primary" @click="previewHandle(scope.row.id)" :underline="false">{{ scope.row.username }}</el-link>
                            </template>
                        </el-table-column>
                        <el-table-column prop="mobile" :label="$t('user.mobile')" header-align="center" align="center" min-width="120"/>
                        <el-table-column prop="realName" :label="$t('user.realName')" header-align="center" align="center"/>
                        <el-table-column prop="code" label="工号" header-align="center" align="center"/>
                        <el-table-column prop="type" label="类型" header-align="center" align="center" width="120">
                            <template slot-scope="scope">
                                <el-tag v-if="scope.row.type === 0">超级管理员</el-tag>
                                <el-tag v-else-if="scope.row.type === 10">系统管理员</el-tag>
                                <el-tag v-else-if="scope.row.type === 20">单位管理员</el-tag>
                                <el-tag v-else-if="scope.row.type === 100">App用户</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="roleNames" :label="$t('user.role')" header-align="center" align="center">
                            <template slot-scope="scope" v-if="scope.row.roleNames">
                                <el-tag :key="tag" v-for="tag in scope.row.roleNames.split(',')" style="margin-right: 3px;">{{tag}}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="status" :label="$t('user.status')" sortable="custom" header-align="center" align="center">
                            <template slot-scope="scope">
                                <el-button v-if="scope.row.status === 0" size="mini" type="danger" round :disabled="!$hasPermission('uc:user:update')" @click="changeStatusHandle(scope.row.id)">{{ $t('user.status0') }}</el-button>
                                <el-button v-else size="mini" type="success" round :disabled="!$hasPermission('uc:user:update')" @click="changeStatusHandle(scope.row.id)">{{ $t('user.status1') }}</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <el-pagination
                            :current-page="page"
                            :page-sizes="[10, 20, 50, 100]"
                            :page-size="limit"
                            :total="total"
                            hide-on-single-page
                            small
                            layout="total, prev, pager, next"
                            @size-change="pageSizeChangeHandle"
                            @current-change="pageCurrentChangeHandle"/>
                </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="visible = false">{{ $t('cancel') }}</el-button>
                <el-button type="primary" @click="dataFormSubmitHandle()" :disabled="!dataListSelections || dataListSelections.length === 0">{{ $t('confirm') }}</el-button>
                <el-button type="warning" @click="clearSubmitHandle()">{{ $t('clear') }}</el-button>
            </div>
        </el-dialog>
    </el-button>
</template>

<script>
import mixinListModule from '@/mixins/list-module'

export default {
  name: 'user-pick',
  // 参数
  props: {
    // 请求码
    requestCode: {
      type: String,
      default: null
    },
    userId: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'single'
    }
  },
  mixins: [mixinListModule],
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/uc/user/page',
        getDataListIsPage: true
      },
      // 是否可见
      visible: false,
      // 全屏
      fullscreen: false,
      searchDataForm: {
        username: '',
        mobile: '',
        realName: ''
      }
    }
  },
  methods: {
    openPickHandle () {
      this.visible = true
      this.getDataList()
    },
    // 关闭时的回调
    closeHandle () {

    },
    dataListSelectionChangeHandle (val) {
      if (this.type === 'multi') {
        this.dataListSelections = val
      } else {
        if (val.length > 1) {
          this.$refs.dataTable.toggleRowSelection(val[0], false)
          val.splice(0, 1)
        }
        this.dataListSelections = val
      }
    },
    dataFormSubmitHandle () {
      // 验证通过,提交表单
      this.$emit('onUserPicked', this.dataListSelections, this.requestCode)
      this.visible = false
    },
    // 清空选择内容
    clearSubmitHandle () {
      this.$emit('onUserPicked', null, this.requestCode)
      this.visible = false
    }
  }
}
</script>
