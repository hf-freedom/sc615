import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Books from '../views/Books.vue'
import Users from '../views/Users.vue'
import BorrowRecords from '../views/BorrowRecords.vue'
import Statistics from '../views/Statistics.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/books', name: 'Books', component: Books },
  { path: '/users', name: 'Users', component: Users },
  { path: '/borrow-records', name: 'BorrowRecords', component: BorrowRecords },
  { path: '/statistics', name: 'Statistics', component: Statistics }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
