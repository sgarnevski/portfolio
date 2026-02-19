# Portfolio Rebalancer

A full-stack portfolio management and rebalancing application. Track your holdings, monitor live prices, and get trade recommendations to keep your portfolio aligned with target allocations.

## Features

- **Portfolio Management** — Create and manage multiple investment portfolios
- **Holdings Tracking** — Add stocks/ETFs with quantity, cost basis, and currency
- **Live Prices** — Real-time quotes via WebSocket (STOMP)
- **Target Allocations** — Define target percentages per asset class (Equity, Bond, Commodity, Real Estate, Cash)
- **Rebalancing Engine** — Calculate trades needed to realign with targets, or allocate new cash optimally
- **Profit/Loss** — Track cost basis and see P/L per holding
- **Multi-Currency** — Holdings and rebalancing displayed in the correct currency
- **User Settings** — Profile management, password changes, currency configuration

## Architecture

```
Frontend:3000 → Backend:8080 → Market-Data-Gateway:8060 → Yahoo Finance
                    ↕
              Auth-Service:8090 (user + service tokens)
```

| Service | Port | Description |
|---------|------|-------------|
| **Backend** | 8080 | Main API — portfolios, holdings, rebalancing, WebSocket prices |
| **Auth Service** | 8090 | JWT auth, Google SSO, user profiles, service token issuance |
| **Market Data Gateway** | 8060 | Vendor-neutral market data proxy (currently Yahoo Finance) |
| **Frontend** | 3000 | React SPA, proxies API calls to backend and auth-service |

The market data gateway isolates the external data vendor — if Yahoo Finance is replaced, only the gateway changes. Backend-to-gateway communication is secured via OAuth2 `client_credentials` flow (service JWTs issued by the auth-service).

## Tech Stack

### Backend
- Java 17+ / Spring Boot 3.4.3
- Spring Security with JWT authentication (jjwt)
- Spring Data JPA with H2 (dev) / PostgreSQL (prod)
- Spring WebSocket (STOMP) for live price updates
- OpenAPI/Swagger UI via springdoc

### Auth Service
- Java 17+ / Spring Boot 3.4.3
- JWT user tokens + service tokens (client_credentials grant)
- Google OAuth2 SSO
- H2 (dev) / PostgreSQL (prod)

### Market Data Gateway
- Java 17+ / Spring Boot 3.4.3
- Stateless (no database)
- JWT-secured endpoints (validates service tokens)
- Yahoo Finance API integration

### Frontend
- React 18 + TypeScript
- Vite build tool
- Redux Toolkit + Redux Saga for state management
- React Hook Form for form handling
- Tailwind CSS for styling
- Recharts for allocation pie charts
- STOMP.js + SockJS for WebSocket price feeds

## Getting Started

### Prerequisites
- Java 17+ (tested with Java 25)
- Maven 3.9+
- Node.js 18+ (tested with Node 24)

### Quick Start (all services)

DevOps scripts are provided in `D:\src\devops-scripts\`:

```bash
# Start everything (auth → gateway → backend → frontend)
D:/src/devops-scripts/start-all.sh

# Check status
D:/src/devops-scripts/status.sh

# Stop everything
D:/src/devops-scripts/stop-all.sh
```

### Manual Start

Start services in order: auth-service → market-data-gateway → backend → frontend.

```bash
# 1. Auth Service
cd auth-service
mvn spring-boot:run    # http://localhost:8090

# 2. Market Data Gateway
cd market-data-gateway
mvn spring-boot:run    # http://localhost:8060

# 3. Backend
cd backend
mvn spring-boot:run    # http://localhost:8080

# 4. Frontend
cd frontend
npm install
npm run dev            # http://localhost:3000
```

### URLs

| URL | Description |
|-----|-------------|
| http://localhost:3000 | Frontend |
| http://localhost:8080/swagger-ui.html | Backend Swagger UI |
| http://localhost:8060/swagger-ui.html | Gateway Swagger UI |
| http://localhost:8090/swagger-ui.html | Auth Service Swagger UI |
| http://localhost:8080/h2-console | Backend H2 Console |
| http://localhost:8090/h2-console | Auth Service H2 Console |

### Production

Set the `prod` Spring profile and configure environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/portfolio
DB_USERNAME=postgres
DB_PASSWORD=secret
JWT_SECRET=your-256-bit-secret
GATEWAY_URL=http://gateway-host:8060
AUTH_TOKEN_URL=http://auth-host:8090/api/auth/token
AUTH_CLIENT_ID=portfolio-backend
AUTH_CLIENT_SECRET=change-this-in-production
```

