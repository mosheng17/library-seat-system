<template>
  <section class="panel">
    <h2>系统首页</h2>
    <section class="home-login-card">
      <h3>{{ loginUser ? '当前账号' : activeTab === 'login' ? '账号登录' : '学生注册' }}</h3>
      <p v-if="loginUser" class="home-login-text">
        已登录为 {{ loginUser.realName }}（{{ loginUser.username }}），角色：{{ roleTextMap[loginUser.role] || loginUser.role }}
      </p>
      <template v-else>
        <div class="home-auth-switch">
          <button
            class="auth-switch-btn"
            :class="{ active: activeTab === 'login' }"
            @click="activeTab = 'login'"
          >
            登录
          </button>
          <button
            class="auth-switch-btn"
            :class="{ active: activeTab === 'register' }"
            @click="activeTab = 'register'"
          >
            注册
          </button>
        </div>

        <el-form v-if="activeTab === 'login'" label-width="80px" class="home-login-form" @submit.prevent>
          <el-form-item label="用户名">
            <el-input v-model="loginForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="loginForm.password"
              type="password"
              show-password
              placeholder="请输入密码"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleLogin">
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <el-form v-else label-width="80px" class="home-login-form" @submit.prevent>
          <el-form-item label="姓名">
            <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="用户名">
            <el-input v-model="registerForm.username" placeholder="请输入注册用户名" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input
              v-model="registerForm.password"
              type="password"
              show-password
              placeholder="请输入注册密码"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="handleRegister">
              注册并登录
            </el-button>
          </el-form-item>
        </el-form>
      </template>
    </section>

    <div class="grid">
      <div v-if="!loginUser || loginUser.role === 'STUDENT'">
        <h3>学生端功能</h3>
        <p>登录、查看空闲座位、预约、取消预约、查看个人记录和预约状态。</p>
      </div>
      <div v-if="!loginUser || loginUser.role === 'ADMIN'">
        <h3>管理员功能</h3>
        <p>维护自习室、维护座位、查看预约情况、处理异常记录。</p>
      </div>
      <div>
        <h3>当前状态</h3>
        <p>系统已具备登录、座位查询、预约、取消预约、管理员管理等基础能力。</p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { login, register } from '../api/auth'

const loading = ref(false)
const loginUser = ref(readLoginUser())
const activeTab = ref('login')
const loginForm = reactive({
  username: '',
  password: ''
})
const registerForm = reactive({
  realName: '',
  username: '',
  password: ''
})

const roleTextMap = {
  STUDENT: '学生',
  ADMIN: '管理员'
}

function readLoginUser() {
  const value = sessionStorage.getItem('loginUser')
  return value ? JSON.parse(value) : null
}

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请先输入用户名和密码')
    return
  }

  loading.value = true

  try {
    const { data } = await login(loginForm)
    sessionStorage.setItem('loginUser', JSON.stringify(data.data))
    loginUser.value = data.data
    loginForm.username = ''
    loginForm.password = ''
    window.dispatchEvent(new Event('login-user-changed'))
    ElMessage.success(data.message || '登录成功')
  } catch (error) {
    const message = error.response?.data?.message || '登录失败，请检查后端服务'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.realName || !registerForm.username || !registerForm.password) {
    ElMessage.warning('请先完整填写注册信息')
    return
  }

  loading.value = true

  try {
    const { data } = await register(registerForm)
    sessionStorage.setItem('loginUser', JSON.stringify(data.data))
    loginUser.value = data.data
    registerForm.realName = ''
    registerForm.username = ''
    registerForm.password = ''
    activeTab.value = 'login'
    window.dispatchEvent(new Event('login-user-changed'))
    ElMessage.success(data.message || '注册成功')
  } catch (error) {
    const message = error.response?.data?.message || '注册失败，请稍后重试'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}
</script>
