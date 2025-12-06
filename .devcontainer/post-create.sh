#!/bin/bash

set -e

echo "🚀 Running post-create setup..."

# 配置 npm 和 pnpm 镜像（确保配置生效）
echo "🔧 Configuring npm/pnpm mirrors..."
npm config set registry https://registry.npmmirror.com/
pnpm config set registry https://registry.npmmirror.com/

# 显示当前配置
echo "📋 Current npm registry: $(npm config get registry)"
echo "📋 Current pnpm registry: $(pnpm config get registry)"

# 设置 Git 配置（可选）
git config --global core.autocrlf input

# 安装后端依赖
echo "📦 Installing backend dependencies..."
cd /workspaces/CertVault/server
mvn dependency:resolve || true

# 安装前端依赖
echo "📦 Installing frontend dependencies..."
cd /workspaces/CertVault/frontend
pnpm install || true

# 创建必要的目录
mkdir -p /workspaces/CertVault/server/src/main/resources/static

echo "✅ Post-create setup completed!"