<template>
  <el-card shadow="never" class="aui-card--fill">
    <el-divider>客户信息</el-divider>
    <el-form :model="dataForm" ref="dataForm" label-width="120px" v-loading="formLoading">
      <el-row>
        <el-col :span="8">
          <el-form-item label="客户名称" prop="name">
            <span>{{ dataForm.name }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="客户级别" prop="status">
            <template slot-scope="scope">
              <el-tag v-if="dataForm.level === 1" type="success">重点</el-tag>
              <el-tag v-else-if="dataForm.level === 2" type="warning">普通</el-tag>
              <el-tag v-else-if="dataForm.level === 3" type="info">非优先</el-tag>
            </template>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="客户来源" prop="source">
            <span>{{ dataForm.source }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="联系人" prop="status">
            <span>{{ dataForm.contacts }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="联系电话" prop="source">
            <span>{{ dataForm.mobile }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="成交状态" prop="amount">
            <template slot-scope="scope">
              <el-tag v-if="dataForm.level === 1" type="success">已成交</el-tag>
              <el-tag v-else-if="dataForm.level === 2" type="fail">未成交</el-tag>
            </template>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="地址" prop="dealDate">
            <span>{{ dataForm.address }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="标签" prop="dealDate">
            <span>{{ dataForm.tags }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="备注" prop="dealDate">
            <span>{{ dataForm.remark }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <div style="text-align: center; margin-top: 20px;" v-if="$hasPermission('crm:customer:update')">
        <el-button type="warning" size="small" @click="addOrUpdateHandle(dataForm.id)">{{ $t('update') }}</el-button>
      </div>
      <!-- 弹窗, 新增 / 修改 -->
      <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getInfo"/>
    </el-form>
    <el-divider>商机记录</el-divider>
    <business ref="business" mode="embed" :lazyLoad="true"/>
    <el-divider>合同记录</el-divider>
    <contract ref="contract" mode="embed" :lazyLoad="true"/>
  </el-card>
</template>

<script>
import mixinBaseModule from '@/mixins/base-module'
import mixinFormModule from '@/mixins/form-module'
import AddOrUpdate from './customer-add-or-update'
import Business from './business'
import Contract from './contract'

export default {
  mixins: [mixinBaseModule, mixinFormModule],
  components: { Business, Contract, AddOrUpdate },
  data: function () {
    return {
      // 表单模块参数
      mixinFormModuleOptions: {
        dataFormInfoURL: `/crm/customer/info?id=`
      },
      // 新增修改
      addOrUpdateVisible: false,
      dataForm: {
        id: '',
        name: '',
        sort: 0,
        level: '',
        source: '',
        dealStatus: '',
        contacts: '',
        telephone: '',
        mobile: '',
        content: '',
        status: 1,
        regionName: '',
        regionCode: '',
        address: '',
        lat: '',
        remark: '',
        lng: ''
      }
    }
  },
  activated () {
    this.dataForm.id = this.$route.query.id || ''
    if (!this.dataForm.id) {
      this.formLoading = false
      this.$message.error('参数不能为空')
    } else {
      this.initFormData()
    }
  },
  methods: {
    // form信息获取成功
    onGetInfoSuccess (res) {
      this.dataForm = {
        ...this.dataForm,
        ...res.data
      }
      this.$refs.business.searchDataForm.customerId = this.dataForm.id
      this.$refs.business.searchDataForm.customerName = this.dataForm.name
      this.$refs.business.getDataList()

      this.$refs.contract.searchDataForm.customerId = this.dataForm.id
      this.$refs.contract.searchDataForm.customerName = this.dataForm.name
      this.$refs.contract.getDataList()
    },
    // 新增 / 修改
    addOrUpdateHandle (id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.clear()
        this.$refs.addOrUpdate.dataForm.id = id
        this.$refs.addOrUpdate.dataFormMode = !id ? 'save' : 'update'
        this.$refs.addOrUpdate.init()
      })
    }
  }

}
</script>
