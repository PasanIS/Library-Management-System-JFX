package repository.custom;

import model.entity.Borrow;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface BorrowRepository extends CRUDRepository<Borrow, String> {
    // Define any additional methods specific to BorrowRepository if needed
    // For example, methods to find borrowed books by member ID, etc.
    boolean hasAlreadyBorrowed(String memberId, String bookId) throws SQLException;
    void issueBook(Borrow borrow) throws SQLException; // -----Changed signature to take Borrow object
    List<Borrow> findBorrowingsByMember(String memberId) throws SQLException;
    void updateReturnDateAndFine(String borrowId, LocalDateTime returnDate, double fineAmount) throws SQLException; // Separate update for return
    String generateNextBorrowId() throws SQLException;
    int countActiveBorrowingsByMember(String memberId) throws SQLException;
}
