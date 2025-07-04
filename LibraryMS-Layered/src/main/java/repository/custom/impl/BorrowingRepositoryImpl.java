package repository.custom.impl;

import entity.Borrowing;
import repository.custom.BorrowingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowingRepositoryImpl implements BorrowingRepository {
    @Override
    public boolean save(Borrowing borrowing, Connection connection) throws SQLException {
        String sql = "INSERT INTO Borrowing (borrow_id, member_id, book_id, borrow_date, due_date, return_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, borrowing.getBorrowId());
            ps.setString(2, borrowing.getMemberId());
            ps.setString(3, borrowing.getBookId());
            ps.setDate(4, Date.valueOf(borrowing.getBorrowDate()));
            ps.setDate(5, Date.valueOf(borrowing.getDueDate()));
            ps.setDate(6, borrowing.getReturnDate() != null ? Date.valueOf(borrowing.getReturnDate()) : null);
            ps.setString(7, borrowing.getStatus().name()); // Store enum as string
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Borrowing> findById(String id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Borrowing WHERE borrow_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBorrowing(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Borrowing borrowing, Connection connection) throws SQLException {
        String sql = "UPDATE Borrowing SET member_id = ?, book_id = ?, borrow_date = ?, due_date = ?, return_date = ?, status = ? WHERE borrow_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, borrowing.getMemberId());
            ps.setString(2, borrowing.getBookId());
            ps.setDate(3, Date.valueOf(borrowing.getBorrowDate()));
            ps.setDate(4, Date.valueOf(borrowing.getDueDate()));
            ps.setDate(5, borrowing.getReturnDate() != null ? Date.valueOf(borrowing.getReturnDate()) : null);
            ps.setString(6, borrowing.getStatus().name()); // Store enum as string
            ps.setString(7, borrowing.getBorrowId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id, Connection connection) throws SQLException {
        String sql = "DELETE FROM Borrowing WHERE borrow_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Borrowing> findAll(Connection connection) throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT * FROM Borrowing";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                borrowings.add(mapResultSetToBorrowing(rs));
            }
        }
        return borrowings;
    }

    @Override
    public List<Borrowing> findByMemberId(String memberId, Connection connection) throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT * FROM Borrowing WHERE member_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(mapResultSetToBorrowing(rs));
                }
            }
        }
        return borrowings;
    }

    @Override
    public List<Borrowing> findByBookId(String bookId, Connection connection) throws SQLException {
        List<Borrowing> borrowings = new ArrayList<>();
        String sql = "SELECT * FROM Borrowing WHERE book_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(mapResultSetToBorrowing(rs));
                }
            }
        }
        return borrowings;
    }

    @Override
    public boolean updateReturnStatus(String borrowId, java.time.LocalDate returnDate, Borrowing.BorrowingStatus status, Connection connection) throws SQLException {
        String sql = "UPDATE Borrowing SET return_date = ?, status = ? WHERE borrow_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, returnDate != null ? Date.valueOf(returnDate) : null);
            ps.setString(2, status.name());
            ps.setString(3, borrowId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Borrowing> findBorrowedBooks(Connection connection) throws SQLException {
        List<Borrowing> borrowedBooks = new ArrayList<>();
        String sql = "SELECT * FROM Borrowing WHERE status = 'borrowed'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                borrowedBooks.add(mapResultSetToBorrowing(rs));
            }
        }
        return borrowedBooks;
    }

    /**
     * Helper method to map a ResultSet row to a Borrowing object.
     * @param rs The ResultSet containing borrowing data.
     * @return A Borrowing object.
     * @throws SQLException if a database access error occurs.
     */
    private Borrowing mapResultSetToBorrowing(ResultSet rs) throws SQLException {
        Borrowing borrowing = new Borrowing();
        borrowing.setBorrowId(rs.getString("borrow_id"));
        borrowing.setMemberId(rs.getString("member_id"));
        borrowing.setBookId(rs.getString("book_id"));
        borrowing.setBorrowDate(rs.getDate("borrow_date").toLocalDate());
        borrowing.setDueDate(rs.getDate("due_date").toLocalDate());
        Date returnDateSql = rs.getDate("return_date");
        borrowing.setReturnDate(returnDateSql != null ? returnDateSql.toLocalDate() : null);
        borrowing.setStatus(Borrowing.BorrowingStatus.valueOf(rs.getString("status")));
        return borrowing;
    }
}
