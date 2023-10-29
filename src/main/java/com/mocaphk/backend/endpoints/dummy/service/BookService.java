package com.mocaphk.backend.endpoints.dummy.service;

import com.mocaphk.backend.endpoints.dummy.dto.BookInput;
import com.mocaphk.backend.endpoints.dummy.model.Author;
import com.mocaphk.backend.endpoints.dummy.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class BookService {
    private int bookCount = 3;

    private int authorCount = 3;

    private List<Book> books = new ArrayList<>(Arrays.asList(
            new Book("book-1", "Effective Java", 416, "author-1", "secret-1"),
            new Book("book-2", "Hitchhiker's Guide to the Galaxy", 208, "author-2", "secret-2"),
            new Book("book-3", "Down Under", 436, "author-3", "secret-3")
    ));

    private List<Author> authors = new ArrayList<>(Arrays.asList(
            new Author("author-1", "Joshua", "Bloch"),
            new Author("author-2", "Douglas", "Adams"),
            new Author("author-3", "Bill", "Bryson")
    ));

    public Book getBookById(String id) {
        return books.stream()
                .filter(book -> book.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Author getAuthorById(String id) {
        return authors.stream()
                .filter(author -> author.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Book> getAllBooks() {
        return books;
    }

    public Book addBook(BookInput bookInput) {
        bookCount++;
        authorCount++;

        Author author = new Author(
                "author-" + authorCount,
                bookInput.author() != null ? bookInput.author().firstName() : "",
                bookInput.author() != null ? bookInput.author().lastName() : ""
        );
        authors.add(author);

        Book book = new Book(
                "book-" + bookCount,
                bookInput.name(),
                bookInput.pageCount() != null ? bookInput.pageCount() : 0,
                author.getId(),
                "secret-" + bookCount
        );
        books.add(book);

        return book;
    }
}
