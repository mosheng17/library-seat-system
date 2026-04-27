# 阶段1：构建 - 用带 Maven 的镜像打包项目
FROM maven:3.8.8-openjdk-17 AS build
WORKDIR /app

# 先复制 pom.xml 下载依赖（利用 Docker 缓存，加速构建）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包
COPY src ./src
RUN mvn clean package -DskipTests

# 阶段2：运行 - 用轻量 JDK 镜像运行 jar 包
FROM openjdk:17-jdk-slim
WORKDIR /app

# 从构建阶段复制打好的 jar 包
COPY --from=build /app/target/*.jar app.jar

# 添加健康检查
HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD wget -qO- http://localhost:8080/api/health || exit 1

# 暴露端口（Railway 会自动映射 PORT 环境变量）
EXPOSE 8080

# 启动命令，读取 Railway 分配的端口
ENTRYPOINT ["java", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]