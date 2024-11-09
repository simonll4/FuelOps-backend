## Proyecto IW3 - 2024
# Backend for a Load Management Application
## FuelOps

We have developed this backend for a load management application. This server performs all the fundamental CRUD operations related to load management with user validation at every step. This project is developed by a team of developers.

## Tech Stack

- Java
- Spring Framework
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL

## Modules

- Login, Logout Module
- Order Module
- Weight Module
- Load Detail Module

## Features

- External interface authentication for clients and internal users authentication & validation with session token having validity of 1 hour for security purposes
- **Order Features:**
    - Create an order with basic details
    - Retrieve order details
    - Cancel an order
- **Weight Features:**
    - Register initial weight details for an order
    - Create validation password to charge
    - Register final weight details for an order
- **Load Detail Features:**
    - Validate password to charge
    - Register load details for an order
    - Close order

## Contributors

- [simonll4](https://github.com/simonll4)
- [MattGoode7](https://github.com/MattGoode7)
- [Pandulc](https://github.com/Pandulc)

## API Documentation
```
http://localhost:8080/swagger-ui.html
```
### API Endpoints
#### 1. Login Module
- POST `/api/v1/login`: Login
#### 2. Order Module
- POST `/api/v1/orders/acknowledge-alarm`: Acknowledge Alarm
- POST `/api/v1/orders/issue-alarm`: Issue Alarm
- GET `/api/v1/orders/conciliation/{id}`: Get Conciliation as PDF or JSON
#### 3. SAP Module
- POST `/api/v1/integration/cli1/orders/b2b`: Create Order
- GET `/api/v1/integration/cli1/orders`: Get Orders
- GET `/api/v1/integration/cli1/orders/{id}`: Get Order by ID
- POST `/api/v1/integration/cli1/orders/cancel`: Cancel Order
#### 3. Weight Module
- POST `/api/v1/integration/cli2/orders/initial-weighing`: Initial Weighing for Order
- POST `/api/v1/integration/cli2/orders/validate-password`: Validate Password for Charge
- POST `/api/v1/integration/cli2/orders/final-weighing`: Final Weighing for Order
#### 4. Load Detail Module
- POST `/api/v1/integration/cli3/orders/validate-password`: Validate Password for Charge
- POST `/api/v1/integration/cli3/orders/detail`: Register Load Details for Order
- POST `/api/v1/integration/cli3/orders/close`: Close Order

## Installation & Run

Before running the API server, you should update the database config inside the `application.properties` file. Update the port number, username, and password as per your local database config.

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/loadmanagementdb
spring.datasource.username=root
spring.datasource.password=root
```
### Commands to Start the Server

#### 1. Start Docker Compose

```sh
docker-compose up -d
```
#### 2. Install Dependencies with npm
```sh
npm install
```

#### 3. Run the Spring Boot Application

You can run the application under two profiles: `mysql_dev` and `mysql_prod`. By default, the application runs under the `mysql_dev` profile.