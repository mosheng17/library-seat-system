import request from './request'

export function getSeatsByRoom(roomId) {
  return request.get(`/seats/room/${roomId}`)
}

export function createReservation(data) {
  return request.post('/reservations', data)
}

