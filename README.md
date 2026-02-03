# Personal Finance Tracker (Spring Boot 3 + MySQL)

A production‑grade, modular backend for tracking personal finance:
- Manage **accounts**
- Categorize **transactions** (expense, income, transfers) with **tags**
- Define **budgets** and view **spent vs remaining**
- Set **goals** with contributions and progress tracking
- Generate **reports** (monthly summary, category breakdown, trends)
- Stateless **JWT** authentication with multi‑tenant isolation

> **Status:** Auth ✅ · Accounts ✅ · Categories ✅ · Transactions & Tags ✅ · Budgets ✅ · Goals ✅ · Reports ✅  

---

## Tech Stack

- Java 17, Spring Boot 3.x
- Spring Web, Spring Security (JWT), Spring Data JPA (Hibernate 6)
- MySQL 8.x
- Jakarta Validation (`jakarta.validation`)
- Maven

---

## Project Structure

```
src/main/java/com/ey/pft
  auth/
    controller/
    dto/
    security/   (SecurityConfig, JwtAuthFilter, JwtUtil, AppUserDetails, AppUserDetailsService)
    service/
  user/         (User, UserRepository)

  account/
    controller/
    dto/
    service/
    Account.java
    AccountRepository.java

  category/
    controller/
    dto/
    service/
    Category.java
    CategoryRepository.java
    CategorySeeder.java

  transaction/
    controller/
    dto/
    service/
    Transaction.java
    TransactionRepository.java
    tag/
      Tag.java
      TagRepository.java

  budget/
    controller/
    dto/
    service/
    Budget.java
    BudgetLine.java
    BudgetRepository.java
    BudgetLineRepository.java

  goals/
    controller/
    dto/
    service/
    Goal.java
    GoalContribution.java
    GoalRepository.java
    GoalContributionRepository.java

  report/
    controller/
    dto/
    service/

  common/
    exception/  (ApiError, BadRequestException, ResourceNotFoundException, GlobalExceptionHandler)
    util/       (SecurityUtils)

PftApplication.java
```

**Conventions**
- Controllers use **DTOs**; entities are not exposed
- Controllers are thin; **services** hold business logic
- Global error payload via `@ControllerAdvice`
- Pageable list endpoints where relevant
- JWT auth; only `/api/v1/auth/**` and `GET /api/v1/test` are public

---

## Setup

1. **Create database**
   ```sql
   CREATE DATABASE personal_finance_tracker;
   ```

2. **Run the app**
   ```bash
   mvn spring-boot:run
   ```

3. **Seeded categories (on startup)**
   - EXPENSE: Food, Rent, Travel, Utilities
   - INCOME: Salary
   - TRANSFER: Transfer

---

## Authentication Flow

1) **Register**
```http
POST /api/v1/auth/register
Content-Type: application/json
```
**Request**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "StrongPassw0rd!"
}
```
**Response 200**
```json
{ "status": "registered" }
```

2) **Login**
```http
POST /api/v1/auth/login
Content-Type: application/json
```
**Request**
```json
{
  "email": "john@example.com",
  "password": "StrongPassw0rd!"
}
```
**Response 200**
```json
{
  "accessToken": "<JWT_TOKEN>",
  "tokenType": "Bearer",
  "expiresInSeconds": 1800
}
```
Use `Authorization: Bearer <JWT_TOKEN>` for protected endpoints.

---

## Error Format (Global)

**Example**
```json
{
  "timestamp": "2026-02-02T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation error",
  "path": "/api/v1/transactions"
}
```

---

# API Endpoints (Detailed)

> Base URL: `http://localhost:8080`  
> All endpoints require **Authorization: Bearer <token>** unless marked **Public**.

---

## Health (Public)

### GET `/api/v1/test`
**Response 200**
```json
{ "status": "ok", "message": "public test endpoint" }
```

---

## Auth (Public)

### POST `/api/v1/auth/register`
See **Authentication Flow** above.

