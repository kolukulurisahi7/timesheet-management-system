# Timesheet Management System

A modern Spring Boot-based Timesheet Management System that enables employees to create, submit, and track weekly timesheets while allowing managers and admins to review, approve, or reject them.

## 🎯 Features

- **User Authentication**
  - User registration and login with email/password
  - JWT token-based authentication
  - Two user roles: USER and ADMIN

- **Timesheet Management**
  - Create weekly timesheets
  - Add daily work entries with hours and task descriptions
  - Submit timesheets for review
  - Track timesheet status (DRAFT, SUBMITTED, APPROVED, REJECTED)

- **Admin Capabilities**
  - Review submitted timesheets
  - Approve or reject timesheets with optional rejection reasons
  - View all pending timesheets

- **Security**
  - Spring Security with JWT tokens
  - Password encryption using BCrypt
  - Role-based access control
  - CORS support for frontend integration

## 📚 Tech Stack

- **Java 17** - Modern Java with records and sealed classes support
- **Spring Boot 3.2.0** - Latest version with Virtual Threads support
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations with Hibernate
- **JWT (JJWT)** - Secure token generation and validation
- **H2 Database** - In-memory database for development and testing
- **Maven** - Build and dependency management
- **Lombok** - Reduce boilerplate code

## 🗄️ Database Design

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Timesheets Table
```sql
CREATE TABLE timesheets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    week_start_date DATE NOT NULL,
    week_end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    rejection_reason VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    approved_by_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES users(id),
    FOREIGN KEY (approved_by_id) REFERENCES users(id)
);
```

### Timesheet Entries Table
```sql
CREATE TABLE timesheet_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    timesheet_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    hours_worked DECIMAL(5, 2) NOT NULL,
    task VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (timesheet_id) REFERENCES timesheets(id)
);
```

## 🔌 API Endpoints

### Authentication Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|-------------|
| POST | `/api/auth/register` | Register a new user | No |
| POST | `/api/auth/login` | Login and get JWT token | No |

### Timesheet Endpoints
| Method | Endpoint | Description | Auth Required | Role |
|--------|----------|-------------|--------------|------|
| POST | `/api/timesheets` | Create a new timesheet | Yes | USER |
| POST | `/api/timesheets/{id}/entries` | Add entry to timesheet | Yes | USER |
| GET | `/api/timesheets/my` | Get all user's timesheets | Yes | USER |
| GET | `/api/timesheets/{id}` | Get timesheet by ID | Yes | USER |
| PUT | `/api/timesheets/{id}/submit` | Submit timesheet | Yes | USER |
| PUT | `/api/timesheets/{id}/approve` | Approve timesheet | Yes | ADMIN |
| PUT | `/api/timesheets/{id}/reject` | Reject timesheet | Yes | ADMIN |
| GET | `/api/timesheets/pending` | Get all pending timesheets | Yes | ADMIN |

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Docker (optional)

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/kolukulurisahi7/timesheet-management-system.git
   cd timesheet-management-system
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - Main API: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:timesheetdb`
     - Username: `sa`
     - Password: (leave blank)

## 📋 API Request/Response Examples

### 1. Register User

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

### 2. Login

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. Create Timesheet

**Request:**
```bash
curl -X POST http://localhost:8080/api/timesheets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "weekStartDate": "2024-03-25",
    "weekEndDate": "2024-03-31"
  }'
```

**Response:**
```json
{
  "id": 1,
  "weekStartDate": "2024-03-25",
  "weekEndDate": "2024-03-31",
  "status": "DRAFT",
  "entries": [],
  "createdAt": "2024-03-25T10:30:00"
}
```

### 4. Add Timesheet Entry

**Request:**
```bash
curl -X POST http://localhost:8080/api/timesheets/1/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "workDate": "2024-03-25",
    "hoursWorked": 8.5,
    "task": "Implemented user authentication"
  }'
```

### 5. Submit Timesheet

**Request:**
```bash
curl -X PUT http://localhost:8080/api/timesheets/1/submit \
  -H "Authorization: Bearer {token}"
