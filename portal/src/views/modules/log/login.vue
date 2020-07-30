<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-log__login">
            <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
                <el-form-item class="small-item">
                    <el-input v-model="searchDataForm.createName" placeholder="用户" clearable/>
                </el-form-item>
                <el-form-item class="small-item">
                    <el-select v-model="searchDataForm.result" placeholder="结果" clearable>
                        <el-option label="成功" :value="1"/>
                        <el-option label="失败" :value="0"/>
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
                    <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('log:login:export')">
                    <el-button type="info" @click="exportHandle()">{{ $t('export') }}</el-button>
                </el-form-item>
            </el-form>
            <el-table v-loading="dataListLoading" :data="dataList" border @sort-change="dataListSortChangeHandle" style="width: 100%;">
                <el-table-column prop="createName" label="用户" header-align="center" align="center" width="150"/>
                <el-table-column prop="type" label="类型" header-align="center" align="center" width="140">
                    <template slot-scope="scope">
                        <span v-if="scope.row.type === -1" style="color: red">退出</span>
                        <span v-else-if="scope.row.type === 10">后台帐号密码登录</span>
                        <span v-else-if="scope.row.type === 20">后台手机密码登录</span>
                        <span v-else-if="scope.row.type === 30">后台手机短信登录</span>
                        <span v-else-if="scope.row.type === 40">后台微信登录</span>
                        <span v-else-if="scope.row.type === 50">APP帐号密码登录</span>
                        <span v-else-if="scope.row.type === 60">APP手机密码登录</span>
                        <span v-else-if="scope.row.type === 70">APP手机短信登录</span>
                        <span v-else-if="scope.row.type === 80">APP微信登录</span>
                        <span v-else>{{scope.row.type}}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="result" label="结果" sortable="custom" header-align="center" align="center" width="150">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.result === 1" size="small" type="success">成功</el-tag>
                        <el-tag v-else-if="scope.row.result === 0" size="small" type="danger">失败</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="msg" label="结果消息" header-align="center" align="center"/>
                <el-table-column prop="ip" label="IP" header-align="center" align="center" width="200"/>
                <el-table-column prop="userAgent" label="UA" header-align="center" align="center" show-overflow-tooltip/>
                <el-table-column prop="createTime" label="创建时间" sortable="custom" header-align="center" align="center" width="180"/>
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
        getDataListURL: '/log/login/page',
        getDataListIsPage: true,
        exportURL: '/log/login/export'
      },
      searchDataForm: {
        createName: '',
        status: '',
        startCreateTime: '',
        endCreateTime: ''
      }
    }
  }
}
</script>
