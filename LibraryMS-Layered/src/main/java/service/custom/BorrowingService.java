package service.custom;

import dto.BorrowingDTO;
import service.SuperService;

import java.time.LocalDate;
import java.util.List;

public interface BorrowingService extends SuperService<BorrowingDTO, String> {

    boolean borrowBook(BorrowingDTO borrowingDTO);

    boolean returnBook(String borrowId);

    List<BorrowingDTO> findBorrowingsByMemberId(String memberId);

    List<BorrowingDTO> findBorrowedBooks();

    LocalDate calculateDueDate(LocalDate borrowDate);
}
