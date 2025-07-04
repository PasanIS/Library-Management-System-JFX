package entity;

import lombok.*;

import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Book {
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
        Book book = (Book) o;
        return copiesAvailable == book.copiesAvailable &&
                isDeleted == book.isDeleted &&
                Objects.equals(bookId, book.bookId) &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(category, book.category) &&
                Objects.equals(publisher, book.publisher) &&
                Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId, title, author, category, publisher, isbn, copiesAvailable);
    }

}
