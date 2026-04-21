<template>
  <section class="panel form-card">
    <h2>登录页面</h2>
    <el-form label-width="80px" @submit.prevent>
      <el-form-item label="用户名">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input
          v-model="form.password"
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

    <div class="result-box">
      <p>测试账号：</p>
      <p>学生：`student01 / 123456`</p>
      <p>管理员：`admin01 / 123456`</p>
    </div>

    <div v-if="userInfo" class="result-box">
      <p>登录成功</p>
      <p>用户 ID：{{ userInfo.userId }}</p>
      <p>用户名：{{ userInfo.username }}</p>
      <p>姓名：{{ userInfo.realName }}</p>
      <p>角色：{{ userInfo.role }}</p>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const loading = ref(false)
const userInfo = ref(null)
const form = reactive({
  username: '',
  password: ''
})

async function handleLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请先输入用户名和密码')
    return
  }

  loading.value = true

  try {
    const { data } = await login(form)
    userInfo.value = data.data
    sessionStorage.setItem('loginUser', JSON.stringify(data.data))
    ElMessage.success(data.message || '登录成功')
  } catch (error) {
    const message = error.response?.data?.message || '登录失败，请检查后端服务'
    ElMessage.error(message)
  } finally {
    loading.value = false
  }
}
</script>
