package com.mocaphk.backend.endpoints.mocap.dummy.controller;

import com.mocaphk.backend.endpoints.mocap.dummy.dto.BookInput;
import com.mocaphk.backend.endpoints.mocap.dummy.model.Author;
import com.mocaphk.backend.endpoints.mocap.dummy.model.Book;
import com.mocaphk.backend.endpoints.mocap.dummy.service.BookService;
import com.mocaphk.backend.enums.Roles;
import jakarta.annotation.security.RolesAllowed;
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

    @RolesAllowed({ Roles.ADMIN, Roles.LECTURER })
    @QueryMapping(name = "book")
    public Book getBookById(@Argument Long id) {
        log.debug("getBookById: {}", id);
        return bookService.getBookById(id);
    }

    @RolesAllowed({ Roles.LECTURER })
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

//    @SchemaMapping(typeName = "Book", field = "author")
//    public Author getAuthorByBook(Book book) {
//        log.debug("getAuthorByBook: {}", book);
//        return bookService.getAuthorById(book.getAuthorId());
//    }
}
