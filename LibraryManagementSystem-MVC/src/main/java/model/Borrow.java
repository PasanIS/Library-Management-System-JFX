package model;

import lombok.*;

import java.time.LocalDateTime;

@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Borrow {

    private String borrowId;
    private String bookId;
    private String memberId;

    private Member member;
    private Book book;

    private LocalDateTime issueDate;
    private LocalDateTime returnDate;

    private Double fine;

    public String getBookId() {
        return book != null ? book.getBookId() : null;
    }

    public String getBookTitle() {
        return book != null ? book.getTitle() : null;
    }


}
