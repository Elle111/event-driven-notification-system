# Event-Driven Notification System

A portfolio-quality, event-driven microservices architecture for handling notifications with Spring Boot, Apache Kafka, and PostgreSQL.

## 🏗️ Architecture Overview

This system demonstrates a production-ready event-driven architecture with two Spring Boot microservices:

### Services

1. **notification-api-service** (Port 8080)
   - REST API for creating and retrieving notifications
   - Publishes events to Kafka when notifications are created
   - Manages notification lifecycle states

2. **notification-processor-service** (Port 8081)
   - Consumes notification events from Kafka
   - Processes notifications (simulates email/SMS/push sending)
   - Updates notification status in shared database

### Event Flow

```
Client Request → API Service → Database (PENDING) → Kafka Event → Processor Service → Update Status
```

## 🛠️ Tech Stack

- **Java 21** - Modern Java with latest features
- **Spring Boot 3.2.5** - Enterprise application framework
- **Apache Kafka** - Event streaming platform
- **PostgreSQL** - Primary database
- **Docker Compose** - Local development environment
- **Maven** - Build and dependency management
- **JUnit 5** - Unit testing framework
- **Testcontainers** - Integration testing with real containers

## 📋 Features

### Core Functionality
- ✅ Create notifications (EMAIL, SMS, PUSH)
- ✅ Event-driven processing with Kafka
- ✅ Status tracking (PENDING → PROCESSING → SENT/FAILED)
- ✅ Retry mechanism with exponential backoff
- ✅ Dead-letter queue for failed messages
- ✅ RESTful API with comprehensive endpoints

### Business Logic
- **EMAIL notifications**: 90% success rate
- **SMS notifications**: 80% success rate  
- **PUSH notifications**: 70% success rate
- **Retry logic**: 3 attempts with exponential backoff
- **Error handling**: Failed messages go to dead-letter topic

### Testing
- ✅ Unit tests for business logic
- ✅ Integration tests with embedded Kafka
- ✅ Repository layer testing
- ✅ Service layer testing with Mockito

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker and Docker Compose

### 1. Start Infrastructure

```bash
# Start PostgreSQL, Kafka, and Zookeeper
docker-compose up -d

# Verify services are running
docker-compose ps
```

### 2. Build and Run Services

```bash
# Build all modules
mvn clean install

# Run API Service (Terminal 1)
cd notification-api-service
mvn spring-boot:run

# Run Processor Service (Terminal 2) 
cd notification-processor-service
mvn spring-boot:run
```

### 3. Test the APIs

#### Using Postman
1. Import `postman-collection.json` into Postman
2. Execute the requests in order:
   - Create notification
   - Get notification by ID
   - Check status updates

#### Using curl

```bash
# Create a notification
curl -X POST http://localhost:8080/api/notifications \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "test@example.com",
    "message": "Test notification message",
    "type": "EMAIL"
  }'

# Get notification by ID (replace 1 with actual ID)
curl http://localhost:8080/api/notifications/1

# Get all notifications
curl http://localhost:8080/api/notifications

# Get notifications by status
curl http://localhost:8080/api/notifications/status/SENT
```

## 📊 API Endpoints

### Notification API Service (Port 8080)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/notifications` | Create new notification |
| GET | `/api/notifications/{id}` | Get notification by ID |
| GET | `/api/notifications` | Get all notifications |
| GET | `/api/notifications/recipient/{recipient}` | Get by recipient |
| GET | `/api/notifications/status/{status}` | Get by status |

### Notification Types

- `EMAIL` - Email notifications (90% success rate)
- `SMS` - SMS notifications (80% success rate)
- `PUSH` - Push notifications (70% success rate)

### Notification Status Flow

```
PENDING → PROCESSING → SENT
                ↓
              FAILED
```

## 🗄️ Database Schema

```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient VARCHAR(255) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔄 Kafka Topics

| Topic | Purpose | Partitions |
|-------|---------|------------|
| `notification-created` | New notification events | 1 |
| `notification-created-dead-letter` | Failed processing events | 1 |

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Module Tests
```bash
# API Service Tests
cd notification-api-service && mvn test

