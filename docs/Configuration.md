# System Configuration Reference

> **[中文](Configuration_CN.md) | English**

---

This document provides comprehensive documentation of CertVault system configuration parameters, organized by functional modules.

## 1. Basic Configuration

| Parameter                 | Type    | Required | Example Value   | Environment Variable     | Description                  |
|---------------------------|---------|----------|-----------------|--------------------------|------------------------------|
| `spring.application.name` | String  | Yes      | `CertVault`     | None                     | Spring Boot application name |
| `spring.profiles.active`  | String  | Yes      | `prod`/`dev`    | `SPRING_PROFILES_ACTIVE` | Active environment profile   |
| `server.port`             | Integer | No       | `1888`          | `SPRING_SERVER_PORT`     | Service listening port       |

## 2. Database Configuration

### 2.1 MySQL Configuration

| Parameter                             | Type     | Required | Example Value                           | Environment Variable         | Description             |
|---------------------------------------|----------|----------|-----------------------------------------|------------------------------|-------------------------|
| `spring.datasource.driver-class-name` | String   | Yes      | `com.mysql.cj.jdbc.Driver`              | `SPRING_DATASOURCE_DRIVER`   | Database driver class   |
| `spring.datasource.url`               | JDBC URL | Yes      | `jdbc:mysql://localhost:3306/certvault` | `SPRING_DATASOURCE_URL`      | Database connection URL |
| `spring.datasource.username`          | String   | Yes      | `certvault`                             | `SPRING_DATASOURCE_USERNAME` | Database username       |
| `spring.datasource.password`          | String   | Yes      | `your-mysql-password`                   | `SPRING_DATASOURCE_PASSWORD` | Database password       |
| `spring.sql.init.platform`            | String   | Yes      | `mysql`                                 | `DATABASE_TYPE`              | Database type           |

### 2.2 PostgreSQL Configuration

| Parameter                             | Type     | Required | Example Value                                | Environment Variable         | Description             |
|---------------------------------------|----------|----------|----------------------------------------------|------------------------------|-------------------------|
| `spring.datasource.driver-class-name` | String   | Yes      | `org.postgresql.Driver`                      | `SPRING_DATASOURCE_DRIVER`   | Database driver class   |
| `spring.datasource.url`               | JDBC URL | Yes      | `jdbc:postgresql://localhost:5432/certvault` | `SPRING_DATASOURCE_URL`      | Database connection URL |
| `spring.datasource.username`          | String   | Yes      | `certvault`                                  | `SPRING_DATASOURCE_USERNAME` | Database username       |
| `spring.datasource.password`          | String   | Yes      | `your-postgresql-password`                   | `SPRING_DATASOURCE_PASSWORD` | Database password       |
| `spring.sql.init.platform`            | String   | Yes      | `postgresql`                                 | `DATABASE_TYPE`              | Database type           |

## 3. Cache Configuration

| Parameter                    | Type    | Required | Example Value         | Environment Variable    | Description                   |
|------------------------------|---------|----------|-----------------------|-------------------------|-------------------------------|
| `spring.data.redis.host`     | String  | Yes      | `localhost`           | `SPRING_REDIS_HOST`     | Redis server hostname         |
| `spring.data.redis.port`     | Integer | Yes      | `6379`                | `SPRING_REDIS_PORT`     | Redis server port             |
| `spring.data.redis.password` | String  | No       | `your-redis-password` | `SPRING_REDIS_PASSWORD` | Redis authentication password |
| `spring.data.redis.database` | Integer | No       | `1`                   | `SPRING_REDIS_DATABASE` | Redis database index          |

## 4. Security Configuration

### 4.1 OIDC Authentication

