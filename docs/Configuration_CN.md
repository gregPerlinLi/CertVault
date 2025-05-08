# 系统配置项说明

> **中文 | [English](Configuration.md)**

---

本文档详细说明CertVault系统的配置参数，按照功能模块进行分类说明。

## 一、基础配置

| 配置项                       | 类型  | 必填 | 示例值          | 说明              | 环境变量                     |
|---------------------------|-----|----|--------------|-----------------|--------------------------|
| `spring.application.name` | 字符串 | 是  | `CertVault`  | Spring Boot应用名称 | 无                        |
| `spring.profiles.active`  | 字符串 | 是  | `prod`/`dev` | 激活的环境配置文件       | `SPRING_PROFILES_ACTIVE` |
| `server.port`             | 数字  | 否  | `1888`       | 服务监听端口          | `SPRING_SERVER_PORT`     |

## 二、数据库配置

### 1. MySQL配置

| 配置项                          | 类型       | 必填 | 示例值                                     | 说明      | 环境变量                         |
|------------------------------|----------|----|-----------------------------------------|---------|------------------------------|
| `spring.datasource.url`      | JDBC URL | 是  | `jdbc:mysql://localhost:3306/certvault` | 数据库连接地址 | `SPRING_DATASOURCE_URL`      |
| `spring.datasource.username` | 字符串      | 是  | `certvault`                             | 数据库用户名  | `SPRING_DATASOURCE_USERNAME` |
| `spring.datasource.password` | 字符串      | 是  | `your-mysql-password`                   | 数据库密码   | `SPRING_DATASOURCE_PASSWORD` |
| `spring.sql.init.platform`   | 字符串      | 是  | `mysql`                                 | 数据库类型   | `DATABASE_TYPE`              |

### 2. PostgreSQL配置

| 配置项                          | 类型       | 必填 | 示例值                                          | 说明      | 环境变量                         |
|------------------------------|----------|----|----------------------------------------------|---------|------------------------------|
| `spring.datasource.url`      | JDBC URL | 是  | `jdbc:postgresql://localhost:5432/certvault` | 数据库连接地址 | `SPRING_DATASOURCE_URL`      |
| `spring.datasource.username` | 字符串      | 是  | `certvault`                                  | 数据库用户名  | `SPRING_DATASOURCE_USERNAME` |
| `spring.datasource.password` | 字符串      | 是  | `your-postgresql-password`                   | 数据库密码   | `SPRING_DATASOURCE_PASSWORD` |
| `spring.sql.init.platform`   | 字符串      | 是  | `postgresql`                                 | 数据库类型   | `DATABASE_TYPE`              |

## 三、缓存配置

| 配置项                          | 类型  | 必填 | 示例值                   | 说明         | 环境变量                    |
|------------------------------|-----|----|-----------------------|------------|-------------------------|
| `spring.data.redis.host`     | 字符串 | 是  | `localhost`           | Redis主机地址  | `SPRING_REDIS_HOST`     |
| `spring.data.redis.port`     | 数字  | 是  | `6379`                | Redis端口    | `SPRING_REDIS_PORT`     |
| `spring.data.redis.password` | 字符串 | 否  | `your-redis-password` | Redis认证密码  | `SPRING_REDIS_PASSWORD` |
| `spring.data.redis.database` | 数字  | 否  | `1`                   | Redis数据库索引 | `SPRING_REDIS_DATABASE` |

## 四、安全配置

### 1. OIDC认证

