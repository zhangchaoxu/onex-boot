<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-ba__exam-user">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item class="middle-item">
          <el-input v-model="searchDataForm.userName" placeholder="用户" readonly>
            <user-pick class="small-button" slot="append" :userId="searchDataForm.userId" @onUserPicked="onUserPicked"/>
          </el-input>
        </el-form-item>
        <el-form-item class="small-item">
          <el-select v-model="searchDataForm.subjectType" placeholder="类型" clearable>
            <el-option label="成人检测" :value="1"/>
            <el-option label="孩子检测" :value="2"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
                  v-model="dateRange"
                  type="datetimerange"
                  @change="dateRangeChangeHandle"
                  :picker-options="dateRangePickerOptions"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  :range-separator="$t('datePicker.range')"
                  :start-placeholder="$t('datePicker.start')"
                  :end-placeholder="$t('datePicker.end')">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('ba:examUser:export')">
          <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('ba:examUser:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('ba:examUser:delete')">
          <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle" style="width: 100%;">
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column prop="userName" label="用户" header-align="center" align="center"></el-table-column>
        <el-table-column prop="subjectType" label="检测类型" header-align="center" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.subjectType === 1">成人检测</el-tag>
            <el-tag v-else-if="scope.row.subjectType === 2" type="success">孩子检测</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="检测结果" header-align="center" align="center"></el-table-column>
        <el-table-column prop="parentName" label="家长名字" header-align="center" align="center"></el-table-column>
        <el-table-column prop="childName" label="小孩名字" header-align="center" align="center"></el-table-column>
        <el-table-column prop="childClass" label="小孩年级" header-align="center" align="center"></el-table-column>
        <el-table-column prop="createTime" label="提交时间" header-align="center" align="center" width="180"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('ba:examUser:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('ba:examUser:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="mixinListModuleOptions.getDataListIsPage"
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="limit"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="pageSizeChangeHandle"
        @current-change="pageCurrentChangeHandle"/>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import mixinBaseModule from '@/mixins/base-module'
import AddOrUpdate from './exam-user-add-or-update'
import UserPick from '../uc/user-pick'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { UserPick, AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/ba/examUser/page',
        getDataListIsPage: true,
        exportURL: '/ba/examUser/export',
        deleteURL: '/ba/examUser/delete',
        deleteIsBatch: true
      },
      searchDataForm: {
        subjectType: '',
        userName: '',
        userId: '',
        startCreateTime: '',
        endCreateTime: ''
      }
    }
  }
}
</script>
