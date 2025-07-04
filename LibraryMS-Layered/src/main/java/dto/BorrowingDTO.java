package dto;

import entity.Borrowing;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BorrowingDTO {
    public static Borrowing.BorrowingStatus BorrowingStatus;
    private String borrowId;
    private String memberId;
    private String memberName; // Added for UI convenience
    private String bookId;
    private String bookTitle; // Added for UI convenience
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Borrowing.BorrowingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowingDTO that = (BorrowingDTO) o;
        return Objects.equals(borrowId, that.borrowId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(memberName, that.memberName) &&
                Objects.equals(bookId, that.bookId) &&
                Objects.equals(bookTitle, that.bookTitle) &&
                Objects.equals(borrowDate, that.borrowDate) &&
                Objects.equals(dueDate, that.dueDate) &&
                Objects.equals(returnDate, that.returnDate) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(borrowId, memberId, memberName, bookId, bookTitle, borrowDate, dueDate, returnDate, status);
    }
}
