# Portfolio Rebalancer

A full-stack portfolio management and rebalancing application. Track your holdings, monitor live prices, and get trade recommendations to keep your portfolio aligned with target allocations.

## Features

- **Portfolio Management** — Create and manage multiple investment portfolios
- **Holdings Tracking** — Add stocks/ETFs with quantity, cost basis, and currency
- **Live Prices** — Real-time quotes from Yahoo Finance via WebSocket (STOMP)
- **Target Allocations** — Define target percentages per asset class (Equity, Bond, Commodity, Real Estate, Cash)
- **Rebalancing Engine** — Calculate trades needed to realign with targets, or allocate new cash optimally
- **Profit/Loss** — Track cost basis and see P/L per holding
- **Multi-Currency** — Holdings and rebalancing displayed in the correct currency
- **User Settings** — Profile management, password changes, currency configuration

## Tech Stack

### Backend
- Java 17+ / Spring Boot 3.4.3
- Spring Security with JWT authentication (jjwt)
- Spring Data JPA with H2 (dev) / PostgreSQL (prod)
- Spring WebSocket (STOMP) for live price updates
- Yahoo Finance API integration for market data
- OpenAPI/Swagger UI via springdoc

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

### Backend

```bash
cd backend
mvn spring-boot:run
```

The backend starts on http://localhost:8080 with an H2 file database by default (dev profile).

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend starts on http://localhost:3000.

### Production

Set the `prod` Spring profile and configure environment variables:

```bash
DB_URL=jdbc:postgresql://localhost:5432/portfolio
DB_USERNAME=postgres
DB_PASSWORD=secret
JWT_SECRET=your-256-bit-secret
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
    config/          # Security, WebSocket, OpenAPI, RestClient config
    controller/      # REST endpoints
    dto/             # Request/response DTOs
    entity/          # JPA entities (User, Portfolio, Holding, Currency, etc.)
    exception/       # Global exception handling
    repository/      # Spring Data JPA repositories
    security/        # JWT filter, token provider, UserDetailsService
    service/         # Business logic (rebalancing, prices, auth)
    websocket/       # WebSocket message types

frontend/
  src/
    api/             # Axios API clients
    components/      # React components by feature
    store/           # Redux slices and sagas
    types/           # TypeScript interfaces
    utils/           # Formatting helpers
```

## API Overview

| Endpoint | Description |
|---|---|
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

## License

MIT
