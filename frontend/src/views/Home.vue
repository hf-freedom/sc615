<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon :size="30"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalBorrowed || 0 }}</div>
              <div class="stat-label">总借阅量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon :size="30"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.currentBorrowed || 0 }}</div>
              <div class="stat-label">当前借阅</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon :size="30"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.overdueCount || 0 }}</div>
              <div class="stat-label">逾期数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #909399">
              <el-icon :size="30"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.turnoverRate || '0%' }}</div>
              <div class="stat-label">周转率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <el-space direction="vertical" style="width: 100%">
            <el-button type="primary" @click="showBorrowDialog" style="width: 100%">借阅图书</el-button>
            <el-button type="success" @click="showReturnDialog" style="width: 100%">归还图书</el-button>
            <el-button type="warning" @click="showRenewDialog" style="width: 100%">续借图书</el-button>
            <el-button type="info" @click="showReserveDialog" style="width: 100%">预约图书</el-button>
          </el-space>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门图书 Top 5</span>
            </div>
          </template>
          <el-table :data="popularBooks" style="width: 100%" size="small">
            <el-table-column prop="title" label="书名" />
            <el-table-column prop="author" label="作者" />
            <el-table-column prop="borrowCount" label="借阅次数" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <el-dialog 
    v-model="borrowDialogVisible" 
    title="借阅图书" 
    width="500px"
    :modal="false"
    :close-on-click-modal="false"
    draggable
    :top="10vh"
    class="operation-dialog"
  >
    <el-form :model="borrowForm" label-width="100px">
      <el-form-item label="选择用户">
        <el-select v-model="borrowForm.userId" placeholder="请选择用户" style="width: 100%" @change="onUserChange">
          <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
        </el-select>
      </el-form-item>
      <div v-if="borrowForm.userId" class="borrow-check">
        <el-alert 
          :title="borrowCheckMessage" 
          :type="canBorrow ? 'success' : 'error'" 
          :closable="false"
          show-icon
          style="margin-bottom: 15px"
        />
        <el-statistic-group>
          <el-statistic title="当前借阅" :value="userBorrowCount" />
          <el-statistic title="最大限制" :value="userMaxLimit" />
          <el-statistic title="剩余可借" :value="userMaxLimit - userBorrowCount" />
        </el-statistic-group>
      </div>
      <el-form-item label="选择图书">
        <el-select v-model="borrowForm.bookId" placeholder="请选择图书" style="width: 100%" :disabled="!canBorrow">
          <el-option v-for="book in availableBooks" :key="book.id" :label="book.title" :value="book.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="borrowDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitBorrow" :disabled="!canBorrow">确认借阅</el-button>
    </template>
  </el-dialog>

  <el-dialog 
    v-model="returnDialogVisible" 
    title="归还图书" 
    width="500px"
    :modal="false"
    :close-on-click-modal="false"
    draggable
    :top="10vh"
    class="operation-dialog"
  >
    <el-form :model="returnForm" label-width="100px">
      <el-form-item label="选择记录">
        <el-select v-model="returnForm.borrowRecordId" placeholder="请选择借阅记录" style="width: 100%">
          <el-option v-for="record in unreturnedRecords" :key="record.id" 
            :label="getBookTitle(record.bookId) + ' - ' + getUserName(record.userId)" 
            :value="record.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="returnDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitReturn">确认归还</el-button>
    </template>
  </el-dialog>

  <el-dialog 
    v-model="renewDialogVisible" 
    title="续借图书" 
    width="500px"
    :modal="false"
    :close-on-click-modal="false"
    draggable
    :top="10vh"
    class="operation-dialog"
  >
    <el-form :model="renewForm" label-width="100px">
      <el-form-item label="选择记录">
        <el-select v-model="renewForm.borrowRecordId" placeholder="请选择借阅记录" style="width: 100%">
          <el-option v-for="record in renewableRecords" :key="record.id" 
            :label="getBookTitle(record.bookId) + ' - ' + getUserName(record.userId)" 
            :value="record.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="renewDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitRenew">确认续借</el-button>
    </template>
  </el-dialog>

  <el-dialog 
    v-model="reserveDialogVisible" 
    title="预约图书" 
    width="500px"
    :modal="false"
    :close-on-click-modal="false"
    draggable
    :top="10vh"
    class="operation-dialog"
  >
    <el-form :model="reserveForm" label-width="100px">
      <el-form-item label="选择用户">
        <el-select v-model="reserveForm.userId" placeholder="请选择用户" style="width: 100%">
          <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="选择图书">
        <el-select v-model="reserveForm.bookId" placeholder="请选择图书" style="width: 100%">
          <el-option v-for="book in unavailableBooks" :key="book.id" :label="book.title" :value="book.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="reserveDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitReserve">确认预约</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Reading, Document, Warning, TrendCharts } from '@element-plus/icons-vue'
import { 
  getBooks, getUsers, getBorrowRecords, getBorrowStatistics, getPopularBooks,
  borrowBook, returnBook, renewBook, reserveBook
} from '../api'

const stats = ref({})
const popularBooks = ref([])
const books = ref([])
const users = ref([])
const borrowRecords = ref([])

const borrowDialogVisible = ref(false)
const returnDialogVisible = ref(false)
const renewDialogVisible = ref(false)
const reserveDialogVisible = ref(false)

