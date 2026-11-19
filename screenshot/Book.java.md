# Book.java

```java
package com.example.library;

// Book model class representing a library book record
// This is the POJO (Plain Old Java Object) for the REST API
public class Book {

    // Fields (instance variables) for book record
    private Integer id;            // unique book identifier
    private String title;          // book title
    private String author;         // book author
    private String category;       // book category
    private Integer availableCopies; // number of copies available

    // Default constructor (no-argument constructor)
    // Required by Spring for creating objects and by Jackson for deserialization
    public Book() {
    }

    // Parameterized constructor
    // This is constructor used to initialize a Book with all field values
    public Book(Integer id, String title, String author, String category, Integer availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.availableCopies = availableCopies;
    }

    // Getter for id field
    public Integer getId() {
        return id;
    }

    // Setter for id field
    public void setId(Integer id) {
        this.id = id;
    }

    // Getter for title field
    public String getTitle() {
        return title;
    }

    // Setter for title field
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for author field
    public String getAuthor() {
        return author;
    }

    // Setter for author field
    public void setAuthor(String author) {
        this.author = author;
    }

    // Getter for category field
    public String getCategory() {
        return category;
    }

    // Setter for category field
    public void setCategory(String category) {
        this.category = category;
    }

    // Getter for availableCopies field
    public Integer getAvailableCopies() {
        return availableCopies;
    }

    // Setter for availableCopies field
    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
}
```
