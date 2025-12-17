FROM eclipse-temurin:17-jre

ARG JAR_FILE=target/*.jar
ARG IMAGE_CREATED=1970-01-01T00:00:00Z
ARG IMAGE_VERSION=0.0.0
ARG IMAGE_REVISION=1

ENV TIME_ZONE=Asia/Shanghai
COPY ${JAR_FILE} app.jar
RUN ln -snf /usr/share/zoneinfo/${TIME_ZONE} /etc/localtime && echo ${TIME_ZONE} > /etc/timezone
ENTRYPOINT ["java","-jar","/app.jar"]

LABEL org.opencontainers.image.created="${IMAGE_CREATED}" \
  org.opencontainers.image.authors="gregPerlinLi" \
  org.opencontainers.image.url="https://github.com/gregPerlinLi/CertVault" \
  org.opencontainers.image.documentation="https://github.com/gregPerlinLi/CertVault/blob/main/README.md" \
  org.opencontainers.image.source="https://github.com/gregPerlinLi/CertVault" \
  org.opencontainers.image.version="${IMAGE_VERSION}" \
  org.opencontainers.image.revision="${IMAGE_REVISION}" \
  org.opencontainers.image.vendor="gregPerlinLi" \
  org.opencontainers.image.licenses="Apache License 2.0" \
  org.opencontainers.image.title="A Self-Signed SSL Certificate Issuance and Management Platform" \
  org.opencontainers.image.description="🔒⚙️CertVault —— A Self-Signed SSL Certificate Issuance and Management Platform"
