package com.mocaphk.backend.endpoints.dummy.service;

import com.mocaphk.backend.endpoints.dummy.dto.BookInput;
import com.mocaphk.backend.endpoints.dummy.model.Author;
import com.mocaphk.backend.endpoints.dummy.model.Book;
import com.mocaphk.backend.endpoints.dummy.repository.AuthorRepository;
import com.mocaphk.backend.endpoints.dummy.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookService {
    private final AuthorRepository authorRepo;
    private final BookRepository bookRepo;

    public Book getBookById(Long id) {
        return bookRepo.findById(id).orElse(null);
    }

    public Author getAuthorById(Long id) {
        return authorRepo.findById(id).orElse(null);
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAll();
    }

    public Book addBook(BookInput bookInput) {
        Author author = new Author();
        author.setFirstName(bookInput.author().firstName());
        author.setLastName(bookInput.author().lastName());
        authorRepo.save(author);

        Book book = new Book();
        book.setName(bookInput.name());
        book.setPageCount(bookInput.pageCount());
        book.setAuthorId(author.getId());
        book.setSecret(123L);
        return bookRepo.save(book);
    }
}
