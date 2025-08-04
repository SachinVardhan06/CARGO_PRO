# Load & Booking Management System

A robust Spring Boot backend system for managing Load & Booking operations with PostgreSQL database, designed for performance, security, and scalability.

## 🚀 Features

- **Load Management**: Create, read, update, delete loads with comprehensive filtering
- **Booking Management**: Handle booking requests with business rule enforcement
- **Status Transitions**: Automatic status management based on business logic
- **Input Validation**: Comprehensive validation using Bean Validation
- **Exception Handling**: Global exception handling with detailed error responses
- **Pagination & Filtering**: Efficient data retrieval with pagination support
- **API Documentation**: Swagger/OpenAPI documentation
- **High Test Coverage**: JUnit and Mockito tests with 60%+ coverage

## 🏗️ Architecture

The system follows a clean, layered architecture:

```
├── Controller Layer    (REST endpoints)
├── Service Layer      (Business logic)
├── Repository Layer   (Data access)
├── Entity Layer       (JPA entities)
├── DTO Layer         (Data transfer objects)
└── Exception Layer   (Error handling)
```

## 📊 Database Schema

### Load Entity
- **id**: UUID (Primary Key)
- **shipperId**: String (Required)
- **facility**: Embedded object containing loading/unloading details
- **productType**: String (Required)
- **truckType**: String (Required)
- **noOfTrucks**: Integer (Required, Positive)
- **weight**: Double (Required, Positive)
- **comment**: String (Optional)
- **datePosted**: Timestamp (Auto-generated)
- **status**: Enum (POSTED, BOOKED, CANCELLED)

### Booking Entity
- **id**: UUID (Primary Key)
- **loadId**: UUID (Foreign Key to Load)
- **transporterId**: String (Required)
- **proposedRate**: Double (Required, Positive)
- **comment**: String (Optional)
- **status**: Enum (PENDING, ACCEPTED, REJECTED)
- **requestedAt**: Timestamp (Auto-generated)

## 🔧 Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Setup Instructions

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd load-booking-system
```

### 2. Database Setup
```sql
-- Create database
CREATE DATABASE load_booking_db;

-- Create user (optional)
CREATE USER cargopro WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE load_booking_db TO cargopro;
```

### 3. Configure Application Properties
Update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/load_booking_db
    username: postgres  # or your username
    password: password  # or your password
```

### 4. Build and Run
```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Start the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

Once the application is running, access the Swagger UI at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 🔗 API Endpoints

### Load Management
- `POST /load` - Create a new load
- `GET /load` - Get loads with filtering and pagination
- `GET /load/{loadId}` - Get load by ID
- `PUT /load/{loadId}` - Update load
- `DELETE /load/{loadId}` - Delete load

### Booking Management
- `POST /booking` - Create a new booking
- `GET /booking` - Get bookings with filtering and pagination
- `GET /booking/{bookingId}` - Get booking by ID
- `PUT /booking/{bookingId}` - Update booking
- `DELETE /booking/{bookingId}` - Delete booking

## 📝 API Usage Examples

### Create a Load
```bash
curl -X POST http://localhost:8080/load \
  -H "Content-Type: application/json" \
  -d '{
    "shipperId": "SHIPPER001",
    "facility": {
      "loadingPoint": "Mumbai Port",
      "unloadingPoint": "Delhi Warehouse",
      "loadingDate": "2024-01-20T08:00:00.000Z",
      "unloadingDate": "2024-01-22T18:00:00.000Z"
    },
    "productType": "Electronics",
    "truckType": "Container",
    "noOfTrucks": 2,
    "weight": 15.5,
    "comment": "Handle with care"
  }'
```

### Create a Booking
```bash
curl -X POST http://localhost:8080/booking \
  -H "Content-Type: application/json" \
  -d '{
    "loadId": "123e4567-e89b-12d3-a456-426614174000",
    "transporterId": "TRANS001",
    "proposedRate": 25000.0,
    "comment": "Can deliver within 2 days"
  }'
```

### Get Loads with Filtering
```bash
curl "http://localhost:8080/load?shipperId=SHIPPER001&status=POSTED&page=0&size=10"
```

## 🎯 Business Rules

### Load Status Transitions
- **POSTED**: Default status when load is created
- **BOOKED**: When first booking is created
- **CANCELLED**: When all bookings are deleted

### Booking Status Transitions
- **PENDING**: Default status when booking is created
- **ACCEPTED**: When booking is accepted (auto-rejects other bookings)
- **REJECTED**: When booking is rejected

### Validation Rules
- Cannot create booking for cancelled loads
- One transporter can have only one booking per load
- All numeric values must be positive
- Required fields cannot be null or empty

## 🧪 Testing

Run tests with coverage:
```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report
```

Test coverage report will be available at `target/site/jacoco/index.html`

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/cargopro/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── entity/         # JPA entities
│   │   ├── dto/           # Data transfer objects
│   │   ├── mapper/        # Entity-DTO mappers
│   │   ├── enums/         # Enumerations
│   │   └── exception/     # Exception handling
│   └── resources/
│       ├── application.yml
│       └── application-test.yml
└── test/
    ├── java/com/cargopro/
    │   ├── controller/    # Controller tests
    │   ├── service/       # Service tests
    │   └── repository/    # Repository tests
    └── resources/
        └── application-test.yml
```

## 🔒 Security Considerations

- Input validation on all endpoints
- SQL injection prevention through JPA
- Global exception handling to prevent information leakage
- Proper HTTP status codes for different scenarios

## 📈 Performance Optimizations

- Database indexing on frequently queried fields
- Pagination to handle large datasets
- Lazy loading for entity relationships
- Connection pooling with HikariCP

## 🚀 Deployment

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/load-booking-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/load_booking_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=password
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For any questions or issues, please contact:
- Email: careers@cargopro.ai
- Subject: YourName_SoftwareDevelopment_Internship

## 🎯 Assumptions

1. **Database**: PostgreSQL is used as the primary database
2. **Authentication**: Basic API without authentication (can be extended)
3. **Timezone**: All timestamps are stored in UTC
4. **Validation**: Bean Validation is used for input validation
5. **Error Handling**: Global exception handler provides consistent error responses
6. **Testing**: H2 in-memory database is used for testing
7. **Pagination**: Default page size is 10, maximum is 100
8. **Status Management**: Status transitions are handled automatically based on business rules

## 🔄 Future Enhancements

- JWT-based authentication and authorization
- Real-time notifications for status changes
- Audit logging for all operations
- Rate limiting for API endpoints
- Caching for frequently accessed data
- Metrics and monitoring integration