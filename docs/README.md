# ToolShare — A Neighborhood Tool Lending Platform

## Problem Statement

In many communities, people own tools and equipment (drills, pressure washers, ladders, camping gear, etc.) that they use only occasionally. Purchasing new tools for one-time use is wasteful and expensive. Currently, neighbors rely on informal word-of-mouth or social media groups to borrow items, with no proper tracking of who has what, when it's due back, or what condition it's in.

**ToolShare** solves this by providing a web-based platform where community members can list tools they're willing to lend, browse and request tools from neighbors, and manage the complete borrow-return lifecycle with full accountability.

## Architecture

```
┌─────────────────────┐      ┌──────────────────────────┐
│   React Frontend    │      │      user-service        │
│   (port 3000)       │─────▶│      (port 8081)         │
│                     │      │  - Authentication (JWT)   │
│  - Browse Tools     │      │  - User Registration     │
│  - Search/Filter    │      │  - Profile Management    │
│  - Borrow Requests  │      │  - H2 Database           │
│  - Dashboard        │      └──────────────────────────┘
│  - Auth (Login/     │
│    Register)        │      ┌──────────────────────────┐
│                     │─────▶│      tool-service         │
└─────────────────────┘      │      (port 8082)         │
                             │  - Tool CRUD              │
                             │  - Borrow Management     │
                             │  - Date Conflict Check   │
                             │  - H2 Database           │
                             └──────────────────────────┘
```

### Microservice Architecture
- **user-service (port 8081)**: Handles user registration, JWT authentication, and profile management
- **tool-service (port 8082)**: Manages tool catalog, borrow requests, approvals, and returns
- **React frontend (port 3000)**: Single-page application with React Router for navigation

### Tech Stack
| Layer | Technology |
|-------|-----------|
| Frontend | React 18, React Router v6, Axios |
| Backend | Java 17, Spring Boot 3.1.5 |
| Security | Spring Security, JWT (jjwt 0.11.5) |
| Database | H2 (embedded, file-based) |
| Build | Maven 3.8.1, npm |
| API Docs | SpringDoc OpenAPI (Swagger UI) |

## Database Schema

### user-service: `users` table
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| username | VARCHAR | UNIQUE, NOT NULL |
| email | VARCHAR | UNIQUE, NOT NULL |
| password | VARCHAR | NOT NULL (BCrypt hashed) |
| full_name | VARCHAR | |
| phone | VARCHAR | |
| role | VARCHAR | ENUM: MEMBER, ADMIN |
| created_at | TIMESTAMP | |

### tool-service: `tools` table
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| name | VARCHAR | NOT NULL |
| description | VARCHAR | |
| category | VARCHAR | NOT NULL |
| tool_condition | VARCHAR | ENUM: NEW, GOOD, FAIR, WORN |
| available | BOOLEAN | DEFAULT TRUE |
| owner_id | BIGINT | NOT NULL |
| owner_username | VARCHAR | |
| created_at | TIMESTAMP | |

### tool-service: `borrow_requests` table
| Column | Type | Constraints |
|--------|------|------------|
| id | BIGINT | PK, AUTO_INCREMENT |
| tool_id | BIGINT | FK → tools.id |
| borrower_id | BIGINT | NOT NULL |
| borrower_username | VARCHAR | |
| start_date | DATE | NOT NULL |
| end_date | DATE | NOT NULL |
| status | VARCHAR | ENUM: PENDING, APPROVED, REJECTED, RETURNED |
| message | VARCHAR | |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

## API Documentation

### Auth APIs (user-service :8081)

#### POST /api/auth/register
Register a new user.
```json
Request: {
  "username": "john",
  "email": "john@example.com",
  "password": "pass123",
  "fullName": "John Doe",
  "phone": "555-0101"
}
Response: {
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "john",
  "role": "MEMBER",
  "userId": 4
}
```

#### POST /api/auth/login
```json
Request: { "username": "rahul", "password": "rahul123" }
Response: { "token": "...", "username": "rahul", "role": "MEMBER", "userId": 2 }
```

### User APIs (user-service :8081)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | /api/users/me | JWT | Get current user profile |
| PUT | /api/users/me | JWT | Update own profile |
| GET | /api/users/{id} | Public | Get user by ID |
| GET | /api/users | JWT | List all users |

### Tool APIs (tool-service :8082)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | /api/tools | Public | List all tools (optional: ?search=&category=) |
| GET | /api/tools/{id} | Public | Get tool details |
| GET | /api/tools/available | Public | List available tools only |
| GET | /api/tools/my | JWT | Get tools I listed |
| POST | /api/tools | JWT | Add a new tool |
| PUT | /api/tools/{id} | JWT | Update my tool |
| DELETE | /api/tools/{id} | JWT | Delete tool (owner or admin) |

### Borrow APIs (tool-service :8082)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /api/borrows | JWT | Create borrow request |
| GET | /api/borrows/my-requests | JWT | My outgoing requests |
| GET | /api/borrows/incoming | JWT | Requests for my tools |
| PUT | /api/borrows/{id}/approve | JWT | Approve a request (owner) |
| PUT | /api/borrows/{id}/reject | JWT | Reject a request (owner) |
| PUT | /api/borrows/{id}/return | JWT | Mark as returned |

#### POST /api/borrows
```json
Request: {
  "toolId": 1,
  "startDate": "2026-04-25",
  "endDate": "2026-04-30",
  "message": "Need it for a weekend project"
}
```

## Component Hierarchy (Frontend)

```
App
├── AuthProvider (Context)
├── BrowserRouter
│   ├── Navbar
│   └── Routes
│       ├── HomePage (/ )
│       │   ├── Search bar
│       │   ├── Category filters
│       │   └── Tool cards grid
│       ├── LoginPage (/login)
│       ├── RegisterPage (/register)
│       ├── ToolDetailPage (/tools/:id)
│       │   └── Borrow request form
│       ├── AddToolPage (/tools/add)
│       └── DashboardPage (/dashboard)
│           ├── Stats row
│           ├── My Tools tab
│           ├── Incoming Requests tab
│           └── My Borrows tab
```

## How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+

### Backend
```bash
# Set Java 17
export JAVA_HOME=/path/to/java17

# Start user-service (port 8081)
cd backend/user-service
mvn spring-boot:run

# Start tool-service (port 8082) — in another terminal
cd backend/tool-service
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
# Opens at http://localhost:3000
```

### Test Accounts (seeded automatically)
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| naveen | naveen123 | MEMBER |
| kumar | kumar123 | MEMBER |

## Swagger UI
- user-service: http://localhost:8081/swagger-ui.html
- tool-service: http://localhost:8082/swagger-ui.html

## Assumptions
1. H2 file-based database is used for simplicity (can be swapped to MySQL via properties)
2. JWT tokens are shared between services using the same secret key
3. Tool images are not stored — only text descriptions
4. The platform serves a single community (no location/radius filtering)
5. One active borrow per tool at a time (date overlap prevention in place)
