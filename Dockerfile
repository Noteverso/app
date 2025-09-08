# 第 1 阶段：构建 Spring Boot 应用
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY . .

# 运行 Maven 构建 Spring Boot 可执行 jar
RUN mvn clean package -DskipTests

# 第 2 阶段：使用轻量级 JRE 运行
FROM eclipse-temurin:17-jre

# 设置时区（可选）
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime

# 创建工作目录
WORKDIR /app

# 复制构建好的 jar 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 运行 Spring Boot 应用
ENTRYPOINT ["java", "-jar", "app.jar"]
