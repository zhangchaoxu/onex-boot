<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-msg__mail-tpl">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.code" :placeholder="$t('base.code')" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.name" :placeholder="$t('base.name')" clearable/>
        </el-form-item>
        <el-form-item class="tiny-item">
          <el-select v-model="searchDataForm.type" :placeholder="$t('base.type')" clearable>
            <el-option label="短信" value="sms"/>
            <el-option label="电子邮件" value="email"/>
            <el-option label="微信公众号模板消息" value="wx_mp_template"/>
            <el-option label="微信小程序订阅消息" value="wx_ma_subscribe"/>
            <el-option label="APP推送" value="app_push"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('msg:mailTpl:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('msg:mailTpl:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                @cell-click="cellClickHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="code" :label="$t('base.code')" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="name" :label="$t('base.name')" header-align="center" align="center" width="150" show-tooltip-when-overflow/>
        <el-table-column prop="type" :label="$t('base.type')" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 'sms'">短信</el-tag>
            <el-tag v-if="scope.row.type === 'email'">电子邮件</el-tag>
            <el-tag v-if="scope.row.type === 'wx_mp_template'">微信公众号模板消息</el-tag>
            <el-tag v-if="scope.row.type === 'wx_ma_subscribe'">微信小程序订阅消息</el-tag>
            <el-tag v-if="scope.row.type === 'app_push'">APP推送</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" :label="$t('base.title')" header-align="center" align="center"/>
        <el-table-column prop="param" :label="$t('base.param')" header-align="center" align="center" class-name="nowrap json link"/>
        <el-table-column prop="content" :label="$t('base.content')" header-align="center" align="center" class-name="nowrap html link" :formatter="htmlFmt"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('msg:mailLog:save')" type="text" size="small" @click="sendHandle(scope.row.type, scope.row.code)">{{ $t('base.send') }}</el-button>
            <el-button v-if="$hasPermission('msg:mailTpl:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('msg:mailTpl:delete')"  type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <!-- 弹窗, 发送邮件 -->
      <send v-if="sendVisible" ref="send"/>
    </div>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './mail-tpl-add-or-update'
import Send from './mail-send'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate, Send },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/msg/mailTpl/page',
        getDataListIsPage: true,
        deleteURL: '/msg/mailTpl/delete',
        deleteBatchURL: '/msg/mailTpl/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        code: '',
        name: '',
        type: ''
      },
      sendVisible: false
    }
  },
  methods: {
    // 发送邮件
    sendHandle (tplType, tplCode) {
      this.sendVisible = true
      this.$nextTick(() => {
        this.$refs.send.dataForm.tplType = tplType
        this.$refs.send.dataForm.tplCode = tplCode
        this.$refs.send.init()
      })
    }
  }
}
</script>
