<template>
  <el-card shadow="never" class="aui-card--fill">
    <div class="mod-sys__oss">
      <el-form :inline="true" :model="searchDataForm" @keyup.enter.native="getDataList()">
        <el-form-item>
          <el-button type="primary" @click="uploadHandle()">{{ $t('upload.button') }}</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="success" @click="uploadAliyunHandle()">直传阿里云OSS</el-button>
        </el-form-item>
        <el-form-item>
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
        <el-table-column prop="filename" label="文件名" header-align="center" align="center" min-width="120">
          <template slot-scope="scope">
            <file-viewer type="public" :file="{ name: scope.row.filename, url:scope.row.url }"/>
          </template>
        </el-table-column>
        <el-table-column prop="contentType" label="类型" header-align="center" align="center" min-width="120"/>
        <el-table-column prop="size" label="尺寸" header-align="center" align="center" width="120"/>
        <el-table-column prop="url" label="url" header-align="center" align="center" show-tooltip-when-overflow/>
        <el-table-column prop="createTime" :label="$t('base.createTime')" sortable="custom" header-align="center" align="center" width="180"/>
        <el-table-column :label="$t('handle')" fixed="right" header-align="center" align="center" width="100">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">{{ $t('delete') }}</el-button>
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
      <!-- 弹窗, 上传文件 -->
      <upload v-if="uploadVisible" ref="upload" @refreshDataList="getDataList"/>
      <upload-aliyun v-if="uploadAliyunVisible" ref="uploadAliyun" @refreshDataList="getDataList"/>
    </div>
  </el-card>
</template>

<script>
import mixinListModule from '@/mixins/list-module'
import FileViewer from '@/components/file-viewer'
import Config from './param-oss-cfg'
import Upload from './oss-upload'
import UploadAliyun from './oss-aliyun-upload'

export default {
  mixins: [mixinListModule],
  components: { Config, Upload, UploadAliyun, FileViewer },
  data () {
    return {
      mixinListModuleOptions: {
        getDataListURL: '/sys/oss/page',
        getDataListIsPage: true,
        deleteURL: '/sys/oss/delete',
        deleteBatchURL: '/sys/oss/deleteBatch',
        deleteIsBatch: true
      },
      searchDataForm: {},
      configVisible: false,
      uploadVisible: false,
      uploadAliyunVisible: false
    }
  },
  methods: {
    // 云存储配置
    configHandle () {
      this.configVisible = true
      this.$nextTick(() => {
        this.$refs.config.init()
      })
    },
    // 上传文件
    uploadHandle () {
      this.uploadVisible = true
      this.$nextTick(() => {
        this.$refs.upload.init()
      })
    },
    // 直传阿里云OSS文件
    uploadAliyunHandle () {
      this.uploadAliyunVisible = true
      this.$nextTick(() => {
        this.$refs.uploadAliyun.init()
      })
    },
    fileViewHandle () {
      this.$http.get(`/sys/oss/presignedUrl`).then(({ data: res }) => {
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        } else {
          console.log(res)
        }
      }).catch(resp => {
        this.formLoading = false
      })
    }
  }
}
</script>