const borrowForm = ref({ userId: '', bookId: '' })
const returnForm = ref({ borrowRecordId: '' })
const renewForm = ref({ borrowRecordId: '' })
const reserveForm = ref({ userId: '', bookId: '' })

const availableBooks = computed(() => books.value.filter(b => b.availableQuantity > 0))
const unavailableBooks = computed(() => books.value.filter(b => b.availableQuantity <= 0))
const unreturnedRecords = computed(() => borrowRecords.value.filter(r => !r.isReturned))
const renewableRecords = computed(() => borrowRecords.value.filter(r => !r.isReturned && !r.isOverdue))

const userBorrowCount = computed(() => {
  if (!borrowForm.value.userId) return 0
  return borrowRecords.value.filter(r => r.userId === borrowForm.value.userId && !r.isReturned).length
})

const userMaxLimit = computed(() => {
  if (!borrowForm.value.userId) return 5
  const user = users.value.find(u => u.id === borrowForm.value.userId)
  return user ? user.maxBorrowLimit : 5
})

const canBorrow = computed(() => {
  if (!borrowForm.value.userId) return true
  const user = users.value.find(u => u.id === borrowForm.value.userId)
  if (!user) return false
  if (!user.canBorrow) return false
  return userBorrowCount.value < userMaxLimit.value
})

const borrowCheckMessage = computed(() => {
  if (!borrowForm.value.userId) return ''
  const user = users.value.find(u => u.id === borrowForm.value.userId)
  if (!user) return '用户不存在'
  if (!user.canBorrow) return '该用户因逾期罚分过高，暂时无法借阅新书'
  if (userBorrowCount.value >= userMaxLimit.value) {
    return `已达到最大借阅数量限制（${userMaxLimit.value}本），请先归还图书后再借阅`
  }
  return `可以借阅，当前借阅 ${userBorrowCount.value}/${userMaxLimit.value} 本`
})

const onUserChange = () => {
  borrowForm.value.bookId = ''
}

const getBookTitle = (bookId) => {
  const book = books.value.find(b => b.id === bookId)
  return book ? book.title : bookId
}

const getUserName = (userId) => {
  const user = users.value.find(u => u.id === userId)
  return user ? user.name : userId
}

const loadData = async () => {
  try {
    const [booksRes, usersRes, recordsRes, statsRes, popularRes] = await Promise.all([
      getBooks(), getUsers(), getBorrowRecords(), getBorrowStatistics(), getPopularBooks()
    ])
    books.value = booksRes.data.data
    users.value = usersRes.data.data
    borrowRecords.value = recordsRes.data.data
    stats.value = statsRes.data.data
    popularBooks.value = popularRes.data.data.slice(0, 5)
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const showBorrowDialog = () => {
  borrowForm.value = { userId: '', bookId: '' }
  borrowDialogVisible.value = true
}

const showReturnDialog = () => {
  returnForm.value = { borrowRecordId: '' }
  returnDialogVisible.value = true
}

const showRenewDialog = () => {
  renewForm.value = { borrowRecordId: '' }
  renewDialogVisible.value = true
}

const showReserveDialog = () => {
  reserveForm.value = { userId: '', bookId: '' }
  reserveDialogVisible.value = true
}

const submitBorrow = async () => {
  if (!borrowForm.value.userId || !borrowForm.value.bookId) {
    ElMessage.warning('请选择用户和图书')
    return
  }
  try {
    const res = await borrowBook(borrowForm.value)
    if (res.data.code === 200) {
      ElMessage.success('借阅成功')
      borrowDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const submitReturn = async () => {
  if (!returnForm.value.borrowRecordId) {
    ElMessage.warning('请选择借阅记录')
    return
  }
  try {
    const res = await returnBook(returnForm.value)
    if (res.data.code === 200) {
      ElMessage.success('归还成功')
      returnDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const submitRenew = async () => {
  if (!renewForm.value.borrowRecordId) {
    ElMessage.warning('请选择借阅记录')
    return
  }
  try {
    const res = await renewBook(renewForm.value)
    if (res.data.code === 200) {
      ElMessage.success('续借成功')
      renewDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const submitReserve = async () => {
  if (!reserveForm.value.userId || !reserveForm.value.bookId) {
    ElMessage.warning('请选择用户和图书')
    return
  }
  try {
    const res = await reserveBook(reserveForm.value)
    if (res.data.code === 200) {
      ElMessage.success('预约成功')
      reserveDialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-around;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}
.stat-info {
  text-align: right;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.borrow-check {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 15px;
}

.borrow-check .el-statistic-group {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
}

.borrow-check .el-statistic {
  text-align: center;
}

.borrow-check .el-statistic .el-statistic__head {
  font-size: 13px;
  color: #909399;
}

.borrow-check .el-statistic .el-statistic__content {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

:deep(.operation-dialog) {
  z-index: 100 !important;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

:deep(.operation-dialog .el-dialog__header) {
  cursor: move;
  user-select: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 4px 4px 0 0;
}

:deep(.operation-dialog .el-dialog__title) {
  color: white;
}

:deep(.operation-dialog .el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

:deep(.operation-dialog .el-dialog__headerbtn:hover .el-dialog__close) {
  color: #e0e0e0;
}
</style>
