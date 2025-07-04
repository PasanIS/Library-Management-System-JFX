package service.custom;

import model.dto.BorrowDTO;
import service.SuperService;

import java.time.LocalDate;
import java.util.List;

public interface BorrowService extends SuperService {

    // Business logic related to borrowing
    boolean issueBook(BorrowDTO borrowDTO);

    boolean returnBook(String borrowId, LocalDate returnDate, boolean isFinePaid) throws RuntimeException;

    boolean returnBook(String borrowId,
                       LocalDate returnDate,
                       double fineAmount,
                       boolean isFinePaid,
                       LocalDate paidDate
    );
    boolean hasAlreadyBorrowed(String memberId, String bookId);
    List<BorrowDTO> getBorrowingsByMember(String memberId);
    String generateNextBorrowId();
    int countActiveBorrowingsByMember(String memberId);

    // Combined logic for issueBookIfAllowed
    void issueBookIfAllowed(BorrowDTO borrowDTO) throws IllegalStateException;

}
