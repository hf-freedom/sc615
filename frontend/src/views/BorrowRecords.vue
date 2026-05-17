<template>
  <div class="borrow-records">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>⚠️ 待归还提醒</span>
              <el-badge :value="pendingReminders.length" class="item" type="warning" />
            </div>
          </template>
          <el-empty v-if="pendingReminders.length === 0" description="暂无待归还图书" />
          <el-table v-else :data="pendingReminders" style="width: 100%" size="small">
            <el-table-column label="用户" width="120">
              <template #default="{ row }">
                {{ getUserName(row.userId) }}
              </template>
            </el-table-column>
            <el-table-column label="图书" width="200">
              <template #default="{ row }">
                {{ getBookTitle(row.bookId) }}
              </template>
            </el-table-column>
            <el-table-column label="到期日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.dueDate) }}
              </template>
            </el-table-column>
            <el-table-column label="剩余天数" width="120">
              <template #default="{ row }">
                <el-tag :type="getDaysRemainingType(row)">
                  {{ getDaysRemaining(row) }} 天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="提醒状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.reminderSent ? 'success' : 'warning'">
                  {{ row.reminderSent ? '已提醒' : '待提醒' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
            <span>借阅记录列表</span>
            <el-button type="primary" size="small" @click="loadData">刷新</el-button>
          </div>
          </template>
          <el-table :data="borrowRecords" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="记录ID" width="250" />
            <el-table-column label="用户" width="120">
              <template #default="{ row }">
                {{ getUserName(row.userId) }}
              </template>
            </el-table-column>
            <el-table-column label="图书" width="200">
              <template #default="{ row }">
                {{ getBookTitle(row.bookId) }}
              </template>
            </el-table-column>
            <el-table-column label="借阅日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.borrowDate) }}
              </template>
            </el-table-column>
            <el-table-column label="到期日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.dueDate) }}
              </template>
            </el-table-column>
            <el-table-column label="归还日期" width="180">
              <template #default="{ row }">
                {{ row.returnDate ? formatDate(row.returnDate) : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getRecordType(row)">
                  {{ getRecordStatus(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="提醒状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="!row.isReturned" :type="row.reminderSent ? 'success' : 'info'">
                  {{ row.reminderSent ? '已提醒' : '未提醒' }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="overdueDays" label="逾期天数" width="100" />
            <el-table-column prop="penaltyScore" label="罚分" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getBorrowRecords, getBooks, getUsers } from '../api'

const borrowRecords = ref([])
const books = ref([])
const users = ref([])
const loading = ref(false)

const getBookTitle = (bookId) => {
  const book = books.value.find(b => b.id === bookId)
  return book ? book.title : bookId
}

const getUserName = (userId) => {
  const user = users.value.find(u => u.id === userId)
  return user ? user.name : userId
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

const getDaysRemaining = (record) => {
  const now = new Date()
  const dueDate = new Date(record.dueDate)
  const diff = Math.ceil((dueDate - now) / (1000 * 60 * 60 * 24))
  return diff
}

const getDaysRemainingType = (record) => {
  const days = getDaysRemaining(record)
  if (days <= 0) return 'danger'
  if (days <= 3) return 'warning'
  if (days <= 7) return 'info'
  return 'success'
}

const pendingReminders = computed(() => {
  return borrowRecords.value
    .filter(r => !r.isReturned)
    .filter(r => getDaysRemaining(r) <= 7)
    .sort((a, b) => getDaysRemaining(a) - getDaysRemaining(b))
})

const getRecordStatus = (record) => {
  if (record.isReturned) return '已归还'
  if (record.isOverdue) return '已逾期'
  const days = getDaysRemaining(record)
  if (days <= 3) return '即将到期'
  return '借阅中'
}

const getRecordType = (record) => {
  if (record.isReturned) return 'info'
  if (record.isOverdue) return 'danger'
  const days = getDaysRemaining(record)
  if (days <= 3) return 'warning'
  return 'success'
}

const loadData = async () => {
  loading.value = true
  try {
    const [recordsRes, booksRes, usersRes] = await Promise.all([
      getBorrowRecords(), getBooks(), getUsers()
    ])
    borrowRecords.value = recordsRes.data.data
    books.value = booksRes.data.data
    users.value = usersRes.data.data
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
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

.item {
  margin-top: 0;
}
</style>
