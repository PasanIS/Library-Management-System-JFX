package repository.custom;

import entity.Borrowing;
import repository.SuperRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface BorrowingRepository extends SuperRepository<Borrowing, String> {

    List<Borrowing> findByMemberId(String memberId, Connection connection) throws SQLException;

    List<Borrowing> findByBookId(String bookId, Connection connection) throws SQLException;

    boolean updateReturnStatus(String borrowId, LocalDate returnDate, Borrowing.BorrowingStatus status, Connection connection) throws SQLException;

    List<Borrowing> findBorrowedBooks(Connection connection) throws SQLException;
}
