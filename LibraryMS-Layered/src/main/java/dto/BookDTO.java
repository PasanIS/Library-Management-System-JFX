package dto;


import lombok.*;

import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookDTO {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private String isbn;
    private int copiesAvailable;
    private boolean isDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDTO bookDTO = (BookDTO) o;
        return copiesAvailable == bookDTO.copiesAvailable &&
                isDeleted == bookDTO.isDeleted &&
                Objects.equals(bookId, bookDTO.bookId) &&
                Objects.equals(title, bookDTO.title) &&
                Objects.equals(author, bookDTO.author) &&
                Objects.equals(category, bookDTO.category) &&
                Objects.equals(publisher, bookDTO.publisher) &&
                Objects.equals(isbn, bookDTO.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, title, author, category, publisher, isbn, copiesAvailable, isDeleted);
    }
}
