import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import ReservationView from '../views/ReservationView.vue'
import AdminView from '../views/AdminView.vue'
import UserRecordsView from '../views/UserRecordsView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/login', component: LoginView },
  { path: '/reservation', component: ReservationView },
  { path: '/records', component: UserRecordsView },
  { path: '/admin', component: AdminView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
