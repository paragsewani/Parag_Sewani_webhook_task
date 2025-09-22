# Webhook SQL Solution - Spring Boot Application

## Overview
This Spring Boot application automatically:
1. Generates a webhook by sending a POST request on startup
2. Solves a SQL problem based on the registration number
3. Submits the solution to the webhook URL with JWT authentication

## Problem Statement
The application solves a SQL query that calculates the number of younger employees in each department for every employee.

## SQL Solution Explanation
The query counts employees who are younger (born after) each employee within the same department:
- Uses LEFT JOIN to compare employees within the same department
- Compares DOB (Date of Birth) where a later DOB means the person is younger
- Groups results by employee and counts younger colleagues
- Orders by EMP_ID in descending order

## Project Structure
```
webhook-sql/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/webhooksql/
│       │       ├── WebhookSqlApplication.java    # Main application class
│       │       ├── WebhookService.java           # Service handling webhook flow
│       │       └── WebhookResponse.java          # Response DTO
│       └── resources/
│           └── application.properties            # Application configuration
├── pom.xml                                       # Maven configuration
└── README.md                                     # This file
```

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

## How to Build and Run

### Build the JAR file
```bash
mvn clean package
```

The JAR file will be created at `target/webhook-sql.jar`

### Run the application
```bash
java -jar target/webhook-sql.jar
```

Or using Maven:
```bash
mvn spring-boot:run
```

## What Happens When You Run It

1. **On Startup**: The application automatically sends a POST request to generate a webhook
2. **Process Response**: Receives webhook URL and JWT access token
3. **Generate SQL**: Creates the SQL query solution for the problem
4. **Submit Solution**: Sends the SQL query to the webhook URL with JWT authorization

## The SQL Query Solution

```sql
SELECT 
    e1.EMP_ID,
    e1.FIRST_NAME,
    e1.LAST_NAME,
    d.DEPARTMENT_NAME,
    COUNT(e2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT
FROM 
    EMPLOYEE e1
INNER JOIN 
    DEPARTMENT d ON e1.DEPARTMENT = d.DEPARTMENT_ID
LEFT JOIN 
    EMPLOYEE e2 ON e1.DEPARTMENT = e2.DEPARTMENT 
    AND e2.DOB > e1.DOB
GROUP BY 
    e1.EMP_ID, 
    e1.FIRST_NAME, 
    e1.LAST_NAME, 
    d.DEPARTMENT_NAME
ORDER BY 
    e1.EMP_ID DESC
```

## Key Features
- **Automatic Execution**: No manual trigger needed - runs on application startup
- **RestTemplate**: Uses Spring's RestTemplate for HTTP requests
- **JWT Authentication**: Properly handles JWT token in Authorization header
- **Error Handling**: Includes comprehensive error logging
- **Clean Architecture**: Separation of concerns with service layer

## Configuration
The application uses the following endpoints:
- Generate Webhook: `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`
- Submit Solution: `https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA`

## Logs
The application provides detailed logging for each step of the process. Check the console output for:
- Webhook generation status
- Access token confirmation
- SQL query generation
- Solution submission result

## Submission Requirements Checklist
- ✅ Uses RestTemplate for HTTP requests
- ✅ No controller/endpoint triggers the flow (uses ApplicationReadyEvent)
- ✅ JWT used in Authorization header for second API call
- ✅ Complete Spring Boot application
- ✅ Generates executable JAR file

## GitHub Repository Structure
When uploading to GitHub, ensure your repository includes:
- All source files
- pom.xml
- README.md
- The compiled JAR file (in releases or a specific folder)

## Author
Your Name Here

## License
MIT