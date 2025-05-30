package dao;

import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;

public class BorrowDAO {

    // -------Check Borrowed or Not-----------
    public boolean hasAlreadyBorrowed(int memberId, int bookId) throws SQLException {

        String query = "SELECT COUNT(*) FROM borrowings WHERE member_id = ? AND book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, memberId);
            preparedStatement.setInt(2, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
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
}