### POST `/api/v1/auth/login`
See **Authentication Flow** above.

---

## Accounts

### POST `/api/v1/accounts`
**Request**
```json
{
  "name": "HDFC Savings",
  "type": "BANK",
  "currency": "INR",
  "currentBalance": 25000.00
}
```
**Response 201**
```json
{
  "id": "d3b5c1b7-1ad8-49de-bf0c-51b2b6d2d111",
  "name": "HDFC Savings",
  "type": "BANK",
  "currency": "INR",
  "currentBalance": 25000.00,
  "status": "ACTIVE",
  "createdAt": "2026-02-02T09:00:00",
  "updatedAt": "2026-02-02T09:00:00"
}
```

### GET `/api/v1/accounts?page=0&size=20&sort=createdAt,desc`
**Response 200 (Page)**
```json
{
  "content": [ { "id": "...", "name": "HDFC Savings", "type": "BANK", "currency": "INR", "currentBalance": 25000.0, "status": "ACTIVE", "createdAt": "...", "updatedAt": "..." } ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### GET `/api/v1/accounts/{id}`
**Response 200** – same shape as create response.

### PATCH `/api/v1/accounts/{id}`
**Request (any subset)**
```json
{ "name": "HDFC Main Savings", "currentBalance": 27500.50 }
```
**Response 200** – updated account.

### DELETE `/api/v1/accounts/{id}`
**Response 204** (status becomes `ARCHIVED`).

---

## Categories

### GET `/api/v1/categories?type=EXPENSE|INCOME|TRANSFER`
**Response 200**
```json
[
  { "id": "...", "name": "Food", "type": "EXPENSE", "parentId": null, "system": true,  "createdAt": "...", "updatedAt": "..." },
  { "id": "...", "name": "Groceries", "type": "EXPENSE", "parentId": "<FoodId>", "system": false, "createdAt": "...", "updatedAt": "..." }
]
```

### POST `/api/v1/categories`
**Request**
```json
{ "name": "Groceries", "type": "EXPENSE", "parentId": "<FoodId>" }
```
**Response 200** – `CategoryResponse` as above.

---

## Transactions & Tags

### POST `/api/v1/transactions` (expense/income)
**Request**
```json
{
  "accountId": "<ACCOUNT_ID>",
  "categoryId": "<CATEGORY_ID>",
  "type": "EXPENSE",
  "amount": 450.00,
  "currency": "INR",
  "exchangeRate": null,
  "description": "Dinner",
  "merchant": "Swiggy",
  "transactionDate": "2026-02-02",
  "tags": ["food", "weekend"]
}
```
**Response 201**
```json
{
  "id": "...",
  "accountId": "...",
  "accountName": "HDFC Savings",
  "categoryId": "...",
  "categoryName": "Food",
  "type": "EXPENSE",
  "amount": 450.00,
  "currency": "INR",
  "exchangeRate": null,
  "amountBaseCurrency": null,
  "description": "Dinner",
  "merchant": "Swiggy",
  "transactionDate": "2026-02-02",
  "status": "ACTIVE",
  "transferGroupId": null,
  "tags": [ { "id": "...", "name": "food" }, { "id": "...", "name": "weekend" } ],
  "createdAt": "...",
  "updatedAt": "..."
}
```

### POST `/api/v1/transactions/transfer`
**Request**
```json
{
  "fromAccountId": "<ACCOUNT_A>",
  "toAccountId": "<ACCOUNT_B>",
  "amount": 2000.00,
  "currency": "INR",
  "transactionDate": "2026-02-02",
  "description": "Move to wallet"
}
```
**Response 201** – returns one leg ("from").
```json
{
  "id": "...",
  "type": "TRANSFER",
  "amount": 2000.00,
  "transferGroupId": "c9b1e1f2-...",
  "accountId": "<ACCOUNT_A>",
  "accountName": "HDFC Savings",
  "categoryName": "Transfer",
  "transactionDate": "2026-02-02",
  "status": "ACTIVE"
}
```

### GET `/api/v1/transactions?from=&to=&categoryId=&type=&page=&size=&sort=`
**Example**: `?from=2026-02-01&to=2026-02-28&type=EXPENSE&page=0&size=10&sort=transactionDate,desc`

**Response 200 (Page)**
```json
{
  "content": [ { "id": "...", "type": "EXPENSE", "amount": 450.00, "transactionDate": "2026-02-02", ... } ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### GET `/api/v1/transactions/{id}`
**Response 200** – `TransactionResponse`.

### PATCH `/api/v1/transactions/{id}`
**Request (subset)**
```json
{ "description": "Dinner with friends", "merchant": "Zomato" }
```
**Response 200** – updated `TransactionResponse`.

### DELETE `/api/v1/transactions/{id}`
**Response 204** – soft delete + balance reversal (both legs if transfer).

### POST `/api/v1/transactions/{id}/tags`
**Request**
```json
{ "names": ["friends", "nightout"] }
```
**Response 200**
```json
[
  { "id": "...", "name": "friends" },
  { "id": "...", "name": "nightout" },
  { "id": "...", "name": "food" }
]
```

### DELETE `/api/v1/transactions/{id}/tags/{tagId}`
**Response 204**

---

## Budgets

### POST `/api/v1/budgets`
**Request**
```json
{
  "name": "Feb 2026 - Monthly Budget",
  "period": "MONTHLY",
  "startDate": "2026-02-01",
  "currency": "INR",
  "totalLimit": 50000.00,
  "lines": [
    { "categoryId": "<FoodId>", "threshold": 12000.00 },
    { "categoryId": "<TravelId>", "threshold": 8000.00 }
  ]
}
```
**Response 201** – `BudgetResponse`
```json
{
  "id": "...",
  "name": "Feb 2026 - Monthly Budget",
  "period": "MONTHLY",
  "startDate": "2026-02-01",
  "currency": "INR",
  "totalLimit": 50000.00,
  "status": "ACTIVE",
  "lines": [
    { "id": "...", "categoryId": "<FoodId>", "categoryName": "Food", "threshold": 12000.00 },
    { "id": "...", "categoryId": "<TravelId>", "categoryName": "Travel", "threshold": 8000.00 }
  ],
  "createdAt": "...",
  "updatedAt": "..."
}
```

### GET `/api/v1/budgets?page=0&size=10&sort=startDate,desc`
**Response 200 (Page)** – page of `BudgetResponse` items.

### GET `/api/v1/budgets/{id}`
**Response 200** – `BudgetResponse`.

### PATCH `/api/v1/budgets/{id}`
**Request (subset)**
```json
{ "name": "Feb 2026 Budget - Updated", "totalLimit": 52000.00 }
```
**Response 200** – updated `BudgetResponse`.

### PUT `/api/v1/budgets/{id}/lines`
**Request**
```json
[
  { "categoryId": "<UtilitiesId>", "threshold": 6000.00 },
  { "categoryId": "<FoodId>", "threshold": 15000.00 }
]
```
**Response 200** – `BudgetResponse` with replaced lines.

### DELETE `/api/v1/budgets/{id}`
**Response 204** – archived.

### PATCH `/api/v1/budgets/{id}/unarchive`
**Response 200** – `BudgetResponse` with `status: ACTIVE`.

### GET `/api/v1/budgets/{id}/summary`
**Response 200** – `BudgetSummaryResponse`
```json
{
  "budgetId": "...",
  "name": "Feb 2026 - Monthly Budget",
  "currency": "INR",
  "totalLimit": 50000.00,
  "totalSpent": 13450.00,
  "totalRemaining": 36550.00,
  "lines": [
    { "categoryId": "<FoodId>", "categoryName": "Food", "threshold": 12000.00, "spent": 9450.00, "remaining": 2550.00 },
    { "categoryId": "<TravelId>", "categoryName": "Travel", "threshold": 8000.00,  "spent": 4000.00, "remaining": 4000.00 }
  ]
}
```

---

## Goals (Savings)

### POST `/api/v1/goals`
**Request**
```json
{
  "name": "Buy Laptop",
  "targetAmount": 100000,
  "currency": "INR",
  "targetDate": "2026-12-31"
}
```
**Response 201** – `GoalResponse`
```json
{
  "id": "...",
  "name": "Buy Laptop",
  "targetAmount": 100000,
  "currency": "INR",
  "targetDate": "2026-12-31",
  "status": "ACTIVE",
  "totalContributed": 0,
  "remaining": 100000,
  "percentComplete": 0.0,
  "contributions": []
}
```

### GET `/api/v1/goals?page=0&size=20&sort=createdAt,desc`
**Response 200 (Page)** – page of `GoalResponse` items.

### GET `/api/v1/goals/{id}`
**Response 200** – single `GoalResponse` including contributions & progress.

### PATCH `/api/v1/goals/{id}`
**Request (subset)**
```json
{ "name": "Laptop Fund", "targetAmount": 120000, "targetDate": "2026-11-30" }
```
**Response 200** – updated `GoalResponse`.

### DELETE `/api/v1/goals/{id}`
**Response 204** – archived.

### PATCH `/api/v1/goals/{id}/unarchive`
**Response 200** – `GoalResponse` with `status: ACTIVE`.

### POST `/api/v1/goals/{id}/contributions`
**Request**
```json
{ "amount": 5000, "contributionDate": "2026-02-02" }
```
**Response 201**
```json
{ "id": "...", "amount": 5000, "contributionDate": "2026-02-02" }
```

---

## Reports

### GET `/api/v1/reports/monthly-summary?year=YYYY&month=MM`
**Response 200** – `MonthlySummaryResponse`
```json
{
  "year": 2026,
  "month": 2,
  "fromDate": "2026-02-01",
  "toDate": "2026-02-28",
  "totalIncome": 15000.00,
  "totalExpense": 9000.00,
  "net": 6000.00,
  "expenseByCategory": [
    { "categoryId": "<FoodId>", "categoryName": "Food", "total": 5000.00 },
    { "categoryId": "<TravelId>", "categoryName": "Travel", "total": 3000.00 }
  ],
  "accountBalances": [
    { "accountId": "<Acc1>", "accountName": "HDFC Savings", "accountType": "BANK", "currency": "INR", "currentBalance": 27500.50 }
  ]
}
```

### GET `/api/v1/reports/category-breakdown?from=YYYY-MM-DD&to=YYYY-MM-DD&type=EXPENSE|INCOME`
**Response 200** – `CategoryBreakdownResponse`
```json
{
  "fromDate": "2026-02-01",
  "toDate": "2026-02-28",
  "type": "EXPENSE",
  "grandTotal": 9000.00,
  "items": [
    { "categoryId": "<FoodId>", "categoryName": "Food",   "total": 5000.00, "percentage": 55.56 },
    { "categoryId": "<TravelId>", "categoryName": "Travel", "total": 3000.00, "percentage": 33.33 }
  ]
}
```

### GET `/api/v1/reports/trends?months=6`
**Response 200** – `TrendResponse`
```json
{
  "months": 6,
  "points": [
    { "year": 2025, "month": 9, "income": 10000.00, "expense": 8000.00, "net": 2000.00 },
    { "year": 2025, "month": 10, "income": 12000.00, "expense": 7000.00, "net": 5000.00 }
  ]
}
```

---

## Security Notes
- Stateless JWT; include `Authorization: Bearer <token>` on protected endpoints
- All data access is scoped to the authenticated user via `userId`
- Passwords hashed with BCrypt

---

## Development
- Build: `mvn clean package`
- Run: `mvn spring-boot:run`
- Java: 17
- DTO-based controllers

---