```

### 6. Approve Timesheet (Admin)

**Request:**
```bash
curl -X PUT http://localhost:8080/api/timesheets/1/approve \
  -H "Authorization: Bearer {admin-token}"
```

### 7. Reject Timesheet (Admin)

**Request:**
```bash
curl -X PUT http://localhost:8080/api/timesheets/1/reject \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {admin-token}" \
  -d '{
    "reason": "Missing entries for some days"
  }'
```

### 8. Get Pending Timesheets (Admin)

**Request:**
```bash
curl -X GET http://localhost:8080/api/timesheets/pending \
  -H "Authorization: Bearer {admin-token}"
```

## 🧪 Testing

### Using Postman

1. Import the provided Postman collection
2. Set up environment variables:
   - `base_url`: `http://localhost:8080`
   - `token`: (extracted from login response)
   - `admin_token`: (extracted from admin login)

### Using curl

See API Request/Response Examples section above.

### Using Maven

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthenticationControllerTest

# Run with coverage
mvn clean verify
```

## 🔐 Security Features

- **JWT Authentication**: Stateless, token-based authentication
- **Password Encryption**: BCrypt algorithm for secure password storage
- **Role-Based Access Control**: USER and ADMIN roles with specific permissions
- **CORS Configuration**: Configurable cross-origin resource sharing
- **H2 Console Security**: Console disabled in production

## 📁 Project Structure

```
timesheet-management-system/
├── src/main/java/com/timesheet/management/
│   ├── controller/              # REST API controllers
│   │   ├── AuthenticationController.java
│   │   └── TimesheetController.java
│   ├── service/                 # Business logic
│   │   ├── AuthenticationService.java
│   │   ├── TimesheetService.java
│   │   └── CustomUserDetailsService.java
│   ├── entity/                  # JPA entities
│   │   ├── User.java
│   │   ├── Timesheet.java
│   │   ├── TimesheetEntry.java
│   │   ├── UserRole.java
│   │   └── TimesheetStatus.java
│   ├── repository/              # Data access layer
│   │   ├── UserRepository.java
│   │   ├── TimesheetRepository.java
│   │   └── TimesheetEntryRepository.java
│   ├── dto/                     # Data transfer objects
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── CreateTimesheetRequest.java
│   │   ├── TimesheetEntryRequest.java
│   │   ├── TimesheetEntryResponse.java
│   │   ├── TimesheetResponse.java
│   │   └── RejectionRequest.java
│   ├── security/                # Security configurations
│   │   ├── JwtTokenProvider.java
│   │   └── JwtAuthenticationFilter.java
│   ├── config/                  # Application configuration
│   │   └── SecurityConfig.java
│   ├── exception/               # Custom exceptions
│   │   ├── ResourceNotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   └── GlobalExceptionHandler.java
│   └── TimesheetManagementApplication.java
├── src/main/resources/
│   └── application.yml
├── pom.xml
├── README.md
└── .gitignore
```

## 🚀 Future Enhancements

- [ ] Swagger/OpenAPI documentation with Springdoc
- [ ] Advanced role-based access control (RBAC)
- [ ] PostgreSQL/MySQL integration
- [ ] Docker containerization
- [ ] Kubernetes deployment configuration
- [ ] Real-time notifications with WebSockets
- [ ] Advanced timesheet analytics and reporting
- [ ] Frontend application (React/Angular)
- [ ] Email notifications for approvals/rejections
- [ ] Audit logging for all changes
- [ ] Multi-language support (i18n)
- [ ] Performance optimization with caching

## 📝 API Documentation

For full API documentation, see [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

**Kolukulurisahi7**

- GitHub: [@kolukulurisahi7](https://github.com/kolukulurisahi7)

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- JWT.io for JWT tokens
- Lombok team for reducing boilerplate

## 📞 Support

If you have any issues or questions, please open an issue in the repository.

---

**Happy coding! 🚀**