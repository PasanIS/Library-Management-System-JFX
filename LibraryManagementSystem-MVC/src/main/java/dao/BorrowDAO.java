package dao;

import model.Book;
import model.Borrow;
import model.Member;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    // -------Check Borrowed or Not-----------
    public boolean hasAlreadyBorrowed(String memberId, String bookId) throws SQLException {

        String query = "SELECT * FROM borrowings WHERE member_id = ? AND book_id = ? AND return_date IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    // -------Issue Book-----------
    public void issueBook(String memberId, String bookId, LocalDateTime issueDate) throws SQLException {

        String borrowId = generateNextBorrowId();

        String sql = "INSERT INTO borrowings (borrow_id, member_id, book_id, borrow_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, borrowId);
            preparedStatement.setString(2, memberId);
            preparedStatement.setString(3, bookId);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(issueDate));
            preparedStatement.executeUpdate();
        }
     }

    // -------Get Borrowings-----------
    public List<Borrow> getBorrowingsByMember(String memberId) throws SQLException {
        List<Borrow> list = new ArrayList<>();

        String sql = "SELECT b.*, bo.title, m.full_name, f.fine_amount " +
                "FROM borrowings b " +
                "JOIN books bo ON b.book_id = bo.book_id " +
                "JOIN members m ON b.member_id = m.member_id " +
                "LEFT JOIN fines f ON b.borrow_id = f.borrow_id " +
                "WHERE b.member_id = ? AND b.return_date IS NULL";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDateTime issueDate = resultSet.getTimestamp("borrow_date").toLocalDateTime();
                    Timestamp returnTimestamp = resultSet.getTimestamp("return_date");
                    LocalDateTime returnDate = returnTimestamp != null ? returnTimestamp.toLocalDateTime() : null;

                    Borrow borrow = new Borrow();
                    borrow.setBorrowId(resultSet.getString("borrow_id"));

                    Member member = new Member();
                    member.setMemberId(resultSet.getString("member_id"));
                    member.setFullName(resultSet.getString("full_name"));

                    Book book = new Book();
                    book.setBookId(resultSet.getString("book_id"));
                    book.setTitle(resultSet.getString("title"));

                    borrow.setMember(member);
                    borrow.setBook(book);
                    borrow.setIssueDate(issueDate);
                    borrow.setReturnDate(returnDate);
                    borrow.setFine(resultSet.getDouble("fine_amount"));

                    list.add(borrow);
                }
            }
        }
        return list;
    }

    // -------Return Book-----------
    public void returnBook(String borrowId, LocalDate returnDate, double fine) throws SQLException {

        String sqlUpdateBorrow = "UPDATE borrowings SET return_date = ? WHERE borrow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps1 = connection.prepareStatement(sqlUpdateBorrow)) {

            ps1.setDate(1, java.sql.Date.valueOf(returnDate));
            ps1.setString(2, borrowId);
            ps1.executeUpdate();
        }

        if (fine > 0) {
            String sqlInsertFine = "INSERT INTO fines (borrow_id, fine_amount, calculated_date, paid, paid_date) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement ps2 = connection.prepareStatement(sqlInsertFine)) {

                ps2.setString(1, borrowId);
                ps2.setDouble(2, fine);
                ps2.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                ps2.setBoolean(4, false); // not paid yet
                ps2.setNull(5, Types.DATE); // no paid date yet
                ps2.executeUpdate();
            }
        }
    }

    // -------Generate Next Borrow ID-----------
    public String generateNextBorrowId() throws SQLException {
        String sql = "SELECT borrow_id FROM borrowings ORDER BY borrow_id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String lastId = rs.getString("borrow_id");

                // Check format before processing
                if (lastId != null && lastId.startsWith("BR") && lastId.length() >= 5) {
                    try {
                        int number = Integer.parseInt(lastId.substring(2));
                        return String.format("BR%03d", number + 1);
                    } catch (NumberFormatException e) {
                        // skip malformed ID and continue
                        continue;
                    }
                }
            }
        }

        // No valid borrow ID found, start fresh
        return "BR001";
    }


    //------------Issue Book If Allowed-------------
    public void issueBookIfAllowed(String memberId, String bookId, LocalDateTime issueDate) throws SQLException {
        int activeBorrowCount = countActiveBorrowingsByMember(memberId);
        if (activeBorrowCount >= 2) {
            throw new IllegalStateException("Member has already borrowed maximum number of books (2).");
        }
        issueBook(memberId, bookId, issueDate);
    }

    public int countActiveBorrowingsByMember(String memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrowings WHERE member_id = ? AND return_date IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

}
