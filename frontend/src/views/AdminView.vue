<template>
  <section class="panel">
    <h2>管理员页面</h2>
    <div class="admin-layout">
      <div class="admin-card">
        <h3>新增自习室</h3>
        <el-form label-width="70px" @submit.prevent>
          <el-form-item label="名称">
            <el-input v-model="roomForm.roomName" placeholder="例如 C301自习室" />
          </el-form-item>
          <el-form-item label="楼层">
            <el-input-number v-model="roomForm.floor" :min="1" />
          </el-form-item>
          <el-form-item label="容量">
            <el-input-number v-model="roomForm.capacity" :min="1" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleCreateRoom">新增自习室</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="admin-card">
        <h3>新增座位</h3>
        <el-form label-width="70px" @submit.prevent>
          <el-form-item label="自习室">
            <el-select v-model="seatForm.roomId" placeholder="请选择自习室" style="width: 100%">
              <el-option
                v-for="room in rooms"
                :key="room.id"
                :label="room.roomName"
                :value="room.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="编号">
            <el-input v-model="seatForm.seatCode" placeholder="例如 C301-01" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="seatForm.status" style="width: 100%">
              <el-option label="空闲" value="AVAILABLE" />
              <el-option label="已预约" value="RESERVED" />
              <el-option label="使用中" value="IN_USE" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleCreateSeat">新增座位</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="admin-card">
      <div class="admin-toolbar">
        <h3>自习室与座位</h3>
        <el-select
          v-model="selectedRoomId"
          placeholder="请选择自习室查看座位"
          style="width: 260px"
          @change="loadSeatsByRoom"
        >
          <el-option
            v-for="room in rooms"
            :key="room.id"
            :label="`${room.roomName} | ${room.floor}楼 | 容量${room.capacity}`"
            :value="room.id"
          />
        </el-select>
      </div>

      <div class="reservation-records">
        <div v-for="room in rooms" :key="room.id" class="record-card">
          <p>名称：{{ room.roomName }}</p>
          <p>楼层：{{ room.floor }} 楼</p>
          <p>容量：{{ room.capacity }}</p>
        </div>
      </div>

      <div v-if="adminSeats.length > 0" class="reservation-records">
        <div v-for="seat in adminSeats" :key="seat.id" class="record-card">
          <p>座位：{{ seat.seatCode }}</p>
          <p>自习室：{{ seat.roomName }}</p>
          <p>状态：{{ statusTextMap[seat.status] || seat.status }}</p>
        </div>
      </div>
    </div>

    <div class="admin-card">
      <div class="admin-toolbar">
        <h3>全部预约记录</h3>
        <el-button @click="loadReservations">刷新预约记录</el-button>
      </div>

      <div v-if="reservations.length === 0" class="result-box">
        暂无预约记录。
      </div>

      <div v-else class="reservation-records">
        <div v-for="item in reservations" :key="item.id" class="record-card">
          <p>用户：{{ item.realName }}</p>
          <p>自习室：{{ item.roomName }}</p>
          <p>座位：{{ item.seatCode }}</p>
          <p>时间：{{ formatDateTime(item.startTime) }} - {{ formatDateTime(item.endTime) }}</p>
          <p>状态：{{ reservationStatusMap[item.status] || item.status }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createRoom,
  createSeat,
  getAdminRooms,
  getAdminSeatsByRoom,
  getAllReservations
} from '../api/admin'

const rooms = ref([])
const reservations = ref([])
const adminSeats = ref([])
const selectedRoomId = ref(null)

const roomForm = reactive({
  roomName: '',
  floor: 1,
  capacity: 40
})

const seatForm = reactive({
  roomId: null,
  seatCode: '',
  status: 'AVAILABLE'
})

const statusTextMap = {
  AVAILABLE: '空闲',
  RESERVED: '已预约',
  IN_USE: '使用中'
}

const reservationStatusMap = {
  RESERVED: '已预约',
  CANCELLED: '已取消'
}

async function loadRooms() {
  try {
    const { data } = await getAdminRooms()
    rooms.value = data.data || []
    if (!selectedRoomId.value && rooms.value.length > 0) {
      selectedRoomId.value = rooms.value[0].id
      seatForm.roomId = rooms.value[0].id
      await loadSeatsByRoom(selectedRoomId.value)
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载自习室失败')
  }
}

async function loadSeatsByRoom(roomId = selectedRoomId.value) {
  if (!roomId) {
    adminSeats.value = []
    return
  }

  try {
    const { data } = await getAdminSeatsByRoom(roomId)
    adminSeats.value = data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载座位失败')
  }
}

async function loadReservations() {
  try {
    const { data } = await getAllReservations()
    reservations.value = data.data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载预约记录失败')
  }
}

async function handleCreateRoom() {
  try {
    await createRoom(roomForm)
    ElMessage.success('新增自习室成功')
    roomForm.roomName = ''
    roomForm.floor = 1
    roomForm.capacity = 40
    await loadRooms()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '新增自习室失败')
  }
}

async function handleCreateSeat() {
  try {
    await createSeat(seatForm)
    ElMessage.success('新增座位成功')
    seatForm.seatCode = ''
    await loadSeatsByRoom(seatForm.roomId)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '新增座位失败')
  }
}

function formatDateTime(value) {
  return value ? value.replace('T', ' ') : ''
}

onMounted(async () => {
  await loadRooms()
  await loadReservations()
})
</script>
