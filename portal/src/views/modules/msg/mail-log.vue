<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-msg__mail-log">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.tplCode" placeholder="模板编码" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-input v-model="searchDataForm.mailTo" placeholder="收件人" clearable/>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.status" :placeholder="$t('base.status')" clearable>
            <el-option :label="$t('success')" :value="1"/>
            <el-option :label="$t('error')" :value="0"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('msg:mailLog:delete')" >
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table
        v-loading="dataListLoading"
        :data="dataList"
        border
        @selection-change="dataListSelectionChangeHandle"
        @sort-change="dataListSortChangeHandle"
        style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="tplCode" label="模板编码" sortable="custom" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="mailFrom" label="发件人" header-align="center" align="center"/>
        <el-table-column prop="mailTo" label="收件人" header-align="center" align="center"/>
        <el-table-column prop="mailCc" label="抄送" header-align="center" align="center"/>
        <el-table-column prop="subject" label="标题" header-align="center" align="center"/>
        <el-table-column prop="content" :label="$t('base.content')" header-align="center" align="center" class-name="nowrap">
          <template slot-scope="scope">
            <el-link type="primary" @click="htmlViewHandle(scope.row.content)" :underline="false">{{ scope.row.content }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" sortable="custom" header-align="center" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" size="small">{{$t('success')}}</el-tag>
            <el-tag v-else size="small" type="danger">{{$t('error')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="$t('base.createTime')" sortable="custom" header-align="center" align="center" width="180"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('msg:mailLog:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
    </div>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/msg/mailLog/page',
        getDataListIsPage: true,
        deleteURL: '/msg/mailLog/delete',
        deleteBatchURL: '/msg/mailLog/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        tplCode: '',
        mailTo: '',
        status: null
      }
    }
  }
}
</script>
