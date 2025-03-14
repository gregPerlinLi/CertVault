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

| Parameter                                | Description                                           | Default Value                                |
|------------------------------------------|-------------------------------------------------------|----------------------------------------------|
| `image.registry`                         | Docker image registry URL                             | `ghcr.io`                                    |
| `image.repository`                       | Docker image repository                               | `gregperlinli/certvault`                     |
| `image.tag`                              | Docker image tag                                      | `latest`                                     |
| `image.pullPolicy`                       | Image pull policy                                     | `IfNotPresent`                               |
| `global.storageClass`                    | Storage class for persistent volumes                  | `standard`                                   |
| `replicaCount`                           | Number of pod replicas                                | `1`                                          |
| `springBoot.profile`                     | Active Spring Boot profile                            | `dev`                                        |
| `springBoot.logging.level`               | Logging levels for different packages                 | See `values.yaml`                            |
| `springBoot.javaOpts`                    | JVM options                                           | See `values.yaml`                            |
| `service.ports.business`                 | Business port for the application                     | `1888`                                       |
| `service.ports.management`               | Management port for the application                   | `1999`                                       |
| `serviceMonitor.enabled`                 | Enable Prometheus service monitor                     | `true`                                       |
| `serviceMonitor.path`                    | Path for Prometheus metrics endpoint                  | `/actuator/prometheus`                       |
| `serviceMonitor.port`                    | Port for Prometheus metrics endpoint                  | `1999`                                       |
| `resources.requests.cpu`                 | CPU request for the application container             | `500m`                                       |
| `resources.requests.memory`              | Memory request for the application container          | `512Mi`                                      |
| `resources.limits.cpu`                   | CPU limit for the application container               | `1000m`                                      |
| `resources.limits.memory`                | Memory limit for the application container            | `1024Mi`                                     |
| `redis.internal`                         | Use internal Redis instance                           | `true`                                       |
| `redis.architecture`                     | Redis architecture (standalone or cluster)            | `standalone`                                 |
| `redis.auth.enabled`                     | Whether authentication is enabled for Redis           | `true`                                       |
| `redis.auth.password`                    | Password used for Redis authentication                | `your-redis-password`                        |
| `redis.external.host`                    | Hostname or IP address of the external Redis instance | `redis-master.example.com`                   |
| `redis.external.port`                    | Port number of the external Redis instance            | `6379`                                       |
| `redis.external.database`                | Database index to use                                 | `0`                                          |
| `redis.external.auth.enabled`            | Whether authentication is enabled for external Redis  | `true`                                       |
| `redis.external.auth.password`           | Password for the external Redis instance              | `your-redis-password`                        |
| `mysql.host`                             | MySQL host                                            | `localhost`                                  |
| `mysql.port`                             | MySQL port                                            | `3306`                                       |
| `mysql.database`                         | MySQL database name                                   | `certvault`                                  |
| `mysql.username`                         | MySQL username                                        | `user`                                       |
| `mysql.password`                         | MySQL password                                        | `password`                                   |
| `ingress.enabled`                        | Enable ingress                                        | `false`                                      |

## Dependencies

- **Redis**: Version `20.11.3` from [Bitnami Charts](https://charts.bitnami.com/bitnami).

## Maintainers

- **Name**: gregPerlinLi
- **Email**: lihaolin13@outlook.com

## Source Code

- [GitHub Repository](https://github.com/gregperlinli/cert-vault)
- [Helm Chart](https://github.com/gregperlinli/certvault-charts)

## License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/gregperlinli/cert-vault/blob/main/LICENSE) file for details.
