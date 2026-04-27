import axios from 'axios'

const getBaseURL = () => {
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL
  }

  if (import.meta.env.VITE_API_URL) {
    return import.meta.env.VITE_API_URL
  }

  if (import.meta.env.DEV) {
    return '/api'
  }

  return '/api'
}

const request = axios.create({
  baseURL: getBaseURL(),
  timeout: 5000
})

export default request
