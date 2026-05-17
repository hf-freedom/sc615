<template>
  <div class="users">
    <el-card>
      <template #header>
        <div class="card-header">
        <span>用户列表</span>
        <el-button type="primary" size="small" @click="loadData">刷新</el-button>
      </div>
      </template>
      <el-table :data="users" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="penaltyScore" label="罚分" width="100">
          <template #default="{ row }">
            <el-tag :type="row.penaltyScore > 0 ? 'warning' : 'info'">
              {{ row.penaltyScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="借阅状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.canBorrow ? 'success' : 'danger'">
              {{ row.canBorrow ? '可借阅' : '限制' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maxBorrowLimit" label="最大借阅量" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUsers } from '../api'

const users = ref([])

const loadData = async () => {
  try {
    const res = await getUsers()
    users.value = res.data.data
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}
</style>
