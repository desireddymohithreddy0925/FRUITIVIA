# Fruitivia - B2B International Fruit Export Management Platform

Welcome to the **Fruitivia** project repository! This is a comprehensive, production-grade B2B international fruit export management platform designed specifically for an Indian fruit export company.

## 🚀 Overview

Fruitivia connects Indian farmers with international buyers, streamlining the entire agricultural export lifecycle—from farm procurement and quality inspection to international logistics and final delivery.

### Key Workflows:
1. **Procurement**: Indian farmers sell fruits to Fruitivia.
2. **Quality Control**: Fruits are inspected, sorted, and stored.
3. **Inventory**: Batches and lots are managed meticulously.
4. **B2B Commerce**: International buyers browse stock, request quotations, and place orders.
5. **Logistics**: Orders are packed, export documentation is automatically prepared, and shipments are dispatched and tracked.

## 🛠 Tech Stack

The application is built using a modern, scalable architecture:

### Backend (`/fruitivia-backend`)
- **Framework**: Java 21 + Spring Boot 3.x
- **Architecture**: Modular Monolith
- **Security**: JWT-based Stateless Authentication, Role-based Access Control (RBAC)
- **Database**: H2 (In-Memory for Development) / PostgreSQL (Production)
- **API Documentation**: OpenAPI / Swagger UI

### Frontend (`/fruitivia-frontend`)
- **Framework**: React 19 + TypeScript + Vite
- **Styling**: Tailwind CSS (Custom themed with Fruitivia brand colors)
- **State Management**: React Context / Hooks
- **Icons**: Lucide React

## 📂 Project Structure

```text
FRUITIVIA/
├── fruitivia-backend/   # Spring Boot REST API
│   ├── src/main/java/com/fruitivia/
│   │   ├── auth/        # JWT Authentication & Registration
│   │   ├── config/      # Spring Security & App Config
│   │   ├── user/        # Admin & System User Management
│   │   ├── farmer/      # Farmer & Farm Profiles
│   │   └── buyer/       # International B2B Buyer Profiles
│   └── pom.xml          # Maven Configuration
│
└── fruitivia-frontend/  # React Vite Application
    ├── src/
    │   ├── layouts/     # Admin Sidebar & Routing Layouts
    │   ├── pages/       # Admin Dashboards & Data Tables
    │   ├── services/    # Axios API Client
    │   └── assets/      # Brand Images & Icons
    ├── tailwind.config.js
    └── package.json
```

## 🏃‍♂️ Getting Started

Follow these steps to run the application locally on your machine.

### 1. Run the Backend Server
The backend is configured to use an H2 in-memory database out of the box, so no Docker or external database setup is required for local development.

```bash
cd fruitivia-backend
./mvnw spring-boot:run
```
The backend API will start on `http://localhost:8080`.

### 2. Run the Frontend Development Server
In a new terminal tab, start the Vite development server:

```bash
cd fruitivia-frontend
npm install
npm run dev
```
The web application will be accessible at `http://localhost:5173`.

## 🔒 Roles & Access

The platform utilizes strict Role-Based Access Control (RBAC). The following roles are supported:
- `ADMIN`: Full access to the platform.
- `FARMER`: Access to procurement, farm details, and payment histories.
- `BUYER`: Access to fruit inventory, quotation requests, and order tracking.
- `ENGINEER`: Technical support and system monitoring.
- `LOGISTICS`: Shipment, packaging, and documentation management.

## 🚢 Future Phases

- **Phase 3**: Fruit Inventory Management (Batches, Warehousing).
- **Phase 4**: Purchasing & B2B Order Management.
- **Phase 5**: Quality Inspection & Logistics.
- **Phase 23**: Optional AI integrations for market predictions and automated support.