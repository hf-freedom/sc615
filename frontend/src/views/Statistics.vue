<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>借阅统计概览</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="4">
              <div class="stat-item">
                <div class="stat-number" style="color: #409EFF">{{ stats.totalBorrowed || 0 }}</div>
                <div class="stat-label">总借阅量</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="stat-item">
                <div class="stat-number" style="color: #67C23A">{{ stats.currentBorrowed || 0 }}</div>
                <div class="stat-label">当前借阅</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="stat-item">
                <div class="stat-number" style="color: #E6A23C">{{ stats.overdueCount || 0 }}</div>
                <div class="stat-label">逾期数量</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="stat-item">
                <div class="stat-number" style="color: #F56C6C">{{ pendingReminders.length }}</div>
                <div class="stat-label">待提醒</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="stat-item">
                <div class="stat-number" style="color: #67C23A">{{ reminderStats.sent }}</div>
                <div class="stat-label">已提醒</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="stat-item">
                <div class="stat-number" style="color: #909399">{{ stats.turnoverRate || '0%' }}</div>
                <div class="stat-label">周转率</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>📢 归还提醒列表</span>
              <el-tag type="warning">共 {{ pendingReminders.length }} 条待提醒</el-tag>
            </div>
          </template>
          <el-empty v-if="pendingReminders.length === 0" description="暂无待归还提醒" />
          <el-table v-else :data="pendingReminders" style="width: 100%">
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
            <el-table-column label="剩余天数" width="120">
              <template #default="{ row }">
                <el-tag :type="getDaysRemainingType(row)">
                  {{ getDaysRemaining(row) }} 天
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="紧急程度" width="120">
              <template #default="{ row }">
                <el-tag :type="getUrgencyType(row)" effect="dark">
                  {{ getUrgencyLevel(row) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="提醒状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.reminderSent ? 'success' : 'warning'">
                  {{ row.reminderSent ? '已发送' : '待发送' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门图书排行</span>
            </div>
          </template>
          <el-table :data="popularBooks" style="width: 100%">
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" />
            <el-table-column prop="category" label="分类" />
            <el-table-column prop="borrowCount" label="借阅次数" width="120" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>逾期用户列表</span>
            </div>
          </template>
          <el-table :data="overdueUsers" style="width: 100%">
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="email" label="邮箱" />
            <el-table-column prop="penaltyScore" label="罚分" width="100">
              <template #default="{ row }">
                <el-tag type="warning">{{ row.penaltyScore }}</el-tag>
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
              <span>📋 预约队列状态</span>
              <div class="header-stats">
                <el-tag type="info">待通知: {{ notificationStats.pending }}</el-tag>
                <el-tag type="warning">已通知: {{ notificationStats.notified }}</el-tag>
                <el-tag type="success">总预约: {{ notificationStats.total }}</el-tag>
              </div>
            </div>
          </template>
          <el-empty v-if="bookReservationGroups.length === 0" description="暂无预约记录" />
          <div v-else>
            <el-collapse v-model="activeBookNames" accordion>
              <el-collapse-item 
                v-for="group in bookReservationGroups" 
                :key="group.bookId" 
                :name="group.bookId"
              >
                <template #title>
                  <div class="book-title-row">
                    <span class="book-name">{{ group.bookTitle }}</span>
                    <el-badge :value="group.reservations.length" class="item" type="info" />
                    <el-tag v-if="group.nextInLine" type="success" class="next-badge">
                      📢 下一位: {{ getUserName(group.nextInLine.userId) }}
                    </el-tag>
                  </div>
                </template>
                <el-table :data="group.reservations" style="width: 100%" size="small">
                  <el-table-column prop="queuePosition" label="顺位" width="80" align="center">
                    <template #default="{ row }">
                      <el-tag v-if="row.queuePosition === 1" type="success" effect="dark">
                        第1位
                      </el-tag>
                      <span v-else>第{{ row.queuePosition }}位</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="用户" width="120">
                    <template #default="{ row }">
                      {{ getUserName(row.userId) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="通知状态" width="120" align="center">
                    <template #default="{ row }">
                      <div v-if="row.isNotified" class="status-notified">
                        <el-icon color="#67C23A"><CircleCheck /></el-icon>
                        <span>已通知</span>
                      </div>
                      <div v-else class="status-pending">
                        <el-icon color="#E6A23C"><Clock /></el-icon>
                        <span>待通知</span>
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column label="通知优先级" width="120" align="center">
                    <template #default="{ row }">
                      <el-tag v-if="row.queuePosition === 1 && !row.isNotified" type="danger" effect="dark">
                        立即通知
                      </el-tag>
                      <el-tag v-else-if="row.queuePosition === 1 && row.isNotified" type="success">
                        已发送
                      </el-tag>
                      <el-tag v-else-if="row.queuePosition === 2" type="warning">
                        候补
                      </el-tag>
                      <el-tag v-else type="info">
                        排队中
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="预约日期" width="180">
                    <template #default="{ row }">
                      {{ formatDate(row.reservationDate) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="预计等待" width="100" align="center">
                    <template #default="{ row }">
                      <span v-if="row.queuePosition === 1">图书归还后可借</span>
                      <span v-else>约 {{ row.queuePosition - 1 }} 轮</span>
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, Clock } from '@element-plus/icons-vue'
import { 
  getPopularBooks, getOverdueUsers, getBorrowStatistics, getReservations, getBooks, getUsers, getBorrowRecords
} from '../api'

const stats = ref({})
const popularBooks = ref([])
const overdueUsers = ref([])
const reservations = ref([])
const books = ref([])
const users = ref([])
const borrowRecords = ref([])
const activeBookNames = ref([])

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

const getUrgencyLevel = (record) => {
  const days = getDaysRemaining(record)
  if (days <= 0) return '已逾期'
  if (days <= 3) return '紧急'
  if (days <= 7) return '警告'
  return '正常'
}

const getUrgencyType = (record) => {
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

const reminderStats = computed(() => {
  const records = borrowRecords.value.filter(r => !r.isReturned)
  return {
    total: records.length,
    sent: records.filter(r => r.reminderSent).length,
    pending: records.filter(r => !r.reminderSent && getDaysRemaining(r) <= 7).length
  }
})

const bookReservationGroups = computed(() => {
  const groups = []
  const bookMap = {}
  
  reservations.value.forEach(r => {
    if (!bookMap[r.bookId]) {
      bookMap[r.bookId] = []
    }
    bookMap[r.bookId].push(r)
  })
  
  Object.keys(bookMap).forEach(bookId => {
    const bookReservations = bookMap[bookId]
      .sort((a, b) => a.queuePosition - b.queuePosition)
    
    const nextInLine = bookReservations.find(r => r.queuePosition === 1)
    
    groups.push({
      bookId,
      bookTitle: getBookTitle(bookId),
      reservations: bookReservations,
      nextInLine,
      totalCount: bookReservations.length,
      notifiedCount: bookReservations.filter(r => r.isNotified).length
    })
  })
  
  return groups.sort((a, b) => b.totalCount - a.totalCount)
})

const notificationStats = computed(() => {
  const allReservations = reservations.value
  return {
    total: allReservations.length,
    notified: allReservations.filter(r => r.isNotified).length,
    pending: allReservations.filter(r => !r.isNotified).length,
    nextInLine: allReservations.filter(r => r.queuePosition === 1 && !r.isNotified).length
  }
})

const loadData = async () => {
  try {
    const [popularRes, overdueRes, statsRes, reservationsRes, booksRes, usersRes, recordsRes] = await Promise.all([
      getPopularBooks(), getOverdueUsers(), getBorrowStatistics(), getReservations(), getBooks(), getUsers(), getBorrowRecords()
    ])
    popularBooks.value = popularRes.data.data
    overdueUsers.value = overdueRes.data.data
    stats.value = statsRes.data.data
    reservations.value = reservationsRes.data.data
    books.value = booksRes.data.data
    users.value = usersRes.data.data
    borrowRecords.value = recordsRes.data.data
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
.stat-item {
  text-align: center;
  padding: 20px;
}
.stat-number {
  font-size: 36px;
  font-weight: bold;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.header-stats {
  display: flex;
  gap: 8px;
}

.book-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.book-name {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
}

.next-badge {
  margin-left: auto;
}

.status-notified,
.status-pending {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
}

:deep(.el-collapse-item__header) {
  background-color: #f5f7fa;
  padding: 0 16px;
  border-radius: 4px;
}

:deep(.el-collapse-item__wrap) {
  border: 1px solid #ebeef5;
  border-top: none;
  border-radius: 0 0 4px 4px;
}

:deep(.el-collapse-item__content) {
  padding-bottom: 0;
}
</style>
