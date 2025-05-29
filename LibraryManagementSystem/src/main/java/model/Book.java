package model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Book {

    private int bookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private int copies; //-- For check-available copies

}
