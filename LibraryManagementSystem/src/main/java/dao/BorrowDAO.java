package dao;

import model.Borrow;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    // -------Check Borrowed or Not-----------
    public boolean hasAlreadyBorrowed(int memberId, int bookId) throws SQLException {

        String query = "SELECT * FROM borrowings WHERE member_id = ? AND book_id = ? AND return_date IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberId);
            preparedStatement.setInt(2, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    // -------Issue Book-----------
    public void issueBook(int memberId, int bookId, LocalDateTime issueDate) throws SQLException {

        String query = "INSERT INTO borrowings (member_id, book_id, issue_date) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberId);
            preparedStatement.setInt(2, bookId);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(issueDate));
            preparedStatement.executeUpdate();
        }
     }

    // -------Get Borrowings-----------
    public List<Borrow> getBorrowingsByMember(int memberId) throws SQLException {
        List<Borrow> list = new ArrayList<>();

        String sql = "SELECT * FROM borrowings WHERE member_id = ? AND return_date IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    LocalDateTime issueDate = resultSet.getTimestamp("issue_date").toLocalDateTime();
                    LocalDateTime returnDate = resultSet.getTimestamp("return_date") != null
                            ? resultSet.getTimestamp("return_date").toLocalDateTime()
                            : null;

                    Borrow borrow = new Borrow(
                            resultSet.getInt("borrow_id"),
                            resultSet.getInt("member_id"),
                            resultSet.getInt("book_id"),
                            issueDate,
                            returnDate,
                            resultSet.getDouble("fine")
                    );
                    list.add(borrow);
                }
            }
        }
        return list;
    }

    // -------Return Book-----------
    public void returnBook(int borrowId, LocalDate returnDate, double fine) throws SQLException {

        String sql = "UPDATE borrowings SET return_date = ?, fine = ? WHERE borrow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(returnDate));
            preparedStatement.setDouble(2, fine);
            preparedStatement.setInt(3, borrowId);
            preparedStatement.executeUpdate();
        }
    }
}
