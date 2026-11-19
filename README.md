# CS336B - Library Management REST API (Spring Boot)

MSE-2 Practical Examination Question 2 (CO5 - 10 Marks)

## Project Introduction

A college library wants to develop a Library Management REST API using Spring Boot to manage its book records. Each book record contains: id, title, author, category, and availableCopies.

This is a simple beginner-friendly Spring Boot REST API that performs full CRUD operations on books stored in-memory using an `ArrayList<Book>`. No database (MySQL/JDBC/Hibernate/JPA/Spring Data) is used, as required by the examination question.

## Technologies Used

- Java 21
- Spring Boot 4.1.2
- Spring Web (spring-boot-starter-webmvc)
- Maven (build tool)
- Tomcat (embedded web server)
- Jackson (JSON serialization/deserialization)
- Postman (API testing)

## Project Structure

```
library/
├── src/main/java/com/example/library/
│   ├── LibraryApplication.java   # Spring Boot main class (@SpringBootApplication)
│   ├── Book.java                 # Book model class (fields, constructors, getters, setters)
│   └── BookController.java       # REST controller with all CRUD endpoints
├── src/main/resources/
│   └── application.properties    # Application configuration (server.port, etc.)
├── src/test/java/com/example/library/
│   └── LibraryApplicationTests.java
├── pom.xml                       # Maven project configuration
├── CS336B - Library Management API.postman_collection.json
├── screenshots/                  # Postman screenshots (capture manually)
├── README.md
└── VIVA_NOTES.md
```

## How to Run the Application

### Prerequisites
- Java 21 (JDK) installed
- Apache Maven installed
- Port 8080 free (see note below)

### Steps

1. Open a terminal in the `library` folder:
   ```
   cd C:\Users\tejeet\Downloads\SPRINGBOOTLEARNINGS\maven install\MSE 2 QUESTION 2\library
   ```

2. Build the project:
   ```
   mvn clean package -DskipTests
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

4. The application starts and listens on the configured port (default 8080).

### Port Note

On this machine, port 8080 was already occupied by an Oracle TNS listener (TNSLSNR.EXE) that could not be stopped (access denied). Therefore `application.properties` is configured with `server.port=8081`. If port 8080 is free on your machine, change `application.properties` to `server.port=8080` and use `http://localhost:8080/books` in Postman.

The Postman collection includes a `baseUrl` variable set to `http://localhost:8081`. Edit it to `http://localhost:8080` if needed.

## API Endpoints

Base URL: `http://localhost:8081/books` (or 8080 if configured)

| # | Method | Endpoint | Description | Success Status |
|---|--------|----------|-------------|----------------|
| 1 | POST | /books | Add a new book | 201 Created |
| 2 | GET | /books | Retrieve all books | 200 OK |
| 3 | GET | /books/{id} | Retrieve a book by ID | 200 OK |
| 4 | PUT | /books/{id} | Update a book's details | 200 OK |
| 5 | DELETE | /books/{id} | Delete a book by ID | 200 OK |

### Error Responses

| Status | Meaning |
|--------|---------|
| 404 Not Found | Book not found (get/update/delete by nonexistent ID) |
| 409 Conflict | Duplicate book ID on add |

## Sample JSON Requests

### Add Book (POST /books)
```json
{
  "id": 1,
  "title": "Java Programming",
  "author": "Herbert Schildt",
  "category": "Programming",
  "availableCopies": 10
}
```
Expected response: 201 Created with the book JSON.

### Get All Books (GET /books)
Expected response: 200 OK with a JSON array of all books.

### Get Book by ID (GET /books/1)
Expected response: 200 OK with the matching book JSON.

### Update Book (PUT /books/1)
```json
{
  "title": "Advanced Java",
  "author": "James Gosling",
  "category": "Computer Science",
  "availableCopies": 15
}
```
Expected response: 200 OK with the updated book JSON.

### Delete Book (DELETE /books/1)
Expected response: 200 OK with a deletion message.

## HTTP Status Codes Summary

- **201 Created** - Book successfully added (POST)
- **200 OK** - Successful retrieval, update, or deletion (GET, PUT, DELETE)
- **404 Not Found** - Book with the given ID does not exist
- **409 Conflict** - A book with the same ID already exists (POST)

## How to Import the Postman Collection

1. Open Postman.
2. Click **Import** (top-left).
3. Upload the file:
   `CS336B - Library Management API.postman_collection.json`
4. The collection "CS336B - Library Management API" appears with 9 requests.
5. Set the `baseUrl` variable to `http://localhost:8081` (or 8080).
6. Start the Spring Boot application.
7. Run each request in order and verify the status codes.

## Screenshots

Capture the following screenshots from Postman and save them in the `screenshots/` folder:

1. `01-add-book.png` - POST request with JSON body and 201 response
2. `02-get-all-books.png` - GET all books with returned JSON
3. `03-get-book-by-id.png` - GET book by ID
4. `04-update-book.png` - PUT request with updated details and 200 response
5. `05-delete-book.png` - DELETE request with deletion message
6. `06-error-handling.png` - Negative test showing 404 or 409

> Note: I cannot generate actual Postman GUI screenshots programmatically. Please run the requests in Postman and capture these screenshots manually for your practical submission.

## Viva Preparation

See `VIVA_NOTES.md` for simple explanations of common viva questions.
