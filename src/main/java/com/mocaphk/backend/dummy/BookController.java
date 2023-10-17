package com.mocaphk.backend.dummy;

import com.mocaphk.backend.dummy.dto.BookInput;
import com.mocaphk.backend.dummy.model.Author;
import com.mocaphk.backend.dummy.model.Book;
import com.mocaphk.backend.dummy.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @QueryMapping(name = "book")
    public Book getBookById(@Argument String id) {
        log.debug("getBookById: {}", id);
        return bookService.getBookById(id);
    }

    @QueryMapping(name = "allBooks")
    public List<Book> getAllBooks() {
        log.debug("getAllBooks");
        return bookService.getAllBooks();
    }

    @MutationMapping(name = "addBook")
    public Book addBook(@Argument BookInput bookInput) {
        log.debug("addBook: {}", bookInput);
        return bookService.addBook(bookInput);
    }

    @SchemaMapping(typeName = "Book", field = "author")
    public Author getAuthorByBook(Book book) {
        log.debug("getAuthorByBook: {}", book);
        return bookService.getAuthorById(book.getAuthorId());
    }
}
