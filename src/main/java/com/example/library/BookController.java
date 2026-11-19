package com.example.library;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REST Controller class for Library Management API
// @RestController marks this class as a controller where every method returns data (JSON) instead of a view
// It combines @Controller and @ResponseBody
@RestController

// @RequestMapping maps all handler methods in this class to the base URL path "/books"
// All endpoints will be like /books, /books/{id}, etc.
@RequestMapping("/books")
public class BookController {

    // In-memory collection to store books
    // ArrayList is a resizable-array implementation of the List interface
    // Books are stored here temporarily (not in a database) as required by the exam
    private final List<Book> books = new ArrayList<>();

    // (b) Create a Book - POST /books
    // @PostMapping maps HTTP POST requests to this method
    // @RequestBody binds the incoming JSON request body to the Book object (deserialized by Jackson)
    // Returns 201 Created on success, 409 Conflict if duplicate ID
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        // Check for duplicate ID using stream API
        boolean exists = books.stream()
                .anyMatch(b -> b.getId().equals(book.getId()));
        if (exists) {
            // Return HTTP 409 Conflict when book with same ID already exists
            return ResponseEntity.status(409).body("Book with id " + book.getId() + " already exists.");
        }
        // Add the new book to the ArrayList using add() method
        books.add(book);
        // Return HTTP 201 Created with the book object
        return ResponseEntity.status(201).body(book);
    }

    // (c.1) Retrieve all books - GET /books
    // @GetMapping maps HTTP GET requests to this method
    // Returns 200 OK with the list of all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        // Return all books stored in ArrayList with HTTP 200 OK
        return ResponseEntity.ok(books);
    }

    // (c.2) Retrieve a specific book by ID - GET /books/{id}
    // @GetMapping with path variable "/{id}"
    // @PathVariable extracts the id value from the URL path
    // Returns 200 OK with book details, or 404 Not Found if missing
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Integer id) {
        // Search for book by ID using stream and filter
        Optional<Book> book = books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();
        if (book.isPresent()) {
            // Return HTTP 200 OK with the found book
            return ResponseEntity.ok(book.get());
        }
        // Return HTTP 404 Not Found when book does not exist
        return ResponseEntity.status(404).body("Book with id " + id + " not found.");
    }

    // (d) Update a book's details - PUT /books/{id}
    // @PutMapping maps HTTP PUT requests to this method
    // @PathVariable gets the id from URL, @RequestBody gets updated details from JSON body
    // Returns 200 OK with updated book, or 404 Not Found if missing
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @RequestBody Book updatedBook) {
        // Iterate through the ArrayList to find the book with matching ID
        for (int i = 0; i < books.size(); i++) {
            Book existing = books.get(i);
            if (existing.getId().equals(id)) {
                // Update all fields: title, author, category, availableCopies
                existing.setTitle(updatedBook.getTitle());
                existing.setAuthor(updatedBook.getAuthor());
                existing.setCategory(updatedBook.getCategory());
                existing.setAvailableCopies(updatedBook.getAvailableCopies());
                // Return HTTP 200 OK with the updated book object
                return ResponseEntity.ok(existing);
            }
        }
        // Return HTTP 404 Not Found when book to update does not exist
        return ResponseEntity.status(404).body("Book with id " + id + " not found for update.");
    }

    // (e) Delete a book by ID - DELETE /books/{id}
    // @DeleteMapping maps HTTP DELETE requests to this method
    // @PathVariable gets the id from URL
    // Returns 200 OK with deletion message, or 404 Not Found if missing
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer id) {
        // Remove the book with matching ID from ArrayList using removeIf()
        boolean removed = books.removeIf(b -> b.getId().equals(id));
        if (removed) {
            // Return HTTP 200 OK with success deletion message
            return ResponseEntity.ok("Book with id " + id + " deleted successfully.");
        }
        // Return HTTP 404 Not Found when book to delete does not exist
        return ResponseEntity.status(404).body("Book with id " + id + " not found for deletion.");
    }
}
