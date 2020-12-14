<template>
  <el-card shadow="never" class="aui-card--fill" v-loading="loading">
    <div class="mod-home">
      <el-row :gutter="20">
        <el-col :span="12" :xs="24">
          <table>
            <tr>
              <th>系统名称</th>
              <td>onex-api</td>
            </tr>
            <tr>
              <th>版本信息</th>
              <td>1.0.0</td>
            </tr>
            <tr>
              <th>操作系统</th>
              <td>{{ sysInfo.osName }}</td>
            </tr>
            <tr>
              <th>系统版本</th>
              <td>{{ sysInfo.osVersion }}</td>
            </tr>
            <tr>
              <th>系统架构</th>
              <td>{{ sysInfo.osArch }}</td>
            </tr>
            <tr>
              <th>CPU核数</th>
              <td>{{ sysInfo.processors }}</td>
            </tr>
            <tr>
              <th>系统内存</th>
              <td>{{ sysInfo.totalPhysical }}MB</td>
            </tr>
            <tr>
              <th>剩余内存</th>
              <td>{{ sysInfo.freePhysical }}MB</td>
            </tr>
            <tr>
              <th>内存使用</th>
              <td>{{ sysInfo.memoryRate }}%</td>
            </tr>
            <tr>
              <th>系统语言</th>
              <td>{{ sysInfo.userLanguage }}</td>
            </tr>
          </table>
        </el-col>
        <el-col :span="12" :xs="24">
          <table>
            <tr>
              <th>JVM信息</th>
              <td>{{ sysInfo.jvmName }}</td>
            </tr>
            <tr>
              <th>JVM版本</th>
              <td>{{ sysInfo.javaVersion }}</td>
            </tr>
            <tr>
              <th>JAVA_HOME</th>
              <td>{{ sysInfo.javaHome }}</td>
            </tr>
            <tr>
              <th>工作目录</th>
              <td>{{ sysInfo.userDir }}</td>
            </tr>
            <tr>
              <th>JVM占用内存</th>
              <td>{{ sysInfo.javaTotalMemory }}MB</td>
            </tr>
            <tr>
              <th>JVM空闲内存</th>
              <td>{{ sysInfo.javaFreeMemory }}MB</td>
            </tr>
            <tr>
              <th>JVM最大内存</th>
              <td>{{ sysInfo.javaMaxMemory }}MB</td>
            </tr>
            <tr>
              <th>当前用户</th>
              <td>{{ sysInfo.userName }}</td>
            </tr>
            <tr>
              <th>CPU负载</th>
              <td>{{ sysInfo.systemCpuLoad }}%</td>
            </tr>
            <tr>
              <th>系统时间</th>
              <td>{{ sysInfo.userTimezone }} {{sysInfo.sysTime}}</td>
            </tr>
          </table>
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>

<script>
export default {
  data () {
    return {
      loading: false,
      sysInfo: {}
    }
  },
  created () {
    this.getSysInfo()
  },
  methods: {
    getSysInfo () {
      this.loading = true
      this.$http.get('/sys/info').then(({ data: res }) => {
        this.loading = false
        if (res.code !== 0) {
          return this.$message.error(res.toast)
        }
        this.sysInfo = res.data
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>