Build the frontend for production:

```bash
cd frontend
npm run build
```

## Project Structure

```
backend/
  src/main/java/com/portfolio/rebalancer/
    config/          # Security, WebSocket, OpenAPI, gateway client config
    controller/      # REST endpoints
    dto/             # Request/response DTOs
    entity/          # JPA entities (User, Portfolio, Holding, Currency, etc.)
    exception/       # Global exception handling
    repository/      # Spring Data JPA repositories
    security/        # JWT filter, token provider, UserDetailsService
    service/         # Business logic, MarketDataClient, ServiceTokenManager
    websocket/       # WebSocket message types

auth-service/
  src/main/java/com/portfolio/auth/
    config/          # Security, OpenAPI config
    controller/      # Auth endpoints (register, login, token, profile)
    dto/             # Auth DTOs, client credentials request/response
    entity/          # User, ServiceClient, AuthProvider
    exception/       # Global exception handling
    repository/      # User + ServiceClient repositories
    security/        # JWT filter, token provider, UserDetailsService
    service/         # Auth logic, Google OAuth, service client seeder

market-data-gateway/
  src/main/java/com/portfolio/gateway/
    config/          # Security, OpenAPI, RestClient (Yahoo User-Agent)
    controller/      # Market data endpoints (quotes, search, history)
    dto/             # QuoteResponse, TickerSearchResult, HistoricalDataPoint
    exception/       # Global exception handling
    security/        # JWT validation filter (service tokens only)
    service/         # YahooFinanceService

frontend/
  src/
    api/             # Axios API clients
    components/      # React components by feature
    store/           # Redux slices and sagas
    types/           # TypeScript interfaces
    utils/           # Formatting helpers
```

## API Overview

### Backend (port 8080)

| Endpoint | Description |
|----------|-------------|
| `POST /api/auth/register` | Register a new user |
| `POST /api/auth/login` | Login and receive JWT |
| `GET /api/portfolios` | List user's portfolios |
| `POST /api/portfolios` | Create a portfolio |
| `GET /api/portfolios/{id}/holdings` | List holdings |
| `POST /api/portfolios/{id}/holdings` | Add a holding |
| `PUT /api/portfolios/{id}/holdings/{hid}` | Update a holding |
| `GET /api/portfolios/{id}/allocations` | Get target allocations |
| `PUT /api/portfolios/{id}/allocations` | Set target allocations |
| `GET /api/portfolios/{id}/rebalance` | Calculate rebalance trades |
| `POST /api/portfolios/{id}/rebalance/cash` | Allocate new cash |
| `GET /api/currencies` | List available currencies |
| `GET /api/quotes?symbols=...` | Fetch live quotes |

### Auth Service (port 8090)

| Endpoint | Description |
|----------|-------------|
| `POST /api/auth/register` | Register a new user |
| `POST /api/auth/login` | Login with username/password |
| `POST /api/auth/google` | Exchange Google ID token for JWT |
| `POST /api/auth/token` | Issue service token (client_credentials) |
| `GET /api/auth/me` | Get current user profile |
| `PUT /api/auth/me` | Update profile |
| `POST /api/auth/me/change-password` | Change password |

### Market Data Gateway (port 8060)

| Endpoint | Description |
|----------|-------------|
| `GET /api/market-data/quotes/{symbol}` | Single quote |
| `GET /api/market-data/quotes?symbols=A,B` | Batch quotes |
| `GET /api/market-data/quotes/{symbol}/history?range=1m` | Historical OHLCV |
| `GET /api/market-data/search?q=apple` | Ticker search |

Gateway endpoints require a service JWT (Bearer token with `type=service`, `scope=market-data`).

## License

MIT
