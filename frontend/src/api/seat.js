import request from './request'

export function getStudyRooms() {
  return request.get('/rooms')
}

export function getSeatsByRoom(roomId) {
  return request.get(`/seats/room/${roomId}`)
}