| Parameter                                                                           | Type    | Required    | Example Value                                           | Environment Variable | Description                            |
|-------------------------------------------------------------------------------------|---------|-------------|---------------------------------------------------------|----------------------|----------------------------------------|
| `spring.security.oauth2.client.registration.{oidc-id}.client-id`                    | String  | Conditional | `cert-vault`                                            | None                 | OAuth2 client ID                       |
| `spring.security.oauth2.client.registration.{oidc-id}.client-secret`                | String  | Conditional | `changeme`                                              | None                 | OAuth2 client secret                   |
| `spring.security.oauth2.client.registration.{oidc-id}.scope`                        | List    | No          | `openid,email,profile`                                  | None                 | Authorization scopes (comma-separated) |
| `spring.security.oauth2.client.registration.{oidc-id}.redirect-uri`                 | URI     | Conditional | `http://127.0.0.1:1888/api/v1/auth/oauth/callback/oidc` | None                 | OAuth callback URL                     |
| `spring.security.oauth2.client.registration.{oidc-id}.authorization-grant-type`     | String  | No          | `authorization_code`                                    | None                 | Authorization grant type               |
| `spring.security.oauth2.client.registration.{oidc-id}.client-authentication-method` | String  | No          | `client_secret_basic`                                   | None                 | Client authentication method           |
| `spring.security.oauth2.client.provider.{oidc-id}.token-uri`                        | URI     | Conditional | `https://127.0.0.1:8443/...`                            | None                 | Token endpoint                         |
| `spring.security.oauth2.client.provider.{oidc-id}.authorization-uri`                | URI     | Conditional | `https://127.0.0.1:8443/...`                            | None                 | Authorization endpoint                 |
| `spring.security.oauth2.client.provider.{oidc-id}.user-info-uri`                    | URI     | Conditional | `https://127.0.0.1:8443/...`                            | None                 | User info endpoint                     |
| `spring.security.oauth2.client.provider.{oidc-id}.jwk-set-uri`                      | URI     | Conditional | `https://127.0.0.1:8443/...`                            | None                 | JWK set endpoint                       |
| `spring.security.oauth2.client.provider.{oidc-id}.user-name-attribute`              | String  | No          | `preferred_username`                                    | None                 | Username attribute                     |
| `oidc.enabled`                                                                      | Boolean | No          | `true`                                                  | `ENABLE_OIDC`        | Enable OIDC authentication             |
| `oidc.providers.{oidc-id}.name`                                                     | String  | No          | `OpenID Connect`                                        | None                 | Authentication provider name           |
| `oidc.providers.{oidc-id}.logo`                                                     | URI     | No          | `https://cdn.jsdelivr.net/...`                          | None                 | Provider logo URL                      |

### 4.2 Key Configuration

| Parameter             | Type       | Required | Example Value                                             | Environment Variable | Description     |
|-----------------------|------------|----------|-----------------------------------------------------------|----------------------|-----------------|
| `rsa.key.private-key` | PEM Format | Yes      | `MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKeP...` | `RSA_PRIVATE_KEY`    | RSA private key |
| `rsa.key.public-key`  | PEM Format | Yes      | `MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnj...`            | `RSA_PUBLIC_KEY`     | RSA public key  |

## 5. Additional Configuration

### 5.1 GeoIP Configuration

| Parameter         | Type      | Required    | Example Value         | Environment Variable | Description         |
|-------------------|-----------|-------------|-----------------------|----------------------|---------------------|
| `geoip.type`      | String    | No          | `ip-api.com`/`mmdb`   | `GEOIP_TYPE`         | IP geolocation type |
| `geoip.file-path` | File Path | Conditional | `/GeoLite2-City.mmdb` | `GEOIP_FILE_PATH`    | GeoIP database path |

### 5.2 Logging Configuration

| Parameter                                  | Type   | Required | Example Value                 | Environment Variable                              | Description                |
|--------------------------------------------|--------|----------|-------------------------------|---------------------------------------------------|----------------------------|
| `logging.level.com.gregperlinli.certvault` | String | No       | `debug`/`info`/`warn`/`error` | `SPRING_LOGGING_LEVEL_COM_GREGPERLINLI_CERTVAULT` | Module-specific log level  |
| `logging.level.org.springframework`        | String | No       | `debug`/`info`/`warn`/`error` | `SPRING_LOGGING_LEVEL_ORG_SPRINGFRAMEWORK`        | Spring framework log level |

## Configuration Examples

```yaml
# application.yml Example
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

# OIDC Configuration Example
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