package entity;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Borrowing {
    private String borrowId;
    private String memberId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowingStatus status; // Using an enum for status

    // Enum for Borrowing Status
    public enum BorrowingStatus {
        borrowed,
        returned,
        late
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Borrowing borrowing = (Borrowing) o;
        return Objects.equals(borrowId, borrowing.borrowId) &&
                Objects.equals(memberId, borrowing.memberId) &&
                Objects.equals(bookId, borrowing.bookId) &&
                Objects.equals(borrowDate, borrowing.borrowDate) &&
                Objects.equals(dueDate, borrowing.dueDate) &&
                Objects.equals(returnDate, borrowing.returnDate) &&
                status == borrowing.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(borrowId, memberId, bookId, borrowDate, dueDate, returnDate, status);
    }
}
