<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-sys__dict">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-input v-model="searchDataForm.name" :placeholder="$t('base.name')" clearable/>
        </el-form-item>
        <el-form-item v-if="searchDataForm.pid === '0'">
          <el-input v-model="searchDataForm.type" :placeholder="$t('base.type')" clearable/>
        </el-form-item>
        <el-form-item v-if="searchDataForm.pid !== '0'">
          <el-input v-model="searchDataForm.value" :placeholder="$t('base.value')" clearable/>
        </el-form-item>
        <el-form-item>
          <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('sys:dict:save')">
          <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
        </el-form-item>
        <el-form-item v-if="$hasPermission('sys:dict:delete')">
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
        <el-table-column prop="name" :label="$t('base.name')" header-align="center" align="center"/>
        <el-table-column prop="type" :label="$t('base.type')" header-align="center" align="center">
          <template slot-scope="scope">
            <span v-if="searchDataForm.pid !== '0'">{{ scope.row.type }}</span>
            <el-link v-else type="primary" @click="childHandle(scope.row)" :underline="false">{{ scope.row.type }}</el-link>
          </template>
        </el-table-column>
        <el-table-column v-if="searchDataForm.pid !== '0'" prop="value" label="字典值" header-align="center" align="center"/>
        <el-table-column prop="sort" :label="$t('base.sort')" sortable="custom" header-align="center" align="center"/>
        <el-table-column prop="remark" :label="$t('base.remark')" header-align="center" align="center"/>
        <el-table-column  :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
          <template slot-scope="scope">
            <el-button v-if="$hasPermission('sys:dict:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row)">{{ $t('update') }}</el-button>
            <el-button v-if="$hasPermission('sys:dict:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="searchDataForm.pid === '0'"
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="limit"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="pageSizeChangeHandle"
        @current-change="pageCurrentChangeHandle"/>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './dict-add-or-update'
import { moduleRoutes } from '@/router'

export default {
  mixins: [mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/sys/dict/page',
        getDataListIsPage: true,
        deleteURL: '/sys/dict/delete',
        deleteBatchURL: '/sys/dict/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {
        pid: '0',
        name: '',
        value: '',
        type: ''
      }
    }
  },
  activated () {
    if (this.$route.params.pid) {
      // 通过路由参数pid, 控制列表请求操作
      this.searchDataForm.pid = this.$route.params.pid || '0'
      if (this.searchDataForm.pid !== '0') {
        this.mixinListModuleOptions.getDataListURL = '/sys/dict/list'
        this.mixinListModuleOptions.getDataListIsPage = false
        this.searchDataForm.type = this.$route.params.type || ''
      }
    }
    this.getDataList()
  },
  methods: {
    // 子级
    childHandle (row) {
      // 组装路由名称
      const routeName = `${this.$route.name}__${row.id}`
      // 并判断是否已添加
      let route = window.SITE_CONFIG['dynamicRoutes'].filter(item => item.name === routeName)[0]
      // 是: 则直接跳转
      if (route) {
        return this.$router.push({ name: routeName, params: { 'pid': row.id } })
      }
      // 否: 添加并全局变量保存, 再跳转
      route = {
        path: routeName,
        component: () => import(`@/views/modules/${this.$route.name.replace(/-/g, '/')}`),
        name: routeName,
        meta: {
          ...window.SITE_CONFIG['contentTabDefault'],
          menuId: this.$route.meta.menuId,
          title: `${this.$route.meta.title} - ${row.type}`
        }
      }
      this.$router.addRoutes([
        {
          ...moduleRoutes,
          name: `main-dynamic__${route.name}`,
          children: [route]
        }
      ])
      window.SITE_CONFIG['dynamicRoutes'].push(route)
      this.$router.push({ name: route.name, params: { 'pid': row.id, 'type': row.type } })
    },
    // 新增 / 修改
    addOrUpdateHandle (row = {}) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.dataForm.id = row.id
        this.$refs.addOrUpdate.mode = !row.id ? 'add' : 'update'
        this.$refs.addOrUpdate.dataForm.pid = this.searchDataForm.pid
        this.$refs.addOrUpdate.dataForm.type = row.type || this.searchDataForm.type || ''
        this.$refs.addOrUpdate.init()
      })
    }
  }
}
</script>
