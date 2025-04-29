# CertVault Helm Chart

## Overview

This Helm chart deploys the **CertVault** self-sign certificate management platform server on Kubernetes. It provides a scalable and configurable way to manage self-signed certificates.

- **Chart Name**: `cert-vault`
- **Description**: A Helm chart for CertVault self-signed SSL certificate management platform backend server.
- **Version**: `@project.version@`
- **App Version**: `@project.version@`

## Prerequisites

- Kubernetes 1.20+
- Helm 3.0+

## Installation

To install the chart with the release name `cert-vault`:

To install in a specific namespace:

```bash
kubectl create namespace cert-vault
helm repo add cert-vault https://gregperlinli.github.io/certvault-charts/
helm repo update
helm -n cert-vault upgrade --install cert-vault/cert-vault
```


## Configuration

The following table lists the configurable parameters of the CertVault chart and their default values.

| Parameter                                   | Description                                                           | Default Value              |
|---------------------------------------------|-----------------------------------------------------------------------|----------------------------|
| `image.registry`                            | Docker image registry URL                                             | `ghcr.io`                  |
| `image.repository`                          | Docker image repository                                               | `gregperlinli/certvault`   |
| `image.tag`                                 | Docker image tag                                                      | `latest`                   |
| `image.pullPolicy`                          | Image pull policy                                                     | `IfNotPresent`             |
| `global.defaultStorageClass`                | Storage class for persistent volumes                                  | `standard`                 |
| `replicaCount`                              | Number of pod replicas                                                | `1`                        |
| `springBoot.profile`                        | Active Spring Boot profile                                            | `dev`                      |
| `springBoot.logging.level`                  | Logging levels for different packages                                 | See `values.yaml`          |
| `springBoot.javaOpts`                       | JVM options                                                           | See `values.yaml`          |
| `service.ports.business`                    | Business port for the application                                     | `1888`                     |
| `server.baseUrl`                            | The base URL of the server                                            | `http://127.0.0.1:1888`    |
| `oidc.enabled`                              | Enable OIDC authentication                                            | `false`                    |
| `oidc.providers`                            | OIDC providers configuration                                          | See `values.yaml`          |
| `geoip.type`                                | How to get GeoIP data (ip-api.com or mmdb)                            | `ip-api.com`               |
| `geoip.mmdb`                                | GeoIP database file configuration                                     | See `values.yaml`          |
| `init.superadmin`                           | Superadmin account configuration                                      | See `values.yaml`          |
| `encrypt.rsa.key`                           | RSA encryption configuration                                          | See `values.yaml`          |
| `apiDocs.enabled`                           | Enable Swagger UI                                                     | `true`                     |
| `service.ports.management`                  | Management port for the application                                   | `1999`                     |
| `serviceMonitor.enabled`                    | Enable Prometheus service monitor                                     | `true`                     |
| `serviceMonitor.path`                       | Path for Prometheus metrics endpoint                                  | `/actuator/prometheus`     |
| `serviceMonitor.port`                       | Port for Prometheus metrics endpoint                                  | `1999`                     |
| `resources.requests.cpu`                    | CPU request for the application container                             | `500m`                     |
| `resources.requests.memory`                 | Memory request for the application container                          | `512Mi`                    |
| `resources.limits.cpu`                      | CPU limit for the application container                               | `1000m`                    |
| `resources.limits.memory`                   | Memory limit for the application container                            | `1024Mi`                   |
| `redis.internal`                            | Use internal Redis instance                                           | `true`                     |
| `redis.auth.enabled`                        | Whether authentication is enabled for Redis                           | `true`                     |
| `redis.auth.password`                       | Password used for Redis authentication                                | `your-redis-password`      |
| `redis.architecture`                        | Redis architecture (standalone or cluster)                            | `standalone`               |
| `redis.metrics.enabled`                     | Enable metrics for Redis                                              | `true`                     |
| `redis.metrics.serviceMonitor.enabled`      | Enable Prometheus service monitor for Redis metrics                   | `true`                     |
| `redis.external.host`                       | Hostname or IP address of the external Redis instance                 | `redis-master.example.com` |
| `redis.external.port`                       | Port number of the external Redis instance                            | `6379`                     |
| `redis.external.database`                   | Database index to use                                                 | `0`                        |
| `redis.external.auth.enabled`               | Whether authentication is enabled for external Redis                  | `true`                     |
| `redis.external.auth.password`              | Password for the external Redis instance                              | `your-redis-password`      |
| `database.type`                             | Database type (mysql or postgresql)                                   | `postgresql`               |
| `mysql.internal`                            | Use internal MySQL instance                                           | `false`                    |
| `mysql.auth.rootPassword`                   | Root user password for internal MySQL deployment                      | `your-mysql-root-password` |
| `mysql.auth.database`                       | Database name to use (used in internal deployment)                    | `certvault`                |
| `mysql.auth.username`                       | Username for MySQL access (used in internal deployment)               | `certvault`                |
| `mysql.auth.password`                       | Username for MySQL access (used in internal deployment)               | `your-mysql-password`      |
| `mysql.architecture`                        | The architecture of the MySQL deployment (standalone or cluster)      | `standalone`               |
| `mysql.metrics.enabled`                     | Enable metrics for MySQL                                              | `true`                     |
| `mysql.metrics.serviceMonitor.enabled`      | Enable Prometheus service monitor for MySQL metrics                   | `true`                     |
| `mysql.external.host`                       | Hostname or IP address of the external MySQL instance                 | `localhost`                |
| `mysql.external.port`                       | Port of the external MySQL instance                                   | `3306`                     |
| `mysql.external.database`                   | Database name to use (used in external deployment)                    | `certvault`                |
| `mysql.external.username`                   | Username for MySQL access (used in external deployment)               | `certvault`                |
| `mysql.external.password`                   | Password for MySQL access (used in external deployment)               | `your-mysql-password`      |
| `postgresql.internal`                       | Use internal PostgreSQL instance                                      | `false`                    |
| `postgresql.auth.postgresqlPassword`        | PostgreSQL password for internal PostgreSQL deployment                | `your-postgresql-password` |
| `postgresql.auth.database`                  | Database name to use (used in internal deployment)                    | `certvault`                |
| `postgresql.auth.username`                  | Username for PostgreSQL access (used in internal deployment)          | `certvault`                |
| `postgresql.auth.password`                  | Password for PostgreSQL access (used in internal deployment)          | `your-postgresql-password` |
| `postgresql.architecture`                   | The architecture of the PostgreSQL deployment (standalone or cluster) | `standalone`               |
| `postgresql.metrics.enabled`                | Enable metrics for PostgreSQL                                         | `true`                     |
| `postgresql.metrics.serviceMonitor.enabled` | Enable Prometheus service monitor for PostgreSQL metrics              | `true`                     |
| `postgresql.external.host`                  | Hostname or IP address of the external PostgreSQL instance            | `localhost`                |
| `postgresql.external.port`                  | Port of the external PostgreSQL instance                              | `5432`                     |
| `postgresql.external.database`              | Database name to use (used in external deployment)                    | `certvault`                |
| `postgresql.external.username`              | Username for PostgreSQL access (used in external deployment)          | `certvault`                |
| `postgresql.external.password`              | Password for PostgreSQL access (used in external deployment)          | `your-postgresql-password` |
| `ingress.enabled`                           | Enable ingress                                                        | `false`                    |
| `ingress.annotation`                        | Annotations for ingress                                               | `{}`                       |
| `ingress.hosts`                             | Hosts configuration for ingress                                       | See `values.yaml`          |
| `nodeSelector`                              | Node Selector configuration                                           | See `values.yaml`          |
| `nodeAffinity`                              | Node Affinity configuration                                           | See `values.yaml`          |
| `podAffinity`                               | Pod Affinity configuration                                            | See `values.yaml`          |

## Dependencies

- **Redis**: Version `20.11.3` from [Bitnami Charts](https://charts.bitnami.com/bitnami).
- **MySQL**: Version `8.0.28` from [Bitnami Charts](https://charts.bitnami.com/bitnami).
- **PostgreSQL**: Version `14.5` from [Bitnami Charts](https://charts.bitnami.com/bitnami).

## Maintainers

- **Name**: gregPerlinLi
- **Email**: lihaolin13@outlook.com

## Source Code

- [GitHub Repository](https://github.com/gregPerlinLi/CertVault)
- [Helm Chart](https://github.com/gregPerlinLi/certvault-charts)

## License

This project is licensed under the Apache 2.0 License. See the [LICENSE](https://github.com/gregperlinli/cert-vault/blob/main/LICENSE) file for details.
