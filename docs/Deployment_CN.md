# 部署指南

> **中文 | [English](Deployment.md)**

---

## 1. JAR 包部署（原生 Java）

### 前提条件

- Java 17 JDK
- MySQL/PostgreSQL 数据库
- Redis 服务器
- GeoLite2-City.mmdb（MaxMind IP 地理定位数据库）

### 操作步骤

#### 1. 从 GitHub 下载发布 JAR 包

[CertVault 发布版本](https://github.com/gregPerlinLi/CertVault/releases)

#### 2. 准备配置文件

```bash
wget https://raw.githubusercontent.com/gregPerlinLi/CertVault/main/application.yml.example
cp application.yml.example application.yml
nano application.yml # 配置数据库和 Redis 参数
```

#### 3. 作为 systemd 服务部署（Linux）

```bash
wget https://raw.githubusercontent.com/gregPerlinLi/CertVault/main/certvault.service
nano certvault.service # 修改服务名称等参数
sudo cp certvault.service /etc/systemd/system/
sudo systemctl --daemon-reload
sudo systemctl enable --now certvault
```

#### 4. 验证部署

```bash
curl -I http://localhost:1888/api/v1/
```

### 配置参数示例

```yaml
spring:
  datasource:
    url: jdbc:postgresql://127.0.0.1:5432/cert_vault?sslmode=disable
    username: root
    password: changeme
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: changeme
  sql:
    init:
      platform: postgresql
```
---

## 2. Docker 部署

### 配置选项

- MySQL + Redis: `docker-compose-with-mysql-redis.yml`
- PostgreSQL + Redis: `docker-compose-with-postgres-redis.yml`
- 外部数据库 + Redis: 使用 `docker-compose-with-redis-external-*.yml` 变体
- 内部数据库 + 外部 Redis: 使用 `docker-compose-with-*-external-redis.yml` 变体
- 外部数据库和 Redis: 使用 `docker-compose-with-external-*-redis.yml` 变体

### 操作步骤（以内置 PostgreSQL 和 Redis 为例）

#### 1. 克隆仓库

```bash
git clone https://github.com/gregPerlinLi/CertVault.git
cd CertVault/docker-compose
```

#### 2. 配置环境

```
bash
nano .env # 设置环境变量
nano application.yml # 配置详细参数（如 OIDC）
```
#### 3. 启动服务

```bash
docker-compose -f docker-compose-with-postgres-redis.yml up -d
```
#### 4. 验证部署

```bash
docker ps | grep cert-vault
curl -I http://localhost:1888/api/v1/
```

### 关键配置文件

- `docker-compose-with-postgres-redis.yml`（默认）
- `GeoLite2-City.mmdb`（IP 地理定位必需）
- `application.yml`（其他详细配置）

---

## 3. Helm Chart 部署（Kubernetes）

### 前提条件

- Kubernetes 1.20+
- Helm 3.0+
- Persistent Volume 支持

### 操作步骤

#### 1. 添加 Helm 仓库

```bash
helm repo add certvault https://gregperlinli.github.io/certvault-charts
```

#### 2. 下载 Chart 包

```bash
helm pull certvault/certvault --untar
```

#### 3. 修改配置

```bash
cd certvault
nano values.yaml
```

#### 4. 部署

```bash
helm --namespace certvault \
--create-namespace \
upgrade --install \
certvault .
```

#### 5. 验证部署

```bash
curl -I http://svc-ip:1888/api/v1/
```
---

## 4. 源码编译部署

### 前提条件

- JDK 17
- Node.js 18+
- Maven 3.8+
- NPM & PNPM 包管理器

### 操作步骤

#### 1. 克隆仓库

```bash
git clone https://github.com/gregPerlinLi/CertVault.git
cd CertVault
```

#### 2. 构建项目

```bash
make
```

#### 3. 部署

```bash
make install
```

---

## 通用环境参数

| 参数名称                  | 说明                          | 示例值                                                                     |
|--------------------------|-----------------------------|----------------------------------------------------------------------------|
| `DATABASE_TYPE`          | 数据库类型（mysql/postgresql） | `DATABASE_TYPE=postgresql`                                                |
| `DATABASE_URL`           | 数据库连接 URL                | `DATABASE_URL=jdbc:postgresql://127.0.0.1:5432/cert_vault?sslmode=disable`|
| `SUPERADMIN_PASSWORD`    | 初始超级管理员密码              | `SUPERADMIN_PASSWORD=admin123`                                            |
| `SPRING_PROFILES_ACTIVE` | 激活的环境配置文件              | `prod`/`dev`                                                              |
| `SPRING_SERVER_PORT`     | 服务监听端口                   | `SPRING_SERVER_PORT=1888`                                                 |

## 验证部署

部署完成后访问：

- 管理界面：`http://<host>:1888`
- 健康检查：`http://<host>:1999/actuator/health`
- Swagger API 文档：`http://<host>:1888/developer-api`

## 常见问题排查

- 数据库连接问题：检查环境变量和 `application.yml` 配置
- Docker 问题：检查 `docker-compose` 文件和 `application.yml`
- Helm 问题：执行 `helm -n certvault ls` 查看部署状态
- 日志获取方法：
  - JAR 包/源码部署：`journalctl -xeu certvault.service -f`
  - Docker 部署：`docker-compose logs -f`
  - Helm 部署：`kubectl -n certvault logs -f deployments/cert-vault`
