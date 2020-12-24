<template>
    <el-card shadow="never" class="aui-card--fill">
        <div class="mod-cms__article">
            <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
                <el-form-item class="small-item">
                    <el-select v-model="searchDataForm.articleCategoryId" placeholder="分类" style="width: 100%" clearable>
                        <el-option v-for="item in articleCategoryList" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
                <el-form-item class="small-item">
                    <el-select v-model="searchDataForm.status" placeholder="状态" class="w-percent-100">
                        <el-option
                                v-for="item in statusOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item class="small-item">
                    <el-select v-model="searchDataForm.top" placeholder="首页推荐" class="w-percent-100">
                        <el-option v-for="item in topOptions" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item class="small-item">
                    <el-input v-model="searchDataForm.author" placeholder="作者" clearable/>
                </el-form-item>
                <el-form-item class="small-item">
                    <el-input v-model="searchDataForm.name" placeholder="标题" clearable/>
                </el-form-item>
                <el-form-item>
                    <el-input v-model="searchDataForm.content" placeholder="文章内容" clearable/>
                </el-form-item>
                <el-form-item>
                    <el-button @click="queryDataList()">{{ $t('query') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('cms:article:save')">
                    <el-button type="primary" @click="addOrUpdateHandle()">{{ $t('add') }}</el-button>
                </el-form-item>
                <el-form-item v-if="$hasPermission('cms:article:delete')">
                    <el-button type="danger" @click="deleteHandle()">{{ $t('deleteBatch') }}</el-button>
                </el-form-item>
            </el-form>
            <el-table v-loading="dataListLoading" :data="dataList" border @selection-change="dataListSelectionChangeHandle" @sort-change="dataListSortChangeHandle"
                      @cell-click="cellClickHandle" style="width: 100%;">
                <el-table-column type="selection" header-align="center" align="center" width="50"/>
                <el-table-column prop="articleCategoryName" label="文章类目" header-align="center" align="center" width="200"/>
                <el-table-column prop="author" label="作者" header-align="center" align="center" width="100"/>
                <el-table-column prop="name" label="标题" header-align="center" align="center" width="200"/>
                <el-table-column prop="imgs" label="封面" header-align="center" align="center" width="100">
                    <template slot-scope="scope">
                        <el-image v-if="scope.row.imgs" lazy class="table-img" :src="scope.row.imgs.split(',')[0]" :preview-src-list="scope.row.imgs.split(',')" fit="cover"/>
                    </template>
                </el-table-column>
                <el-table-column prop="content" label="文章内容" header-align="center" align="center" min-width="200" class-name="nowrap html link" :formatter="htmlFmt"/>
                <el-table-column prop="status" label="状态" header-align="center" align="center" width="100">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status === 0" size="small" type="info">未发布</el-tag>
                        <el-tag v-else-if="scope.row.status === 1" size="small" type="success">已发布</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="top" label="推荐" header-align="center" align="center" width="100">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.top === 0" size="small" type="info">否</el-tag>
                        <el-tag v-else-if="scope.row.top === 1" size="small" type="success">是</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="sort" sortable="custom" label="排序" header-align="center" align="center" width="100"/>
                <el-table-column prop="readNum" label="阅读量" header-align="center" align="center" width="100" sortable="custom"/>
                <el-table-column prop="pubDate" label="发布时间" header-align="center" align="center" width="180"/>
                <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="150">
                    <template slot-scope="scope">
                        <el-button v-if="$hasPermission('cms:article:info')" type="text" size="small"
                                   @click="openLinkHandle(`${process.env.VUE_APP_API_URL}/cms/html/article/info?id=${scope.row.id}`)">{{ $t('preview') }}</el-button>
                        <el-button v-if="$hasPermission('cms:article:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">{{ $t('update') }}</el-button>
                        <el-button v-if="$hasPermission('cms:article:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
        </div>
    </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinListModule from '@/mixins/list-module'
import AddOrUpdate from './article-add-or-update'

export default {
  mixins: [mixinBaseModule, mixinListModule],
  components: { AddOrUpdate },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/cms/article/page',
        getDataListIsPage: true,
        exportURL: '/cms/article/export',
        deleteURL: '/cms/article/delete',
        deleteBatchURL: '/cms/article/deleteBatch',
        deleteIsBatch: true
      },
      statusOptions: [{
        value: 0,
        label: '未发布'
      }, {
        value: 1,
        label: '发布'
      }],
      topOptions: [{
        value: 0,
        label: '否'
      }, {
        value: 1,
        label: '是'
      }],
      // 文章分类列表
      articleCategoryList: [],
      searchDataForm: {
        name: '',
        status: '',
        top: '',
        content: '',
        author: '',
        articleCategoryId: ''
      }
    }
  },
  created () {
    this.getArticleCategoryList()
  },
  methods: {
    // 获取文章分类列表
    getArticleCategoryList () {
      this.$http.get(`/cms/articleCategory/list`).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.articleCategoryList = res.data
      }).catch(() => {
      })
    }
  }
}
</script>
