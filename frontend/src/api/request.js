import axios from 'axios'

// 根据环境变量或当前域名动态设置 API 地址
const getBaseURL = () => {
  // 生产环境：使用相对路径（前后端同域）或通过环境变量配置
  if (import.meta.env.VITE_API_URL) {
    return import.meta.env.VITE_API_URL
  }
  // 开发环境：代理到本地后端
  if (import.meta.env.DEV) {
    return '/api'
  }
  // 生产环境默认：相对路径
  return '/api'
}

const request = axios.create({
  baseURL: getBaseURL(),
  timeout: 5000
})

export default request

