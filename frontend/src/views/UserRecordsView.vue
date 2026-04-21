<template>
  <section class="panel">
    <div class="records-header">
      <div>
        <h2>我的预约中心</h2>
        <p class="records-subtitle">
          查看当前用户的预约记录、历史状态和可取消的预约。
        </p>
      </div>
      <el-button @click="loadReservations">刷新记录</el-button>
    </div>

    <div class="records-summary">
      <div class="summary-card">
        <strong>{{ stats.total }}</strong>
        <span>总预约数</span>
      </div>
      <div class="summary-card">
        <strong>{{ stats.active }}</strong>
        <span>有效预约</span>
      </div>
      <div class="summary-card">
        <strong>{{ stats.cancelled }}</strong>
        <span>已取消</span>
      </div>
    </div>

    <div class="result-box">
      <p>当前登录用户：{{ loginUser ? `${loginUser.realName}（${loginUser.username}）` : '未登录' }}</p>
      <p v-if="!loginUser">请先前往登录页完成登录后再查看预约记录。</p>
    </div>

    <el-tabs v-model="activeTab" class="records-tabs">
      <el-tab-pane label="全部记录" name="all" />
      <el-tab-pane label="有效预约" name="active" />
      <el-tab-pane label="已取消" name="cancelled" />
    </el-tabs>

    <div v-if="filteredReservations.length === 0" class="result-box">
      当前筛选条件下没有预约记录。
    </div>

    <div v-else class="reservation-records">
      <div
        v-for="item in filteredReservations"
        :key="item.id"
        class="record-card"
      >
        <p>自习室：{{ item.roomName }}</p>
        <p>座位：{{ item.seatCode }}</p>
        <p>预约人：{{ item.realName }}</p>
        <p>时间：{{ formatDateTime(item.startTime) }} - {{ formatDateTime(item.endTime) }}</p>
        <p>状态：{{ reservationStatusTextMap[item.status] || item.status }}</p>
        <el-button
          v-if="item.status === 'RESERVED'"
          type="danger"
          plain
          @click="handleCancel(item.id)"
        >
          取消预约
        </el-button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { cancelReservation, getReservationsByUser } from '../api/reservation'

const activeTab = ref('all')
const loginUser = ref(readLoginUser())
const reservations = ref([])

const reservationStatusTextMap = {
  RESERVED: '已预约',
  CANCELLED: '已取消'
}

const filteredReservations = computed(() => {
  if (activeTab.value === 'active') {
    return reservations.value.filter(item => item.status === 'RESERVED')
  }

  if (activeTab.value === 'cancelled') {
    return reservations.value.filter(item => item.status === 'CANCELLED')
  }

  return reservations.value
})

const stats = computed(() => ({
  total: reservations.value.length,
  active: reservations.value.filter(item => item.status === 'RESERVED').length,
  cancelled: reservations.value.filter(item => item.status === 'CANCELLED').length
}))

function readLoginUser() {
  const value = sessionStorage.getItem('loginUser')
  return value ? JSON.parse(value) : null
}

async function loadReservations() {
  if (!loginUser.value?.userId) {
    reservations.value = []
    return
  }

  try {
    const { data } = await getReservationsByUser(loginUser.value.userId)
    reservations.value = data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载预约记录失败')
  }
}

async function handleCancel(reservationId) {
  try {
    await cancelReservation(reservationId)
    ElMessage.success('取消预约成功')
    await loadReservations()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '取消预约失败')
  }
}

function formatDateTime(value) {
  return value ? value.replace('T', ' ') : ''
}

onMounted(() => {
  loadReservations()
})
</script>

