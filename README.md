# 💸 CASH DESK MODULE

## 📖 Description

This project implements a Cash Desk module for managing **cash operations** such as **deposits**, **withdrawals**, and **balance checks** across **multiple cashiers** in **BGN** and **EUR** currencies.

The system supports querying current balances with detailed denomination breakdowns and filtering by **cashier name** and **date range**. Transactions and balances are persisted in simple TXT files to ensure lightweight and efficient storage.

---

## ✅ Features

- Initialize 3 cashiers: `MARTINA`, `PETER`, `LINDA`
- Support operations:
    - Deposit
    - Withdrawal
    - Balance Check
- Support for **BGN** and **EUR**
- Track and log:
    - Amounts
    - Denominations
    - Timestamps
- Filter balances by:
    - `cashierName`
    - `dateFrom`
    - `dateTo`
- Request validation using Spring Boot validation annotations
- API secured using custom header `FIB-X-AUTH`
- Lightweight persistence using `.txt` files

---

## 🔗 API Endpoints Summary

| HTTP Method | Endpoint                  | Description                                |
|-------------|---------------------------|--------------------------------------------|
| `POST`      | `/api/v1/cash-operation`  | Perform deposit or withdrawal              |
| `GET`       | `/api/v1/cash-balance`    | Retrieve current balance with filters      |

---

## 🔧 Technical Stack

- Java 17
- Spring Boot 3.x
- Maven
- SLF4J (Logging)
- JUnit + Mockito (Testing)
- Postman (Collection & Environment, Import them in Postman) 

---



## 🛠 Setup & Run

### Prerequisites

- Java 17+
- Maven 3.6+
- Set the API key as an environment variable:

```bash
export FIB_API_KEY=f9Uie8nNf112hx8s
FIB_API_KEY=f9Uie8nNf112hx8s mvn spring-boot:run

