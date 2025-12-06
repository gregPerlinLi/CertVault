#!/bin/bash

set -e

echo "🌟 Running post-start setup..."

# 等待数据库启动
echo "⏳ Waiting for PostgreSQL to be ready..."
until pg_isready -h localhost -U certvault; do
  sleep 1
done

echo "✅ PostgreSQL is ready!"

# 等待 Redis 启动
echo "⏳ Waiting for Redis to be ready..."
until redis-cli -h localhost ping > /dev/null 2>&1; do
  sleep 1
done

echo "✅ Redis is ready!"

echo "🎉 Development environment is ready!"
echo ""
echo "📝 Quick start commands:"
echo "  Backend:  cd server && mvn spring-boot:run"
echo "  Frontend: cd frontend && pnpm run dev"
echo ""