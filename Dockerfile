# ====== 第一阶段：用 Maven 构建项目 ======
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 先只拷 pom.xml，提前下载依赖（利用 Docker 层缓存，改代码不用重下依赖）
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# 拷源码，构建 jar
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ====== 第二阶段：运行时镜像（更小） ======
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 拷构建好的 jar
COPY --from=builder /build/target/*.jar app.jar

# 容器内暴露端口
EXPOSE 8081

# 健康检查：通过 actuator 的 health endpoint（运行时镜像默认带 wget）
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=5 \
  CMD wget -qO- http://localhost:8081/actuator/health | grep -q '"UP"' || exit 1

# 启动命令（支持 JAVA_OPTS 环境变量设置内存参数）
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
