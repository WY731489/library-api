# Docker 部署说明

## 一次性部署流程

### 1. 安装 Docker Desktop
- Windows：https://www.docker.com/products/docker-desktop
- 安装完成后启动 Docker Desktop，等待右下角图标变绿（约 30 秒）
- 验证：终端执行 `docker --version`，能看到版本号即装好

### 2. 一键启动
在项目根目录 `e:\Lb` 下打开终端：
```bash
docker compose up --build
```

第一次会做这些事：
1. 下载 MySQL 8.0 镜像（~150MB）
2. 用 Maven 构建项目 jar（~2 分钟，下载依赖）
3. 用 jre-jammy 构建应用镜像（~250MB）
4. 启动 MySQL → 执行 init.sql 建表灌数据 → 健康检查通过
5. 启动应用，监听 8081

总耗时约 3~5 分钟。

### 3. 验证服务
- 浏览器：http://localhost:8081/
- Knife4j 文档：http://localhost:8081/doc.html
- 健康检查：http://localhost:8081/actuator/health

返回 `{"status":"UP"}` 就说明正常。

## 常用命令

```bash
# 后台启动（不占终端）
docker compose up -d --build

# 查看日志
docker compose logs -f app       # 只看应用
docker compose logs -f mysql     # 只看 MySQL

# 停止服务
docker compose stop

# 停止并删除容器（数据保留在卷里）
docker compose down

# 彻底清空（连数据库数据一起删，慎用！）
docker compose down -v
```

## 连接容器里的 MySQL（调试用）

宿主机地址：`localhost:3307`，账号 `root`，密码 `root123456`，库 `book_library`

```bash
docker exec -it library-mysql mysql -uroot -proot123456 book_library
```

## 改代码后重新部署

```bash
# 改 Java 代码后必须重新构建应用镜像
docker compose up -d --build app

# 改 application.yml 或前端 HTML 后（这些在 jar 里，也要重新构建）
docker compose up -d --build app

# 改 init.sql 后（需要清空数据库重新初始化，慎用）
docker compose down -v
docker compose up -d --build
```

## 端口冲突怎么办？

如果本地 8081 或 3307 被占用，改 `docker-compose.yml`：
```yaml
ports:
  - "9090:8081"   # 应用：宿主机 9090 → 容器 8081
  - "3308:3306"   # MySQL：宿主机 3308 → 容器 3306
```

## 环境变量配置（可选）

在项目根目录创建 `.env` 文件覆盖默认值：
```
DB_PASSWORD=MyStrongPwd@2026
```

或者直接在 docker-compose.yml 的 environment 里改。
