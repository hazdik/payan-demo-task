# CRUD API Documentation

This document provides comprehensive documentation for the CRUD (Create, Read, Update, Delete) operations implemented in the Payan Demo Task application.

## Table of Contents
1. [Overview](#overview)
2. [User API Endpoints](#user-api-endpoints)
3. [Transaction API Endpoints](#transaction-api-endpoints)
4. [Testing](#testing)
5. [Running the Tests](#running-the-tests)

---

## Overview

This application provides RESTful API endpoints for managing Users and Transactions with full CRUD operations. All endpoints follow RESTful conventions and return appropriate HTTP status codes.

### Base URL
- Local Development: `http://localhost:8080`

### Content Type
All requests and responses use `application/json`

---

## User API Endpoints

### 1. Create User
**Endpoint:** `POST /api/users`

**Description:** Creates a new user with encrypted password.

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "enabled": true
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "username": "john_doe",
  "password": "$2a$10$...", // encrypted
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "enabled": true
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "fullName": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "enabled": true
  }'
```

---

### 2. Get All Users
**Endpoint:** `GET /api/users`

**Description:** Retrieves all users.

**Response:** `200 OK` or `204 No Content`
```json
[
  {
    "id": 1,
    "username": "john_doe",
    "fullName": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "enabled": true
  },
  {
    "id": 2,
    "username": "jane_doe",
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "role": "ADMIN",
    "enabled": true
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/users
```

---

### 3. Get User by ID
**Endpoint:** `GET /api/users/{id}`

**Description:** Retrieves a specific user by ID.

**Path Parameters:**
- `id` (Long) - User ID

**Response:** `200 OK` or `404 Not Found`
```json
{
  "id": 1,
  "username": "john_doe",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "enabled": true
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/users/1
```

---

### 4. Get User by Username
**Endpoint:** `GET /api/users/username/{username}`

**Description:** Retrieves a specific user by username.

**Path Parameters:**
- `username` (String) - Username

**Response:** `200 OK` or `404 Not Found`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/users/username/john_doe
```

---

### 5. Update User
**Endpoint:** `PUT /api/users/{id}`

**Description:** Updates an existing user. Password is only updated if provided.

**Path Parameters:**
- `id` (Long) - User ID

**Request Body:**
```json
{
  "username": "john_doe_updated",
  "password": "newpassword123", // Optional
  "fullName": "John Updated",
  "email": "john.updated@example.com",
  "role": "ADMIN",
  "enabled": false
}
```

**Response:** `200 OK` or `404 Not Found`

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe_updated",
    "fullName": "John Updated",
    "email": "john.updated@example.com",
    "role": "ADMIN",
    "enabled": false
  }'
```

---

### 6. Delete User
**Endpoint:** `DELETE /api/users/{id}`

**Description:** Deletes a user by ID.

**Path Parameters:**
- `id` (Long) - User ID

**Response:** `204 No Content` or `404 Not Found`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

---

### 7. Toggle User Status
**Endpoint:** `PATCH /api/users/{id}/toggle-status`

**Description:** Enables or disables a user account.

**Path Parameters:**
- `id` (Long) - User ID

**Response:** `200 OK` or `404 Not Found`
```json
{
  "id": 1,
  "username": "john_doe",
  "fullName": "John Doe",
  "email": "john@example.com",
  "role": "USER",
  "enabled": false
}
```

**cURL Example:**
```bash
curl -X PATCH http://localhost:8080/api/users/1/toggle-status
```

---

### 8. Check Username Exists
**Endpoint:** `GET /api/users/exists/{username}`

**Description:** Checks if a username already exists.

**Path Parameters:**
- `username` (String) - Username to check

**Response:** `200 OK`
```json
true
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/users/exists/john_doe
```

---

## Transaction API Endpoints

### 1. Create Transaction
**Endpoint:** `POST /api/transactions`

**Description:** Creates a new transaction. Transaction ID and date are auto-generated if not provided.

**Request Body:**
```json
{
  "description": "Monthly Salary",
  "amount": 5000.00,
  "type": "CREDIT",
  "status": "COMPLETED",
  "category": "Salary",
  "reference": "REF-001"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "transactionId": "TXN-A1B2C3D4",
  "description": "Monthly Salary",
  "amount": 5000.00,
  "type": "CREDIT",
  "status": "COMPLETED",
  "transactionDate": "2025-11-10T15:30:00",
  "category": "Salary",
  "reference": "REF-001"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Monthly Salary",
    "amount": 5000.00,
    "type": "CREDIT",
    "status": "COMPLETED",
    "category": "Salary",
    "reference": "REF-001"
  }'
```

---

### 2. Get All Transactions
**Endpoint:** `GET /api/transactions`

**Description:** Retrieves all transactions ordered by date (descending).

**Response:** `200 OK` or `204 No Content`
```json
[
  {
    "id": 1,
    "transactionId": "TXN-A1B2C3D4",
    "description": "Monthly Salary",
    "amount": 5000.00,
    "type": "CREDIT",
    "status": "COMPLETED",
    "transactionDate": "2025-11-10T15:30:00",
    "category": "Salary",
    "reference": "REF-001"
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/transactions
```

---

### 3. Get Transaction by ID
**Endpoint:** `GET /api/transactions/{id}`

**Description:** Retrieves a specific transaction by ID.

**Path Parameters:**
- `id` (Long) - Transaction ID

**Response:** `200 OK` or `404 Not Found`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/transactions/1
```

---

### 4. Get Transactions by Status
**Endpoint:** `GET /api/transactions/status/{status}`

**Description:** Retrieves all transactions with a specific status.

**Path Parameters:**
- `status` (String) - Transaction status (PENDING, COMPLETED, FAILED)

**Response:** `200 OK` or `204 No Content`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/transactions/status/COMPLETED
```

---

### 5. Get Transactions by Type
**Endpoint:** `GET /api/transactions/type/{type}`

**Description:** Retrieves all transactions of a specific type.

**Path Parameters:**
- `type` (String) - Transaction type (CREDIT, DEBIT)

**Response:** `200 OK` or `204 No Content`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/transactions/type/CREDIT
```

---

### 6. Get Transactions by Category
**Endpoint:** `GET /api/transactions/category/{category}`

**Description:** Retrieves all transactions in a specific category.

**Path Parameters:**
- `category` (String) - Transaction category (e.g., Salary, Shopping, Bills)

**Response:** `200 OK` or `204 No Content`

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/transactions/category/Salary
```

---

### 7. Update Transaction
**Endpoint:** `PUT /api/transactions/{id}`

**Description:** Updates an existing transaction.

**Path Parameters:**
- `id` (Long) - Transaction ID

**Request Body:**
```json
{
  "description": "Updated Description",
  "amount": 6000.00,
  "type": "CREDIT",
  "status": "COMPLETED",
  "category": "Bonus",
  "reference": "REF-002"
}
```

**Response:** `200 OK` or `404 Not Found`

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/transactions/1 \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Updated Description",
    "amount": 6000.00,
    "type": "CREDIT",
    "status": "COMPLETED",
    "category": "Bonus",
    "reference": "REF-002"
  }'
```

---

### 8. Update Transaction Status
**Endpoint:** `PATCH /api/transactions/{id}/status`

**Description:** Updates only the status of a transaction.

**Path Parameters:**
- `id` (Long) - Transaction ID

**Query Parameters:**
- `status` (String) - New status (PENDING, COMPLETED, FAILED)

**Response:** `200 OK` or `404 Not Found`

**cURL Example:**
```bash
curl -X PATCH "http://localhost:8080/api/transactions/1/status?status=FAILED"
```

---

### 9. Delete Transaction
**Endpoint:** `DELETE /api/transactions/{id}`

**Description:** Deletes a transaction by ID.

**Path Parameters:**
- `id` (Long) - Transaction ID

**Response:** `204 No Content` or `404 Not Found`

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/transactions/1
```

---

## Testing

### Test Coverage

The application includes comprehensive test suites:

#### Service Layer Tests
1. **UserServiceTest** - Tests for User CRUD operations
   - Create user with password encryption
   - Get all users
   - Get user by ID and username
   - Update user (with and without password)
   - Delete user
   - Toggle user status
   - Check username existence

2. **TransactionServiceTestComplete** - Tests for Transaction CRUD operations
   - Create transaction with/without transaction ID
   - Get all transactions
   - Get transaction by ID
   - Get transactions by status, type, and category
   - Update transaction
   - Delete transaction
   - Update transaction status

#### Controller Layer Tests
1. **UserControllerTest** - API endpoint tests for User operations
   - All HTTP methods (GET, POST, PUT, PATCH, DELETE)
   - Success and error scenarios
   - HTTP status code validation

2. **TransactionControllerTest** - API endpoint tests for Transaction operations
   - All HTTP methods (GET, POST, PUT, PATCH, DELETE)
   - Success and error scenarios
   - HTTP status code validation

---

## Running the Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserServiceTest
mvn test -Dtest=TransactionServiceTestComplete
mvn test -Dtest=UserControllerTest
mvn test -Dtest=TransactionControllerTest
```

### Run Tests with Coverage Report
```bash
mvn clean test jacoco:report
```

### View Test Results
Test results will be available in:
- Console output
- `target/surefire-reports/` directory
- Coverage report (if jacoco is configured): `target/site/jacoco/index.html`

---

## HTTP Status Codes

The API uses standard HTTP status codes:

- `200 OK` - Successful GET, PUT, or PATCH request
- `201 Created` - Successful POST request
- `204 No Content` - Successful DELETE request or empty result
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Notes

1. **Password Encryption**: User passwords are automatically encrypted using BCrypt before storage.
2. **Transaction ID Generation**: Transaction IDs are automatically generated in format `TXN-XXXXXXXX` if not provided.
3. **Transaction Date**: If not provided, the current date/time is automatically set.
4. **Validation**: All required fields must be provided in request bodies.
5. **Security**: Remember to implement authentication and authorization for production use.

---

## Additional Information

### Entity Relationships
- Users are standalone entities
- Transactions are standalone entities
- No direct relationship between User and Transaction in current implementation

### Future Enhancements
Consider implementing:
- User-Transaction relationship (owner/creator)
- Pagination for list operations
- Advanced filtering and search
- Batch operations
- Transaction history tracking
- Input validation with Bean Validation
- Exception handling with custom error responses
- API versioning
- Authentication and authorization
- Rate limiting

---

**Last Updated:** November 10, 2025
**Version:** 1.0.0
