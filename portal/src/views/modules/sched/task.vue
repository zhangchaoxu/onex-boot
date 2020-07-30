<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-sched__task">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-input v-model="searchDataForm.name" :placeholder="$t('base.name')" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('sched:task:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('sched:task:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('sched:task:pause')" type="danger" @click="pauseHandle()">{{ $t('schedule.pauseBatch') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('sched:task:resume')" type="danger" @click="resumeHandle()">{{ $t('schedule.resumeBatch') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('sched:task:run')" type="danger" @click="runHandle()">{{ $t('schedule.runBatch') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="$hasPermission('sched:taskLog:info')" type="success" @click="logHandle()">{{ $t('schedule.log') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table
        v-loading="dataListLoading"
        :data="dataList"
        border
        @selection-change="dataListSelectionChangeHandle"
        @sort-change="dataListSortChangeHandle"
        style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="name" :label="$t('base.name')" header-align="center" align="center"></el-table-column>
        <el-table-column prop="param" :label="$t('base.param')" header-align="center" align="center"></el-table-column>
        <el-table-column prop="cron" label="cron" header-align="center" align="center"></el-table-column>
        <el-table-column prop="remark" :label="$t('base.remark')" header-align="center" align="center"></el-table-column>
        <el-table-column prop="status" :label="$t('base.status')" sortable="custom" header-align="center" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" size="small">{{$t('enable')}}</el-tag>
            <el-tag v-else size="small" type="danger">{{$t('disable')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('sched:taskLog:page')" type="text" size="small" @click="logHandle(scope.row.id)">{{ $t('schedule.log') }}</el-button>
            <el-dropdown trigger="click" @command="editActionHandle" class="action-dropdown">
              <span class="el-dropdown-link">{{ $t('handle') }}<i class="el-icon-arrow-down el-icon--right"/></span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-if="$hasPermission('sched:task:update')" :command="composeEditCommandValue('addOrUpdate', scope.row)" icon="el-icon-edit">{{ $t('update') }}</el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('sched:task:delete')" :command="composeEditCommandValue('delete', scope.row)" icon="el-icon-delete">{{ $t('delete') }}</el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('sched:task:run')" :command="composeEditCommandValue('run', scope.row)" icon="el-icon-video-play">{{ $t('run') }}</el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('sched:task:pause') && scope.row.status === 1" :command="composeEditCommandValue('pause', scope.row)" icon="el-icon-video-pause">{{ $t('pause') }}</el-dropdown-item>
                <el-dropdown-item v-if="$hasPermission('sched:task:resume') && scope.row.status === 0" :command="composeEditCommandValue('resume', scope.row)" icon="el-icon-refresh-right">{{ $t('resume') }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
      <!-- 弹窗, 日志列表 -->
      <log v-if="logVisible" ref="log"></log>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './task-add-or-update'
import Log from './task-log'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate, Log },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/sched/task/page',
        getDataListIsPage: true,
        deleteURL: '/sched/task/delete',
        deleteBatchURL: '/sched/task/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        name: ''
      },
      logVisible: false
    }
  },
  methods: {
    /** 其它更多按钮操作 */
    moreEditActionHandle (command) {
      if (command.command === 'pause') {
        // 暂停
        this.pauseHandle(command.row[this.mixinListModuleOptions.idKey])
      } else if (command.command === 'run') {
        // 运行
        this.runHandle(command.row[this.mixinListModuleOptions.idKey])
      } else if (command.command === 'resume') {
        // 恢复
        this.resumeHandle(command.row[this.mixinListModuleOptions.idKey])
      }
    },
    // 暂停
    pauseHandle (id) {
      if (!id && this.dataListSelections.length <= 0) {
        return this.$message({
          message: this.$t('prompt.deleteBatch'),
          type: 'warning',
          duration: 500
        })
      }
      this.$confirm(this.$t('prompt.info', { 'handle': this.$t('schedule.pause') }), this.$t('prompt.title'), {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        type: 'warning'
      }).then(() => {
        this.$http.put('/sched/task/pause', id ? [id] : this.dataListSelections.map(item => item.id)).then(({ data: res }) => {
          if (res.code !== 0) {
            return this.$message.error(res.toast)
          }
          this.$message({
            message: this.$t('prompt.success'),
            type: 'success',
            duration: 500,
            onClose: () => {
              this.getDataList()
            }
          })
        }).catch(() => {})
      }).catch(() => {})
    },
    // 恢复
    resumeHandle (id) {
      if (!id && this.dataListSelections.length <= 0) {
        return this.$message({
          message: this.$t('prompt.deleteBatch'),
          type: 'warning',
          duration: 500
        })
      }
      this.$confirm(this.$t('prompt.info', { 'handle': this.$t('schedule.resume') }), this.$t('prompt.title'), {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        type: 'warning'
      }).then(() => {
        this.$http.put('/sched/task/resume', id ? [id] : this.dataListSelections.map(item => item.id)).then(({ data: res }) => {
          if (res.code !== 0) {
            return this.$message.error(res.toast)
          }
          this.$message({
            message: this.$t('prompt.success'),
            type: 'success',
            duration: 500,
            onClose: () => {
              this.getDataList()
            }
          })
        }).catch(() => {})
      }).catch(() => {})
    },
    // 执行
    runHandle (id) {
      if (!id && this.dataListSelections.length <= 0) {
        return this.$message({
          message: this.$t('prompt.deleteBatch'),
          type: 'warning',
          duration: 500
        })
      }
      this.$confirm(this.$t('prompt.info', { 'handle': this.$t('schedule.run') }), this.$t('prompt.title'), {
        confirmButtonText: this.$t('confirm'),
        cancelButtonText: this.$t('cancel'),
        type: 'warning'
      }).then(() => {
        this.$http.put('/sched/task/run', id ? [id] : this.dataListSelections.map(item => item.id)).then(({ data: res }) => {
          if (res.code !== 0) {
            return this.$message.error(res.toast)
          }
          this.$message({
            message: this.$t('prompt.success'),
            type: 'success',
            duration: 500,
            onClose: () => {
              this.getDataList()
            }
          })
        }).catch(() => {})
      }).catch(() => {})
    },
    // 日志列表
    logHandle () {
      this.logVisible = true
      this.$nextTick(() => {
        this.$refs.log.init()
      })
    }
  }
}
</script>
