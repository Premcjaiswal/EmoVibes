# EmoVibes Backend Setup Guide

## Prerequisites

Before running the application, ensure you have the following installed:

### 1. Java Development Kit (JDK)
- **Version:** Java 17 or higher
- **Download:** [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
- **Verify:** Run `java -version` in terminal

### 2. PostgreSQL Database
- **Version:** PostgreSQL 12 or higher
- **Download:** [PostgreSQL Official Site](https://www.postgresql.org/download/)
- **Verify:** Run `psql --version` in terminal

### 3. Maven (Optional - Maven Wrapper Included)
- **Version:** Maven 3.6 or higher
- **Download:** [Maven Official Site](https://maven.apache.org/download.cgi)
- **Note:** The project includes Maven Wrapper (`mvnw`/`mvnw.cmd`), so Maven installation is optional

## Setup Instructions

### Step 1: Clone the Repository
```bash
git clone <your-repository-url>
cd EmoVibes/backend/emotionmusic
```

### Step 2: Database Setup

1. **Start PostgreSQL service:**
   ```bash
   # Windows (if installed as service)
   net start postgresql
   
   # macOS (using Homebrew)
   brew services start postgresql
   
   # Linux
   sudo systemctl start postgresql
   ```

2. **Create the database:**
   ```sql
   # Connect to PostgreSQL
   psql -U postgres
   
   # Create database
   CREATE DATABASE emovibes;
   
   # Exit psql
   \q
   ```

### Step 3: Configure Application

1. **Update database credentials in `src/main/resources/application.properties`:**
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

2. **Update Spotify API credentials (optional for testing):**
   ```properties
   spotify.client-id=your_spotify_client_id
   spotify.client-secret=your_spotify_client_secret
   ```

### Step 4: Run the Application

#### Option 1: Using Maven Wrapper (Recommended)
```bash
# Windows
mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

#### Option 2: Using Maven (if installed)
```bash
mvn spring-boot:run
```

#### Option 3: Using IDE
- Import the project as a Maven project
- Run the `EmotionmusicApplication.java` main class

### Step 5: Verify Installation

1. **Check if the application is running:**
   - Open browser and go to `http://localhost:8080/api/recommend/genres`
   - You should see: `{"genres":["pop","acoustic","metal","chill","edm"]}`

2. **Test music recommendations:**
   - Go to `http://localhost:8080/api/recommend/happy`
   - You should see a list of pop music tracks

## Troubleshooting

### Common Issues:

1. **Port 8080 already in use:**
   ```bash
   # Find process using port 8080
   netstat -ano | findstr :8080
   
   # Kill the process (Windows)
   taskkill /PID <process_id> /F
   ```

2. **Database connection failed:**
   - Verify PostgreSQL is running
   - Check database credentials in `application.properties`
   - Ensure database `emovibes` exists

3. **Java version error:**
   - Ensure Java 17+ is installed
   - Check `JAVA_HOME` environment variable

4. **Maven wrapper permission denied (Linux/macOS):**
   ```bash
   chmod +x mvnw
   ```

### Logs Location:
- Application logs: Check console output
- Database logs: Check PostgreSQL logs

## Development Tips

1. **Hot Reload:** The application includes Spring Boot DevTools for automatic restart on code changes

2. **Database Schema:** Tables are created automatically on first run using `schema-simple.sql`

3. **API Testing:** Use Postman or curl to test endpoints (see `API_DOCUMENTATION.md`)

4. **CORS:** CORS is enabled for all origins during development

## Project Structure

```
backend/emotionmusic/
├── src/main/java/com/emovibes/emotionmusic/
│   ├── controllers/          # REST API endpoints
│   ├── models/              # JPA entities
│   ├── repositories/        # Data access layer
│   ├── integrations/spotify/ # Spotify API integration
│   └── config/              # Configuration classes
├── src/main/resources/
│   ├── application.properties # App configuration
│   └── schema-simple.sql     # Database schema
├── pom.xml                   # Maven dependencies
├── mvnw                      # Maven wrapper (Unix)
├── mvnw.cmd                  # Maven wrapper (Windows)
├── API_DOCUMENTATION.md      # API documentation
└── SETUP_GUIDE.md           # This file
```

## Next Steps

1. **For Frontend Developers:** See `API_DOCUMENTATION.md` for endpoint details
2. **For AI/ML Developers:** Emotion tracking endpoints are ready for integration
3. **For Database Admins:** Schema is in `schema-simple.sql`

## Support

If you encounter any issues:
1. Check this setup guide
2. Review the API documentation
3. Check application logs for error details
4. Ensure all prerequisites are installed correctly
