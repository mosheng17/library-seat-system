import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ReservationView from '../views/ReservationView.vue'
import AdminView from '../views/AdminView.vue'
import UserRecordsView from '../views/UserRecordsView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', redirect: '/' },
  { path: '/reservation', component: ReservationView, meta: { requiresAuth: true, role: 'STUDENT' } },
  { path: '/records', component: UserRecordsView, meta: { requiresAuth: true, role: 'STUDENT' } },
  { path: '/admin', component: AdminView, meta: { requiresAuth: true, role: 'ADMIN' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const loginUser = sessionStorage.getItem('loginUser')
  const user = loginUser ? JSON.parse(loginUser) : null

  if (to.meta.requiresAuth && !user) {
    return '/'
  }

  if (to.meta.role && user?.role !== to.meta.role) {
    return '/'
  }

  return true
})

export default router
