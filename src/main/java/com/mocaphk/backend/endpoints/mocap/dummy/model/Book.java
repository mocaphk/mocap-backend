package com.mocaphk.backend.endpoints.mocap.dummy.model;

import com.mocaphk.backend.endpoints.mocap.dummy.enums.BookType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    private BookType type;

    @Column(name = "author_id")
    private Long authorId;

    @ManyToOne
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private Author author;

    // Not exposing to graphql
    private Long secret;
}
