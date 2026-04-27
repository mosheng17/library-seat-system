# 云端部署说明

这个项目是前后端分离架构：

- 前端：Vue 3 + Vite
- 后端：Spring Boot 3
- 数据库：MySQL 8

## 部署前必须知道的事

当前仓库已经调整为可通过环境变量部署：

- 后端端口：`PORT`
- 数据库地址：`SPRING_DATASOURCE_URL`
- 数据库账号：`SPRING_DATASOURCE_USERNAME`
- 数据库密码：`SPRING_DATASOURCE_PASSWORD`
- 前端接口地址：`VITE_API_BASE_URL`
- 后端跨域白名单：`APP_CORS_ALLOWED_ORIGINS`

如果前端和后端最终挂在同一域名下，推荐：

- 前端域名：`https://your-domain.com`
- 后端接口：`https://api.your-domain.com/api`
- 前端环境变量：`VITE_API_BASE_URL=https://api.your-domain.com/api`
- 后端环境变量：`APP_CORS_ALLOWED_ORIGINS=https://your-domain.com`

## 方案一：云主机部署

适合华为云 / 阿里云 / 腾讯云 / AWS ECS。

### 1. 准备服务器

- 系统推荐：Ubuntu 22.04
- 安装：`git`、`docker`、`docker compose`
- 开放端口：`80`、`443`、`8080`（如果前面有反向代理，可不直接开放 8080）

### 2. 准备数据库

有两种做法：

- 直接买云数据库 MySQL
- 先用 Docker 在云主机里运行 MySQL

建议生产环境优先使用云数据库。

### 3. 部署后端

进入后端目录构建镜像：

```bash
cd backend
docker build -t library-seat-backend .
```

运行：

```bash
docker run -d \
  --name library-seat-backend \
  -p 8080:8080 \
  -e PORT=8080 \
  -e SPRING_DATASOURCE_URL='jdbc:mysql://<mysql-host>:3306/library_seat_system?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8' \
  -e SPRING_DATASOURCE_USERNAME='<mysql-user>' \
  -e SPRING_DATASOURCE_PASSWORD='<mysql-password>' \
  -e APP_CORS_ALLOWED_ORIGINS='https://your-domain.com' \
  library-seat-backend
```

### 4. 部署前端

前端构建时需要写入接口地址：

```bash
cd frontend
docker build -t library-seat-frontend --build-arg VITE_API_BASE_URL='https://api.your-domain.com/api' .
docker run -d --name library-seat-frontend -p 80:80 library-seat-frontend
```

注意：当前 `frontend/Dockerfile` 是静态托管版，最适合放在 Nginx、对象存储静态站点或前端托管平台。

### 5. 配置反向代理

推荐使用 Nginx：

- `https://your-domain.com` 指向前端
- `https://api.your-domain.com` 反向代理到 `http://127.0.0.1:8080`

## 方案二：Railway / Render / Heroku 类 PaaS

最省事，适合课程项目和演示。

### 拆分方式

- 一个服务部署 `backend`
- 一个静态站点部署 `frontend`
- 一个托管 MySQL 数据库

### 后端部署

- Root Directory 选 `backend`
- 构建命令：`mvn clean package -DskipTests`
- 启动命令：`java -jar target/*.jar`

环境变量：

```text
PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://<host>:3306/library_seat_system?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8
SPRING_DATASOURCE_USERNAME=<user>
SPRING_DATASOURCE_PASSWORD=<password>
APP_CORS_ALLOWED_ORIGINS=https://your-frontend-domain
```

### 前端部署

- Root Directory 选 `frontend`
- Build Command：`npm ci && npm run build`
- Publish Directory：`dist`

环境变量：

```text
VITE_API_BASE_URL=https://your-backend-domain/api
```

## 方案三：Docker + 云容器托管

适合：

- 阿里云 ACK / SAE
- 腾讯云 TKE
- 华为云 CCE
- AWS ECS / App Runner

思路是：

- 后端直接使用 `backend/Dockerfile`
- 前端直接使用 `frontend/Dockerfile`
- MySQL 用托管数据库，不建议自己在容器里跑生产库

你只需要在平台里分别配置：

- 镜像构建上下文
- 端口
- 环境变量

## 方案四：Serverless

这个项目不太推荐直接做纯 Serverless：

- 前端可以上 Vercel
- 但 Spring Boot 后端不适合直接塞进 Vercel Functions

如果一定要走这一类方案，建议：

- 前端：Vercel
- 后端：Render / Railway / 云函数容器版 / AWS Lambda Container
- 数据库：托管 MySQL

也就是说，前端 Serverless，后端仍然走容器化会更稳。

## 本地先验证一次

可以先在本地用 Docker Compose 验证：

```bash
docker compose up --build
```

启动后：

- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080`
- MySQL：`localhost:3306`

## 生产环境建议

- 不要继续使用明文默认账号密码
- 不要把数据库密码写死进代码
- `show-sql` 生产建议关闭
- `ddl-auto` 生产更建议用 `validate` 或配合 Flyway/Liquibase 管理表结构
- 建议给后端加 HTTPS、日志持久化、健康检查

## 我建议你的最优路线

如果你追求最快上线：

1. 数据库买一个云 MySQL
2. 后端部署到 Railway 或 Render
3. 前端部署到 Vercel / Netlify / Render Static Site
4. 用环境变量把前后端地址连起来

如果你追求更像正式项目：

1. 买云主机
2. 前端用 Nginx
3. 后端用 Docker 跑 Spring Boot
4. 数据库用托管 MySQL
5. 域名 + HTTPS + 反向代理
