package model.dto;

import lombok.*;
import model.entity.Book;

import java.io.Serializable;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookDTO implements Serializable {
    private String bookId;
    private String title;
    private String isbn;

    // Use DTOs for nested objects
    private AuthorDTO author;
    private CategoryDTO category;
    private PublisherDTO publisher;

    private int totalQty;
    private int availableQty;
}
