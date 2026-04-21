<template>
  <div class="app-shell">
    <header class="top-bar">
      <h1>图书馆自习室座位预约系统</h1>
      <nav class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link v-if="!loginUser" to="/">登录</router-link>
        <router-link v-if="loginUser?.role === 'STUDENT'" to="/reservation">预约</router-link>
        <router-link v-if="loginUser?.role === 'STUDENT'" to="/records">我的预约</router-link>
        <router-link v-if="loginUser?.role === 'ADMIN'" to="/admin">管理</router-link>
        <button v-if="loginUser" class="nav-logout" @click="handleLogout">退出登录</button>
      </nav>
    </header>
    <main class="page-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loginUser = ref(readLoginUser())

function readLoginUser() {
  const value = sessionStorage.getItem('loginUser')
  return value ? JSON.parse(value) : null
}

function syncLoginUser() {
  loginUser.value = readLoginUser()
}

function handleLogout() {
  sessionStorage.removeItem('loginUser')
  syncLoginUser()
  window.dispatchEvent(new Event('login-user-changed'))
  router.push('/')
}

onMounted(() => {
  window.addEventListener('focus', syncLoginUser)
  window.addEventListener('login-user-changed', syncLoginUser)
})

onUnmounted(() => {
  window.removeEventListener('focus', syncLoginUser)
  window.removeEventListener('login-user-changed', syncLoginUser)
})
</script>