# Processor Service Tests  
cd notification-processor-service && mvn test
```

### Test Coverage
- Unit tests for service layer logic
- Integration tests with embedded Kafka
- Repository tests with in-memory database
- Mock-based testing for external dependencies

## 🔧 Configuration

### Application Properties

#### API Service (`notification-api-service/src/main/resources/application.yml`)
- Server port: 8080
- Database: PostgreSQL on localhost:5432
- Kafka: localhost:9092

#### Processor Service (`notification-processor-service/src/main/resources/application.yml`)
- Server port: 8081
- Database: Shared PostgreSQL
- Kafka: Consumer group configuration

### Environment Variables
You can override configuration using environment variables:

```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/notification_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

# Kafka
export SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## 📦 Project Structure

```
event-driven-notification-system/
├── pom.xml                          # Parent POM with dependency management
├── docker-compose.yml               # Local development infrastructure
├── postman-collection.json          # API testing collection
├── README.md                        # This file
├── notification-api-service/        # API microservice
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/app/notification/api/
│       │   ├── NotificationApiApplication.java
│       │   ├── controller/
│       │   │   └── NotificationController.java
│       │   ├── service/
│       │   │   └── NotificationService.java
│       │   ├── repository/
│       │   │   └── NotificationRepository.java
│       │   ├── entity/
│       │   │   └── Notification.java
│       │   ├── dto/
│       │   │   ├── NotificationRequest.java
│       │   │   └── NotificationResponse.java
│       │   ├── event/
│       │   │   └── NotificationCreatedEvent.java
│       │   └── config/
│       │       └── KafkaConfig.java
│       └── test/
└── notification-processor-service/  # Processor microservice
    ├── pom.xml
    └── src/
        ├── main/java/com/app/notification/processor/
        │   ├── NotificationProcessorApplication.java
        │   ├── listener/
        │   │   └── NotificationEventListener.java
        │   ├── service/
        │   │   └── NotificationProcessorService.java
        │   ├── repository/
        │   │   └── NotificationRepository.java
        │   ├── entity/
        │   │   └── Notification.java
        │   ├── event/
        │   │   └── NotificationCreatedEvent.java
        │   └── config/
        │       └── KafkaConfig.java
        └── test/
```

## 🔍 Monitoring and Logging

### Log Levels
- **INFO**: Business operations, Kafka events
- **DEBUG**: Detailed processing information
- **ERROR**: Failed operations, exceptions

### Key Logs to Monitor
```
# API Service
"Created notification {} and published event"
"Successfully published event for notification {}"
"Failed to publish event for notification {}"

# Processor Service  
"Processing notification {}"
"Successfully processed notification {}"
"Failed to process notification {}"
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Kafka Connection Failed
```bash
# Check if Kafka is running
docker-compose ps kafka

# Restart Kafka
docker-compose restart kafka
```

#### 2. Database Connection Issues
```bash
# Check PostgreSQL
docker-compose ps postgres

# Verify database exists
docker-compose exec postgres psql -U postgres -d notification_db -c "\dt"
```

#### 3. Service Startup Issues
```bash
# Check logs
docker-compose logs notification-api-service
docker-compose logs notification-processor-service

# Restart services
docker-compose restart
```

## 🚀 Production Considerations

### Scaling
- **API Service**: Horizontal scaling behind load balancer
- **Processor Service**: Scale based on Kafka partition count
- **Kafka**: Multiple brokers for high availability
- **Database**: Read replicas for query scaling

### Security
- API authentication and authorization
- Kafka SSL/TLS encryption
- Database connection encryption
- Environment variable management

### Monitoring
- Application metrics (Micrometer/Prometheus)
- Kafka consumer lag monitoring
- Database performance metrics
- Health check endpoints

### Deployment
- Container orchestration (Kubernetes)
- CI/CD pipeline
- Blue-green deployment strategy
- Configuration management

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run the test suite
6. Submit a pull request

## 📄 License

This project is for educational and portfolio demonstration purposes.

## 📞 Support

For questions or issues:
1. Check the troubleshooting section
2. Review the logs
3. Verify all services are running
4. Test with the provided Postman collection

---

