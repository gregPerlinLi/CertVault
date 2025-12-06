FROM mcr.microsoft.com/devcontainers/base:ubuntu-22.04

# 避免交互式提示
ENV DEBIAN_FRONTEND=noninteractive

# 安装基础工具
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    git \
    vim \
    nano \
    unzip \
    zip \
    build-essential \
    ca-certificates \
    gnupg \
    lsb-release \
    && rm -rf /var/lib/apt/lists/*

# 安装 Java 17
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# 安装 Maven
ARG MAVEN_VERSION=3.9.9
RUN wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz -C /usr/local \
    && ln -s /usr/local/apache-maven-${MAVEN_VERSION} /usr/local/maven \
    && rm apache-maven-${MAVEN_VERSION}-bin.tar.gz

ENV MAVEN_HOME=/usr/local/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

# 安装 Node.js 22
RUN curl -fsSL https://deb.nodesource.com/setup_22.x | bash - \
    && apt-get install -y nodejs \
    && rm -rf /var/lib/apt/lists/*

# 配置 npm 镜像加速（全局配置）
RUN npm config set registry https://registry.npmmirror.com/ \
    && npm config set disturl https://npmmirror.com/dist \
    && npm config set electron_mirror https://npmmirror.com/mirrors/electron/ \
    && npm config set sass_binary_site https://npmmirror.com/mirrors/node-sass/ \
    && npm config set phantomjs_cdnurl https://npmmirror.com/mirrors/phantomjs/

# 安装 pnpm 并配置镜像
RUN npm install -g pnpm@10.10.0 \
    && pnpm config set registry https://registry.npmmirror.com/ \
    && pnpm config set disturl https://npmmirror.com/dist

# 安装 PostgreSQL 客户端
RUN apt-get update && apt-get install -y \
    postgresql-client \
    && rm -rf /var/lib/apt/lists/*

# 创建工作目录
WORKDIR /workspace

# 设置 Maven 配置
RUN mkdir -p /root/.m2
COPY settings.xml /root/.m2/settings.xml

# 复制 npm/pnpm 配置文件
COPY .npmrc /root/.npmrc
COPY .pnpmrc /root/.pnpmrc

# 清理
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

ENV DEBIAN_FRONTEND=dialog