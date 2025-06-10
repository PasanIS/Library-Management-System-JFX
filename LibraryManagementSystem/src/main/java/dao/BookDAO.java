package dao;

import model.Book;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // -------Get All Categories-----------
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();

        String query = "SELECT DISTINCT category FROM books";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                categories.add(resultSet.getString("category"));
            }
        }
        return categories;
    }

    // -------Get Books By Category-----------
    public List<Book> getBooksByCategory(String category) throws SQLException {
        List<Book> books = new ArrayList<>();

        String query = "SELECT * FROM books WHERE category = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, category);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Book book = new Book(
                        resultSet.getInt("book_id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getInt("copies")
                );
                books.add(book);
            }
        }
        return books;
    }

    // ----------Get Book By ID-----------
    public Book getBookById(int bookId) throws SQLException {

        String query = "SELECT * FROM books WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new Book(
                            resultSet.getInt("book_id"),
                            resultSet.getString("title"),
                            resultSet.getString("author"),
                            resultSet.getString("category"),
                            resultSet.getString("isbn"),
                            resultSet.getInt("copies")
                    );
                }
            }
        }
        return null;
    }

    // -------Reduce The Copies-----------
    public void reduceCopies(int bookId) throws SQLException {

        String query = "UPDATE books SET copies = copies - 1 WHERE book_id = ? AND copies > 0";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.executeUpdate();
        }
    }

    // -------Increase The Copies-----------
    public void increaseCopies(int bookId) throws SQLException {

        String sql = "UPDATE books SET copies = copies + 1 WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, bookId);
            preparedStatement.executeUpdate();
        }
    }
}
