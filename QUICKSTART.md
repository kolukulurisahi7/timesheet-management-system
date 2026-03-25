# Quick Start Guide

## 1. Prerequisites
- Java 17+
- Maven 3.6+
- Git

## 2. Clone and Build

```bash
# Clone the repository
git clone https://github.com/kolukulurisahi7/timesheet-management-system.git
cd timesheet-management-system

# Build with Maven
mvn clean install
```

## 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 4. Access H2 Console

Navigate to `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:timesheetdb`
- Username: `sa`
- Password: (leave blank)

## 5. Test with Postman

1. Import `Timesheet-Management-API.postman_collection.json` into Postman
2. Set environment variables:
   - `base_url`: `http://localhost:8080`
3. Run requests in order:
   - Register → Login → Create Timesheet → Add Entry → Submit

## 6. Test with cURL

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

Copy the `token` from the response and use it in subsequent requests.

### Create Timesheet
```bash
curl -X POST http://localhost:8080/api/timesheets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "weekStartDate": "2024-03-25",
    "weekEndDate": "2024-03-31"
  }'
```

## 7. Troubleshooting

### Port Already in Use
```bash
# Kill the process using port 8080
lsof -ti:8080 | xargs kill -9
```

### Maven Build Issues
```bash
# Clean and rebuild
mvn clean
mvn install -DskipTests
```

### Database Issues
The H2 database is in-memory, so it resets on restart. This is expected for development.

## 8. Project Structure

- `src/main/java` - Java source code
- `src/main/resources` - Configuration and resources
- `pom.xml` - Maven dependencies and build configuration
- `README.md` - Full documentation
- `Timesheet-Management-API.postman_collection.json` - API collection for testing

## 9. Next Steps

1. Read the full [README.md](README.md) for complete documentation
2. Explore the API endpoints using Postman
3. Review the code structure
4. Check [Future Enhancements](#future-enhancements) for planned features

## 10. Support

For issues or questions, please refer to the main README.md or open an issue on GitHub.
