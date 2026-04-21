<template>
  <section class="panel">
    <h2>座位预约</h2>
    <div class="reservation-toolbar">
      <el-select
        v-model="selectedRoomId"
        placeholder="请选择自习室"
        style="width: 260px"
        @change="handleRoomChange"
      >
        <el-option
          v-for="room in rooms"
          :key="room.id"
          :label="`${room.roomName} | ${room.floor}楼 | 容量${room.capacity}`"
          :value="room.id"
        />
      </el-select>
      <el-button @click="loadRooms">刷新自习室</el-button>
    </div>

    <div v-if="currentRoom" class="result-box">
      <p>当前自习室：{{ currentRoom.roomName }}</p>
      <p>楼层：{{ currentRoom.floor }} 楼</p>
      <p>容量：{{ currentRoom.capacity }}</p>
    </div>

    <div class="reservation-form">
      <el-date-picker
        v-model="reservationRange"
        type="datetimerange"
        range-separator="至"
        start-placeholder="开始时间"
        end-placeholder="结束时间"
        format="YYYY-MM-DD HH:mm"
        value-format="YYYY-MM-DDTHH:mm:ss"
      />
      <div class="result-box">
        <p>当前登录用户：{{ loginUser ? `${loginUser.realName}（${loginUser.username}）` : '未登录' }}</p>
        <p>请先登录，再选择空闲座位和预约时间。</p>
      </div>
    </div>

    <div v-loading="loading" class="seat-grid">
      <div
        v-for="seat in seats"
        :key="seat.id"
        class="seat-card"
        :class="statusClassMap[seat.status] || 'status-default'"
      >
        <h3>{{ seat.seatCode }}</h3>
        <p>{{ seat.roomName }}</p>
        <span>{{ statusTextMap[seat.status] || seat.status }}</span>
        <el-button
          v-if="seat.status === 'AVAILABLE'"
          type="primary"
          plain
          class="seat-action"
          @click="handleReserve(seat)"
        >
          预约该座位
        </el-button>
      </div>
    </div>

    <div v-if="!loading && seats.length === 0" class="result-box">
      当前自习室还没有座位数据。
    </div>

    <div class="reservation-list">
      <h3>我的预约</h3>
      <div v-if="userReservations.length === 0" class="result-box">
        当前还没有预约记录。
      </div>
      <div v-else class="reservation-records">
        <div
          v-for="item in userReservations"
          :key="item.id"
          class="record-card"
        >
          <p>自习室：{{ item.roomName }}</p>
          <p>座位：{{ item.seatCode }}</p>
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
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createReservation, getReservationsByUser, cancelReservation } from '../api/reservation'
import { getSeatsByRoom, getStudyRooms } from '../api/seat'

const loading = ref(false)
const rooms = ref([])
const seats = ref([])
const selectedRoomId = ref(null)
const reservationRange = ref([])
const userReservations = ref([])
const loginUser = ref(readLoginUser())

const statusTextMap = {
  AVAILABLE: '空闲',
  RESERVED: '已预约',
  IN_USE: '使用中'
}

const reservationStatusTextMap = {
  RESERVED: '已预约',
  CANCELLED: '已取消'
}

const statusClassMap = {
  AVAILABLE: 'status-available',
  RESERVED: 'status-reserved',
  IN_USE: 'status-in-use'
}

const currentRoom = computed(() =>
  rooms.value.find(room => room.id === selectedRoomId.value) || null
)

function readLoginUser() {
  const value = sessionStorage.getItem('loginUser')
  return value ? JSON.parse(value) : null
}

async function loadRooms() {
  try {
    const { data } = await getStudyRooms()
    rooms.value = data.data || []

    if (rooms.value.length > 0 && !selectedRoomId.value) {
      selectedRoomId.value = rooms.value[0].id
      await loadSeats(selectedRoomId.value)
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载自习室失败')
  }
}

async function loadSeats(roomId) {
  if (!roomId) {
    seats.value = []
    return
  }

  loading.value = true

  try {
    const { data } = await getSeatsByRoom(roomId)
    seats.value = data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载座位失败')
  } finally {
    loading.value = false
  }
}

function handleRoomChange(roomId) {
  loadSeats(roomId)
}

async function loadUserReservations() {
  if (!loginUser.value?.userId) {
    userReservations.value = []
    return
  }

  try {
    const { data } = await getReservationsByUser(loginUser.value.userId)
    userReservations.value = data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载预约记录失败')
  }
}

async function handleReserve(seat) {
  if (!loginUser.value?.userId) {
    ElMessage.warning('请先到登录页面完成登录')
    return
  }

  if (!reservationRange.value || reservationRange.value.length !== 2) {
    ElMessage.warning('请先选择预约时间段')
    return
  }

  try {
    await createReservation({
      userId: loginUser.value.userId,
      seatId: seat.id,
      startTime: reservationRange.value[0],
      endTime: reservationRange.value[1]
    })
    ElMessage.success(`已成功预约座位 ${seat.seatCode}`)
    await loadSeats(selectedRoomId.value)
    await loadUserReservations()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '预约失败')
  }
}

async function handleCancel(reservationId) {
  try {
    await cancelReservation(reservationId)
    ElMessage.success('取消预约成功')
    await loadSeats(selectedRoomId.value)
    await loadUserReservations()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '取消预约失败')
  }
}

function formatDateTime(value) {
  return value ? value.replace('T', ' ') : ''
}

onMounted(() => {
  loadRooms()
  loadUserReservations()
})
</script>
