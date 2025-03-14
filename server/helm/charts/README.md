# CertVault Helm Chart

## Overview

This Helm chart deploys the **CertVault** self-sign certificate management platform server on Kubernetes. It provides a scalable and configurable way to manage self-signed certificates.

- **Chart Name**: `cert-vault`
- **Description**: A Helm chart for CertVault self-sign certificate management platform server.
- **Version**: `0.1.0`
- **App Version**: `1.0`

## Prerequisites

- Kubernetes 1.20+
- Helm 3.0+

## Installation

To install the chart with the release name `cert-vault`:

To install in a specific namespace:

```bash
helm install cert-vault . --namespace <namespace>
```


## Configuration

The following table lists the configurable parameters of the CertVault chart and their default values.

| Parameter                  | Description                                | Default Value                                |
|----------------------------|--------------------------------------------|----------------------------------------------|
| `image.repository`         | Docker image repository                    | `registry.gdutnic.com/cert-vault/cart-vault` |
| `image.tag`                | Docker image tag                           | `latest`                                     |
| `image.pullPolicy`         | Image pull policy                          | `IfNotPresent`                               |
| `global.storageClass`      | Storage class for persistent volumes       | `standard`                                   |
| `replicaCount`             | Number of pod replicas                     | `1`                                          |
| `springBoot.profile`       | Active Spring Boot profile                 | `dev`                                        |
| `springBoot.logging.level` | Logging levels for different packages      | See `values.yaml`                            |
| `springBoot.javaOpts`      | JVM options                                | See `values.yaml`                            |
| `service.ports.business`   | Business port for the application          | `1888`                                       |
| `service.ports.management` | Management port for the application        | `1999`                                       |
| `serviceMonitor.enabled`   | Enable Prometheus service monitor          | `true`                                       |
| `redis.internal`           | Use internal Redis instance                | `true`                                       |
| `redis.architecture`       | Redis architecture (standalone or cluster) | `standalone`                                 |
| `mysql.host`               | MySQL host                                 | `localhost`                                  |
| `mysql.port`               | MySQL port                                 | `3306`                                       |
| `mysql.database`           | MySQL database name                        | `certvault`                                  |
| `mysql.username`           | MySQL username                             | `user`                                       |
| `mysql.password`           | MySQL password                             | `password`                                   |
| `ingress.enabled`          | Enable ingress                             | `false`                                      |

## Dependencies

- **Redis**: Version `20.11.3` from [Bitnami Charts](https://charts.bitnami.com/bitnami).

## Maintainers

- **Name**: gregPerlinLi
- **Email**: lihaolin13@outlook.com

## Source Code

- [GitHub Repository](https://github.com/gregperlinli/cert-vault)

## License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/gregperlinli/cert-vault/blob/main/LICENSE) file for details.
