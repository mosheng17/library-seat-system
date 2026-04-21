import request from './request'

export function createReservation(data) {
  return request.post('/reservations', data)
}

export function getReservationsByUser(userId) {
  return request.get(`/reservations/user/${userId}`)
}

export function cancelReservation(reservationId) {
  return request.delete(`/reservations/${reservationId}`)
}

