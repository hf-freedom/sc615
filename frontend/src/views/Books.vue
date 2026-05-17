<template>
  <div class="books">
    <el-card>
      <template #header>
        <div class="card-header">
        <span>图书列表</span>
        <el-button type="primary" size="small" @click="loadData">刷新</el-button>
      </div>
      </template>
      <el-table :data="books" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="title" label="书名" />
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="totalQuantity" label="总数量" width="100" />
        <el-table-column label="可借数量" width="100">
          <template #default="{ row }">
            <el-tag :type="row.availableQuantity > 0 ? 'success' : 'danger'">
              {{ row.availableQuantity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="borrowCount" label="借阅次数" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBooks } from '../api'

const books = ref([])

const loadData = async () => {
  try {
    const res = await getBooks()
    books.value = res.data.data
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
