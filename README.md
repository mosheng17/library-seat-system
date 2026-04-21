# library-seat-system

图书馆自习室座位预约系统，采用前后端分离结构实现。

## 技术栈

- 前端：Vue 3 + Vite + Element Plus + Axios
- 后端：Spring Boot 3 + Spring Data JPA
- 数据库：MySQL 8
- 构建工具：Maven 3.9+

## 已实现功能

- 用户登录
- 自习室查询
- 座位状态查询
- 创建预约
- 取消预约
- 我的预约中心
- 管理员新增自习室
- 管理员新增座位
- 管理员查看全部预约记录

## 目录结构

```text
library-seat-system
├── backend
├── database
└── frontend
```

## 环境要求

- JDK 17 及以上
- Maven 3.9 及以上
- Node.js 18 及以上
- MySQL 8

## 数据库初始化

1. 创建 MySQL 数据库连接。
2. 执行脚本 [database/01_schema.sql](/Applications/Code%20Of%20VS/library-seat-system/database/01_schema.sql:1)。
3. 默认库名为 `library_seat_system`。

默认测试账号：

- 学生：`student01 / 123456`
- 管理员：`admin01 / 123456`

## 后端启动

后端配置文件在 [backend/src/main/resources/application.yml](/Applications/Code%20Of%20VS/library-seat-system/backend/src/main/resources/application.yml:1)。

默认数据库配置为：

- 地址：`jdbc:mysql://localhost:3306/library_seat_system`
- 用户名：`root`
- 密码：`123456`

如果你的 MySQL 账号或密码不同，先修改 `application.yml` 再启动。

启动命令：

```bash
cd backend
mvn spring-boot:run
```

默认启动端口：

- 后端：`8080`

## 前端启动

启动命令：

```bash
cd frontend
npm install
npm run dev
```

默认启动端口：

- 前端：`5173`

## 主要页面

- `/login`：登录页面
- `/reservation`：座位预约页面
- `/records`：我的预约中心
- `/admin`：管理员页面

## Git 分支建议

- `feature/auth-login`
- `feature/seat-query`
- `feature/reservation-core`
- `feature/admin-management`
- `feature/user-records`
- `feature/project-polish`

## 当前已知说明

- 本项目当前使用明文密码做课程实验演示，不适合直接用于真实生产环境。
- 如果 `mvn test` 失败，先检查本机 Maven 仓库目录 `~/.m2/repository` 是否可写。

