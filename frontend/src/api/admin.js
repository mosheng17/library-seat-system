import request from './request'

export function getAdminRooms() {
  return request.get('/admin/rooms')
}

export function createRoom(data) {
  return request.post('/admin/rooms', data)
}

export function getAdminSeatsByRoom(roomId) {
  return request.get(`/admin/rooms/${roomId}/seats`)
}

export function createSeat(data) {
  return request.post('/admin/seats', data)
}

export function getAllReservations() {
  return request.get('/admin/reservations')
}

