# CS336B - Library Management API - VIVA NOTES

Simple explanations for your viva. Understand these well and answer in your own words.

## 1. What is Spring Boot?

Spring Boot is a framework built on top of the Spring Framework that makes it easy to create stand-alone, production-ready Spring applications. It provides auto-configuration, embedded servers (like Tomcat), and starter dependencies so you can run a Spring application with minimal configuration using a `main()` method.

## 2. What is a REST API?

REST (Representational State Transfer) API is an architectural style for building web services. It uses HTTP methods (GET, POST, PUT, DELETE) to perform operations on resources (like books). Communication is stateless and typically uses JSON as the data format.

## 3. What is a REST Controller?

A REST Controller is a Spring component that handles incoming HTTP requests and returns responses (usually JSON or XML) instead of rendering HTML views. It maps HTTP endpoints to Java methods.

## 4. What is @RestController?

`@RestController` is a Spring annotation that marks a class as a controller where every method returns a domain object instead of a view. It is a combination of `@Controller` and `@ResponseBody`, meaning Spring automatically converts the returned object into JSON and writes it to the HTTP response body.

## 5. What is @RequestBody?

`@RequestBody` tells Spring to deserialize the incoming HTTP request body (JSON) into a Java method parameter (here, a `Book` object). Spring uses Jackson to convert JSON into the Java object.

## 6. What is @PathVariable?

`@PathVariable` extracts a value from the URI path and binds it to a method parameter. For example, in `GET /books/{id}`, the `{id}` value from the URL is passed to the `id` parameter of the handler method.

## 7. Difference between GET, POST, PUT, DELETE

- **GET** - Retrieve data (read). Safe and idempotent. No body. Used to get all books or a book by ID.
- **POST** - Create a new resource (write). Sends data in the request body. Not idempotent. Used to add a new book.
- **PUT** - Update/replace an existing resource. Sends the full updated data in the body. Idempotent. Used to update a book by ID.
- **DELETE** - Remove a resource. Used to delete a book by ID.

## 8. What is ArrayList?

`ArrayList` is a resizable-array implementation of the `List` interface in Java's Collections Framework. It stores elements in order, allows duplicates, supports fast access by index, and grows automatically as elements are added using the `add()` method. In this project, `List<Book> books = new ArrayList<>();` holds all books in memory.

## 9. HTTP Status Codes Used

- **201 Created** - Book successfully added.
- **200 OK** - Successful GET, PUT, or DELETE.
- **404 Not Found** - Requested book ID does not exist.
- **409 Conflict** - A book with the same ID already exists.

## 10. How the Application Works (Flow)

1. `LibraryApplication.main()` starts the Spring Boot application with `@SpringBootApplication`.
2. `BookController` is registered as a REST controller mapped to `/books`.
3. Books are stored in a static `ArrayList<Book>`.
4. When a request arrives, Spring maps it to the appropriate controller method based on HTTP method and path.
5. `@RequestBody` converts incoming JSON to a `Book` object; `@PathVariable` extracts the ID from the URL.
6. The controller performs the operation on the `ArrayList` and returns a `ResponseEntity` with the appropriate HTTP status code and body.

## Quick Answers Cheat Sheet

| Question | One-line Answer |
|----------|-----------------|
| Spring Boot? | Framework for stand-alone, production-ready Spring apps with minimal config. |
| REST API? | Web service using HTTP methods to operate on resources via JSON. |
| @RestController? | Controller that returns JSON/XML response body instead of a view. |
| @RequestBody? | Binds incoming JSON request body to a Java method parameter. |
| @PathVariable? | Extracts a URI template variable into a method parameter. |
| GET vs POST? | GET reads data; POST creates a new resource. |
| PUT vs DELETE? | PUT updates a resource; DELETE removes a resource. |
| ArrayList? | Dynamic array-based List implementation in Java Collections. |
