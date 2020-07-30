<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-sys__calendar">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-input v-model="searchDataForm.id" placeholder="id" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('sys:calendar:sync')">
          <el-button type="success" @click="syncHandle()">同步</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('sys:calendar:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" style="width: 100%;">
        <el-table-column v-if="$hasPermission('sys:calendar:delete')" type="selection" header-align="center" align="center" width="50"/>
        <el-table-column prop="year" label="年" header-align="center" align="center"/>
        <el-table-column prop="month" label="月" header-align="center" align="center"/>
        <el-table-column prop="day" label="日" header-align="center" align="center"/>
        <el-table-column prop="type" label="类型" header-align="center" align="center" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.type === 0" size="small" type="danger">调班</el-tag>
            <el-tag v-else-if="scope.row.type === 1" size="small" type="success">节假日</el-tag>
            <el-tag v-else-if="scope.row.type === 2" size="small" type="info">正常工作日</el-tag>
            <el-tag v-else-if="scope.row.type === 3" size="small">正常周末</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="week" label="星期" header-align="center" align="center"/>
        <el-table-column prop="lunaryear" label="农历年" header-align="center" align="center"/>
        <el-table-column prop="lunarmonth" label="农历月" header-align="center" align="center"/>
        <el-table-column prop="lunarday" label="农历日" header-align="center" align="center"/>
        <el-table-column prop="shengxiao" label="生肖" header-align="center" align="center"/>
        <el-table-column prop="ganzhi" label="干支" header-align="center" align="center"/>
        <el-table-column prop="star" label="星座" header-align="center" align="center"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('sys:calendar:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('sys:calendar:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <!-- 弹窗, 同步 -->
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './calendar-add-or-update'
export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/sys/calendar/page',
        getDataListIsPage: true,
        exportURL: '/sys/calendar/export',
        deleteURL: '/sys/calendar/delete',
        deleteBatchURL: '/sys/calendar/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        id: ''
      }
    }
  },
  methods: {
    // 同步接口
    syncHandle () {
      this.$http.post('/sys/calendar/sync').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.getDataList()
      }).catch(() => {
      })
    }
  }
}
</script>
