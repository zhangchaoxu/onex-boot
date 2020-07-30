<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-sys__param">
            <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
                <el-form-item>
                    <el-input v-model="searchDataForm.code" :placeholder="$t('base.code')" clearable/>
                </el-form-item>
                <el-form-item>
                    <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('sys:param:save')">
                    <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('sys:param:delete')">
                    <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
                </el-form-item>
            </el-form>
            <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle"
                      @cell-click="cellClickHandle" style="width: 100%;">
                <el-table-column type="selection" header-align="center" align="center" width="50"/>
                <el-table-column prop="code" :label="$t('base.code')" header-align="center" align="center" width="300"/>
                <el-table-column prop="remark" :label="$t('base.remark')" header-align="center" align="center" width="200"/>
                <el-table-column prop="content" :label="$t('base.content')" header-align="center" align="center" class-name="nowrap json link"/>
                <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
                    <template slot-scope="scope">
                        <el-button v-if="$hasPermission('sys:param:update')" type="text" size="small" @click="editHandle(scope.row.id, scope.row.code)">编辑内容</el-button>
                        <el-button v-if="$hasPermission('sys:param:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
                        <el-button v-if="$hasPermission('sys:param:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                    :current-page="page"
                    :page-sizes="[10, 20, 50, 100]"
                    :page-size="limit"
                    :total="total"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="pageSizeChangeHandle"
                    @current-change="pageCurrentChangeHandle"/>
            <!-- 弹窗, 新增 / 修改 -->
            <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
            <!-- 弹窗, 系统配置 -->
            <sys-cfg v-if="sysCfgVisible" ref="sysCfg" @refreshDataList="getDataList"/>
            <!-- 弹窗, oss配置 -->
            <oss-cfg v-if="ossCfgVisible" ref="ossCfg" @refreshDataList="getDataList"/>
            <!-- 弹窗, 后台登录配置 -->
            <login-admin-cfg v-if="loginAdminCfgVisible" ref="loginAdminCfg" @refreshDataList="getDataList"/>
            <!-- 弹窗, 登录渠道配置 -->
            <login-channel-cfg v-if="loginChannelCfgVisible" ref="loginChannelCfg" @refreshDataList="getDataList"/>
            <!-- 弹窗, 微信配置 -->
            <wx-cfg v-if="wxCfgVisible" ref="wxCfg" @refreshDataList="getDataList"/>

            <!-- 弹窗, App关于我们配置 -->
            <app-about-config v-if="appAboutConfigVisible" ref="appAboutConfig" @refreshDataList="getDataList"/>
            <!-- 弹窗, App客服配置-->
            <app-service-config v-if="appServiceConfigVisible" ref="appServiceConfig" @refreshDataList="getDataList"/>
        </div>
    </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'

import AddOrUpdate from './param-add-or-update'
import OssCfg from './param-oss-cfg'
import LoginAdminCfg from './param-login-admin-cfg'
import LoginChannelCfg from './param-login-channel-cfg'
import SysCfg from './param-sys-cfg'
import WxCfg from './param-wx-cfg'

import AppAboutConfig from './params-app-about-config'
import AppServiceConfig from './params-app-service-config'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate, SysCfg, WxCfg, LoginAdminCfg, LoginChannelCfg, OssCfg, AppAboutConfig, AppServiceConfig },
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/sys/param/page',
        getDataListIsPage: true,
        deleteURL: '/sys/param/delete',
        deleteBatchURL: '/sys/param/deleteBatch',
        deleteIsBatch: true
      },
      // 系统配置
      sysCfgVisible: false,
      // 后台登录配置
      loginAdminCfgVisible: false,
      // 登录渠道配置
      loginChannelCfgVisible: false,
      // 云存储配置
      ossCfgVisible: false,
      // 微信配置
      wxCfgVisible: false,

      // App关于我们配置
      appAboutConfigVisible: false,
      // App启动页配置
      appLoadingConfigVisible: false,
      // App客服配置
      appServiceConfigVisible: false,
      searchDataForm: {
        code: ''
      }
    }
  },
  activated () {
    if (this.$route.query.code) {
      this.dataForm.code = this.$route.query.code
    }
    if (this.$route.params.code) {
      this.dataForm.code = this.$route.params.code
    }
    this.getDataList()
  },
  methods: {
    // 修改
    editHandle (id, code) {
      if (code === 'SYS_CFG') {
        this.sysCfgVisible = true
        this.$nextTick(() => {
          this.$refs.sysCfg.dataForm.id = id
          this.$refs.sysCfg.dataFormMode = 'update'
          this.$refs.sysCfg.init()
        })
      } else if (code === 'LOGIN_CFG_ADMIN') {
        this.loginAdminCfgVisible = true
        this.$nextTick(() => {
          this.$refs.loginAdminCfg.dataForm.id = id
          this.$refs.loginAdminCfg.dataFormMode = 'update'
          this.$refs.loginAdminCfg.init()
        })
      } else if (code.startsWith('LOGIN_CHANNEL_CFG')) {
        this.loginChannelCfgVisible = true
        this.$nextTick(() => {
          this.$refs.loginChannelCfg.dataForm.id = id
          this.$refs.loginChannelCfg.dataFormMode = 'update'
          this.$refs.loginChannelCfg.init()
        })
      } else if (code.startsWith('WX_CFG')) {
        this.wxCfgVisible = true
        this.$nextTick(() => {
          this.$refs.wxCfg.dataForm.id = id
          this.$refs.wxCfg.dataFormMode = 'update'
          this.$refs.wxCfg.init()
        })
      } else if (code.startsWith('OSS_CFG')) {
        this.ossCfgVisible = true
        this.$nextTick(() => {
          this.$refs.ossCfg.dataForm.id = id
          this.$refs.ossCfg.dataFormMode = 'update'
          this.$refs.ossCfg.init()
        })
      } else if (code === 'APP_ABOUT_CONFIG_KEY') {
        this.appAboutConfigVisible = true
        this.$nextTick(() => {
          this.$refs.appAboutConfig.dataForm.id = id
          this.$refs.appAboutConfig.dataFormMode = 'update'
          this.$refs.appAboutConfig.init()
        })
      } else if (code === 'APP_SERVICE_CONFIG_KEY') {
        this.appServiceConfigVisible = true
        this.$nextTick(() => {
          this.$refs.appServiceConfig.dataForm.id = id
          this.$refs.appServiceConfig.dataFormMode = 'update'
          this.$refs.appServiceConfig.init()
        })
      } else {
        this.addOrUpdateHandle(id)
      }
    }
  }
}
</script>
