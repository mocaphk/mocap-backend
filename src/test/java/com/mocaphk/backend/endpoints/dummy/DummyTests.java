package com.mocaphk.backend.endpoints.dummy;

import com.mocaphk.backend.endpoints.mocap.dummy.controller.BookController;
import com.mocaphk.backend.endpoints.mocap.dummy.dto.AuthorInput;
import com.mocaphk.backend.endpoints.mocap.dummy.dto.BookInput;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class DummyTests {
    @Autowired
    private BookController bookController;

    @Test
    public void testGetBookById() {
        assertThat(bookController.getBookById(1L).getName()).isEqualTo("Effective Java");
    }

    @Test
    public void testGetAllBooks() {
        assertThat(bookController.getAllBooks().get(0).getName()).isEqualTo("Effective Java");
    }

    @Test
    public void testAddBook() {
        assertThat(bookController.addBook(
                new BookInput(
                        "Test Book",
                        100,
                        new AuthorInput("Test first name", "Test last name")
                )
        ).getName()).isEqualTo("Test Book");
    }

}