| 配置项                                                                                 | 类型  | 必填 | 示例值                                                     | 环境变量          | 说明         |
|-------------------------------------------------------------------------------------|-----|----|---------------------------------------------------------|---------------|------------|
| `spring.security.oauth2.client.registration.{oidc-id}.client-id`                    | 字符串 | 条件 | `cert-vault`                                            | 无             | 客户端ID      |
| `spring.security.oauth2.client.registration.{oidc-id}.client-secret`                | 字符串 | 条件 | `changeme`                                              | 无             | 客户端密钥      |
| `spring.security.oauth2.client.registration.{oidc-id}.scope`                        | 列表  | 否  | `openid,email,profile`                                  | 无             | 授权范围（逗号分隔） |
| `spring.security.oauth2.client.registration.{oidc-id}.redirect-uri`                 | URI | 条件 | `http://127.0.0.1:1888/api/v1/auth/oauth/callback/oidc` | 无             | OAuth回调地址  |
| `spring.security.oauth2.client.registration.{oidc-id}.authorization-grant-type`     | 字符串 | 否  | `authorization_code`                                    | 无             | 授权类型       |
| `spring.security.oauth2.client.registration.{oidc-id}.client-authentication-method` | 字符串 | 否  | `client_secret_basic`                                   | 无             | 客户端认证方法    |
| `spring.security.oauth2.client.provider.{oidc-id}.token-uri`                        | URI | 条件 | `https://127.0.0.1:8443/...`                            | 无             | OIDC令牌端点   |
| `spring.security.oauth2.client.provider.{oidc-id}.authorization-uri`                | URI | 条件 | `https://127.0.0.1:8443/...`                            | 无             | 授权端点       |
| `spring.security.oauth2.client.provider.{oidc-id}.user-info-uri`                    | URI | 条件 | `https://127.0.0.1:8443/...`                            | 无             | 用户信息端点     |
| `spring.security.oauth2.client.provider.{oidc-id}.jwk-set-uri`                      | URI | 条件 | `https://127.0.0.1:8443/...`                            | 无             | JWK集端点     |
| `spring.security.oauth2.client.provider.{oidc-id}.user-name-attribute`              | 字符串 | 否  | `preferred_username`                                    | 无             | 用户名属性      |
| `oidc.enabled`                                                                      | 布尔值 | 否  | `true`                                                  | `ENABLE_OIDC` | 是否启用OIDC认证 |
| `oidc.providers.{oidc-id}.name`                                                     | 字符串 | 否  | `OpenID Connect`                                        | `无            | 认证提供商名称    |
| `oidc.providers.{oidc-id}.logo`                                                     | URI | 否  | `https://cdn.jsdelivr.net/...`                          | 无             | 提供商Logo地址  |

### 2. 密钥配置

| 配置项                   | 类型    | 必填 | 示例值                                                       | 环境变量              | 说明    |
|-----------------------|-------|----|-----------------------------------------------------------|-------------------|-------|
| `rsa.key.private-key` | PEM格式 | 是  | `MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKeP...` | `RSA_PRIVATE_KEY` | RSA私钥 |
| `rsa.key.public-key`  | PEM格式 | 是  | `MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnj...`            | `RSA_PUBLIC_KEY`  | RSA公钥 |

## 五、其他配置

### 1. GeoIP配置

| 配置项               | 类型   | 必填 | 示例值                   | 环境变量              | 说明         |
|-------------------|------|----|-----------------------|-------------------|------------|
| `geoip.type`      | 字符串  | 否  | `ip-api.com`/`mmdb`   | `GEOIP_TYPE`      | IP定位类型     |
| `geoip.file-path` | 文件路径 | 条件 | `/GeoLite2-City.mmdb` | `GEOIP_FILE_PATH` | GeoIP数据库路径 |

### 2. 日志配置

| 配置项                                        | 类型  | 必填 | 示例值                           | 环境变量                                              | 说明           |
|--------------------------------------------|-----|----|-------------------------------|---------------------------------------------------|--------------|
| `logging.level.com.gregperlinli.certvault` | 字符串 | 否  | `debug`/`info`/`warn`/`error` | `SPRING_LOGGING_LEVEL_COM_GREGPERLINLI_CERTVAULT` | 模块日志级别       |
| `logging.level.org.springframework`        | 字符串 | 否  | `debug`/`info`/`warn`/`error` | `SPRING_LOGGING_LEVEL_ORG_SPRINGFRAMEWORK`        | Spring框架日志级别 |

## 配置示例

```yaml
# application.yml 示例
spring:
  application:
    name: CertVault
  profiles:
    active: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/certvault
    username: certvault
    password: securepassword
  data:
    redis:
      host: localhost
      port: 6379
      password: redispass
      database: 1

# OIDC配置示例
spring:
  security:
    oauth2:
      client:
        registration:
          oidc:
            client-id: cert-vault
            client-secret: changeme
            scope: openid,email,profile
            redirect-uri: "${SERVER_BASE_URL:http://127.0.0.1:1888}/api/v1/auth/oauth/callback/oidc"
            authorization-grant-type: authorization_code
            client-authentication-method: client_secret_basic
        provider:
          oidc:
            token-uri: "https://127.0.0.1:8443/realms/cert-vault/protocol/openid-connect/token"
            authorization-uri: "https://127.0.0.1:8443/realms/cert-vault/protocol/openid-connect/auth"
            user-info-uri: "https://127.0.0.1:8443/realms/cert-vault/protocol/openid-connect/userinfo"
            jwk-set-uri: "https://127.0.0.1:8443/realms/cert-vault/protocol/openid-connect/certs"
            user-name-attribute: preferred_username

oidc:
  enabled: true
  providers:
    oidc:
      name: "OpenID Connect"
      logo: "https://cdn.jsdelivr.net/gh/gregperlinli/certvault-frontend@v1.0.0/src/assets/logo.png"
```