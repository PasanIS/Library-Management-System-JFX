package model.entity;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book {

    private String bookId;
    private String title;
    private String isbn;

    private Author author;
    private Category category;
    private Publisher publisher;

    private int totalQty;
    private int availableQty;

}
