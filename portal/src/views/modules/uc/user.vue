    <template>
    <el-card shadow="never" class="aui-card--fill">
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
                    <el-select v-model="roleSelected" multiple collapse-tags :placeholder="$t('user.role')" @change="(value) => this.searchDataForm.roleIds = value.join(',')">
                        <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="$hasPermission('uc:user:save')" type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}
                    </el-button>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="$hasPermission('uc:user:delete')" type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}
                    </el-button>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="$hasPermission('uc:user:export')" type="info" @click="exportHandle()">{{ $t('export') }}
                    </el-button>
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
                <el-table-column prop="username" :label="$t('user.username')" header-align="center" align="center">
                    <template slot-scope="scope">
                        <el-link type="primary" @click="previewHandle(scope.row.id)" :underline="false">{{ scope.row.username }}</el-link>
                    </template>
                </el-table-column>
                <el-table-column prop="mobile" :label="$t('user.mobile')" header-align="center" align="center" min-width="120"/>
                <el-table-column prop="realName" :label="$t('user.realName')" header-align="center" align="center"/>
                <el-table-column prop="tenantName" label="租户名" header-align="center" align="center"/>
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
                        <el-tag v-if="scope.row.status === 0" type="danger">{{ $t('user.status0') }}</el-tag>
                        <el-tag v-else-if="scope.row.status === 1" type="success">{{ $t('user.status1') }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
                    <template slot-scope="scope">
                        <el-button v-if="$hasPermission('uc:user:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}
                        </el-button>
                        <el-button v-if="$hasPermission('uc:user:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}
                        </el-button>
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
            <!-- 弹窗，修改状态 -->
            <change-status v-if="changeStatusVisible" ref="changeStatus" @refreshDataList="getDataList"/>
            <!-- 弹窗，导入用户 -->
            <import v-if="importVisible" ref="import" @refreshDataList="getDataList"/>
        </div>
    </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './user-add-or-update'
import ChangeStatus from './user-change-status'
import Import from './user-import'
export default {
  mixins: [mixinListModule],
  components: { ChangeStatus, AddOrUpdate, Import },
  data () {
    return {
      mixinListModuleOptions: {
        activatedIsNeed: false,
        getDataListURL: '/uc/user/page',
        getDataListIsPage: true,
        deleteURL: '/uc/user/delete',
        deleteBatchURL: '/uc/user/deleteBatch',
        deleteIsBatch: true,
        exportURL: '/uc/user/export',
        importURL: '/uc/user/import'
      },
      roleSelected: null,
      roleList: [],
      searchDataForm: {
        username: '',
        realName: '',
        roleIds: '',
        type: ''
      }
    }
  },
  activated () {
    if (this.$route.query.type) {
      this.searchDataForm.type = this.$route.query.type
    }
    if (this.$route.params.type) {
      this.searchDataForm.type = this.$route.params.type
    }
    this.getDataList()
  },
  created () {
    this.getRoleList()
  },
  methods: {
    // 获取角色列表
    getRoleList () {
      this.$http.get('/uc/role/list').then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.roleList = res.data
      }).catch(() => {
      })
    },
    // 导入处理
    importHandle () {
      this.importVisible = true
      this.$nextTick(() => {
        this.$refs.import.dataForm.id = null
        this.$refs.import.init()
      })
    }
  }
}
</script>
