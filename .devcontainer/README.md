# CertVault DevContainer Configuration

This directory contains the DevContainer configuration for CertVault development.

## What's Included

- **Java 17** with Maven 3.9
- **Node.js 22** with PNPM 10.10
- **PostgreSQL 14** database
- **Redis 7** cache server
- Pre-configured VS Code extensions for Java, Vue.js, and more
- Persistent volumes for Maven cache and PNPM store

## Getting Started

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Visual Studio Code](https://code.visualstudio.com/)
- [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

### Opening the Project

1. Open VS Code
2. Press `F1` and select `Dev Containers: Open Folder in Container...`
3. Select the CertVault project folder
4. Wait for the container to build and start

### Running the Application

#### Backend

```bash
cd server
mvn spring-boot:run
```

Access the backend at: http://localhost:1888

#### Frontend

```bash
cd frontend
pnpm run dev
```

Access the frontend at: http://localhost:5173

### Database Access

- **PostgreSQL**:
    - Host: localhost
    - Port: 5432
    - Database: certvault
    - Username: certvault
    - Password: certvault

- **Redis**:
    - Host: localhost
    - Port: 6379

### Useful Commands

```bash
# Build entire project
make all

# Run backend tests
cd server && mvn test

# Run frontend tests
cd frontend && pnpm run test

# Format frontend code
cd frontend && pnpm run format
```

## Customization

You can customize the DevContainer configuration by editing:
- `.devcontainer/devcontainer.json` - Main configuration
- `.devcontainer/docker-compose.yml` - Services configuration
- `.devcontainer/Dockerfile` - Container image
- `.devcontainer/post-create.sh` - Post-creation scripts

## Troubleshooting

### Container fails to start
- Ensure Docker Desktop is running
- Check Docker logs: `docker-compose logs`

### Database connection issues
- Verify PostgreSQL is running: `pg_isready -h localhost -U certvault`
- Check database logs: `docker-compose logs db`

### Port conflicts
- Ensure ports 1888, 5173, 5432, 6379 are not in use
- Modify port mappings in `devcontainer.json` if needed

## More Information

- [VS Code DevContainers Documentation](https://code.visualstudio.com/docs/devcontainers/containers)
- [CertVault Documentation](https://github.com/gregPerlinLi/CertVault)