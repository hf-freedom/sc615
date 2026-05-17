import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

export const borrowBook = (data) => api.post('/borrow', data)
export const returnBook = (data) => api.post('/return', data)
export const renewBook = (data) => api.post('/renew', data)
export const reserveBook = (data) => api.post('/reserve', data)

export const getBooks = () => api.get('/books')
export const getUsers = () => api.get('/users')
export const getBorrowRecords = () => api.get('/borrow-records')
export const getReservations = () => api.get('/reservations')

export const getUserBorrowRecords = (userId) => api.get(`/user/${userId}/borrow-records`)
export const getBookReservations = (bookId) => api.get(`/book/${bookId}/reservations`)

export const getPopularBooks = () => api.get('/statistics/popular-books')
export const getOverdueUsers = () => api.get('/statistics/overdue-users')
export const getBorrowStatistics = () => api.get('/statistics/borrow')

export default api
