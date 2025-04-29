# Deployment Guide

> **[中文](Deployment_CN.md) | English**

---

## 1. JAR Package Deployment (Java Native)

### Prerequisites

- Java 17 JDK
- MySQL/PostgreSQL database
- Redis server
- GeoLite2-City.mmdb (MaxMind database for IP geolocation)

### Steps

#### 1. Download release JAR from GitHub

[CertVault Release](https://github.com/gregPerlinLi/CertVault/releases)

#### 2. Prepare configuration files

```bash
wget https://raw.githubusercontent.com/gregPerlinLi/CertVault/main/application.yml.example
cp application.yml.example application.yml
nano application.yml # Configure database/redis settings
```

#### 3. Deploy as systemd service (Linux)

```bash
wget https://raw.githubusercontent.com/gregPerlinLi/CertVault/main/certvault.service
nano certvault.service # Set service name and other parameters
sudo cp certvault.service /etc/systemd/system/
sudo systemctl --daemon-reload
sudo systemctl enable --now certvault
```

#### 4. Verify deployment

```bash
curl -I http://localhost:1888/api/v1/
```

### Configuration Parameters

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

## 2. Docker Deployment

### Options

- MySQL + Redis: `docker-compose-with-mysql-redis.yml`
- PostgreSQL + Redis: `docker-compose-with-postgres-redis.yml`
- External DB + Redis: Use `docker-compose-with-redis-external-*.yml` variants
- Internal DB + Redis: Use `docker-compose-with-*-external-redis.yml` variants
- External DB & Redis: Use `docker-compose-with-external-*-redis.yml` variants

### Steps (Example with Internal PostgreSQL & Redis)

#### 1. Clone repository

```bash
git clone https://github.com/gregPerlinLi/CertVault.git
cd CertVault/docker-compose
```

#### 2. Prepare configuration

```bash
nano .env # Set variables
nano application.yml # Configure detailed settings
```

#### 3. Deploy

```bash
docker-compose -f docker-compose-with-postgres-redis.yml up -d
```

#### 4. Verify

```bash
docker ps | grep cert-vault
curl -I http://localhost:1888/api/v1/
```
### Key Configuration Files
- `docker-compose-with-postgres-redis.yml` (default)
- `GeoLite2-City.mmdb` (required for IP geolocation)
- `application.yml` (other detailed configuration, like OIDC)

---

## 3. Helm Chart Deployment (Kubernetes)

### Prerequisites

- Kubernetes 1.20+
- Helm 3.0+
- Persistent Volume support

### Steps

#### 1. Add chart repository

```bash
helm repo add certvault https://gregperlinli.github.io/certvault-charts
```

#### 2. Pull chart

```bash
helm pull certvault/certvault --untar
```

#### 3. Edit configuration

```bash
cd certvault
nano values.yaml
```

#### 4. Deploy

```bash
helm --namespace certvault \
  --create-namespace \
  upgrade --install \
  certvault .
```

#### 5. Verify

```bash
curl -I http://svc-ip:1888/api/v1/
```

### Configuration Options

See [CertVault Helm Chart | certvault-charts](https://gregperlinli.github.io/certvault-charts/)

---

## 4. Source Code Deployment

### Prerequisites

- JDK 17
- Node.js 18+
- Maven 3.8+
- NPM & PNPM package manager

### Steps

#### 1. Clone repository
```bash
git clone https://github.com/gregPerlinLi/CertVault.git
cd CertVault
```

#### 2. Build project

```bash
make
```

#### 3. Deploy

```bash
make install
```

---

## Common Configuration Environment Parameters

| Parameter                | Description                      | Example                                                                    |
|--------------------------|----------------------------------|----------------------------------------------------------------------------|
| `DATABASE_TYPE`          | Database type (mysql/postgresql) | `DATABASE_TYPE=postgresql`                                                 |
| `DATABASE_URL`           | Database connection URL          | `DATABASE_URL=jdbc:postgresql://127.0.0.1:5432/cert_vault?sslmode=disable` |
| `SUPERADMIN_PASSWORD`    | Initial super admin password     | `SUPERADMIN_PASSWORD=admin123`                                             |
| `SPRING_PROFILES_ACTIVE` | Activate environment profile     | `prod`/`dev`                                                               |
| `SPRING_SERVER_PORT`     | Exposed service port             | `SPRING_SERVER_PORT=1888`                                                  |

## Verification

After deployment, access:
- UI: `http://<host>:1888`
- Health check: `http://<host>:1999/actuator/health`
- Swagger API docs: `http://<host>:1888/developer-api`

## Troubleshooting

- Database connection issues: Check environment variables and `application.yml` settings
- Docker issues: Check docker compose file and `application.yml` settings
- Helm issues: Run `helm -n cert-vault ls` to verify release status
- How to retrieve the logs:
  - For JAR package and Source Code deployment: `journalctl -xeu certvault.service -f`
  - For Docker deployment: `docker-compose logs -f`
  - For Helm deployment: `kubectl -n cert-vault logs -f deployments/cert-vault`
