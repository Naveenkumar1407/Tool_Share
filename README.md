# ToolShare — Neighborhood Tool Lending Platform

A full-stack web application for community tool sharing built with **React** + **Java Spring Boot** microservices.

## Quick Start

1. **Backend** (requires Java 17 + Maven):
   ```bash
   cd backend/user-service && mvn spring-boot:run   # port 8081
   cd backend/tool-service && mvn spring-boot:run   # port 8082
   ```

2. **Frontend** (requires Node.js 18+):
   ```bash
   cd frontend && npm install && npm start           # port 3000
   ```

3. Open http://localhost:3000 and login with `naveen / naveen123`

## Documentation
See [docs/README.md](docs/README.md) for complete API docs, DB schema, and architecture.
