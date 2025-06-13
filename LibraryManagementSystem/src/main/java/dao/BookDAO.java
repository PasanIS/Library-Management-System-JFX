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

    // ---------Add Book-----------
    public static boolean addBook(Book book) {

        String sql = "INSERT INTO books (isbn, title, author, category, copies) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getIsbn());
            preparedStatement.setString(2, book.getTitle());
            preparedStatement.setString(3, book.getAuthor());
            preparedStatement.setString(4, book.getCategory());
            preparedStatement.setInt(5, book.getCopies());

            int rows = preparedStatement.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------Get Book By ISBN-----------
    public Book getBookByIsbn (String isbn) throws SQLException {

        String sql = "SELECT * FROM books WHERE isbn = ? AND status = 'AVAILABLE'";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            return null;
        }
    }

    public boolean updateBook(Book book) {

        String sql = "UPDATE books SET title = ?, author = ?, category = ?, copies = ? WHERE isbn = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getAuthor());
            preparedStatement.setString(3, book.getCategory());
            preparedStatement.setInt(4, book.getCopies());
            preparedStatement.setString(5, book.getIsbn());

            int rows = preparedStatement.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------Delete Book-----------
    public boolean removeBook(String isbn) {

        String sql = "UPDATE books SET status = 'REMOVED' WHERE isbn = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            System.out.println("Deleting book with ISBN: [" + isbn + "]");

            preparedStatement.setString(1, isbn);
            int rows = preparedStatement.executeUpdate();

            System.out.println("Rows affected: " + rows);

            return rows > 0;

        } catch (Exception e) {

            System.out.println("Error deleting book: " + e.getMessage());

            e.printStackTrace();
            return false;
        }
    }

    // --------------Get Suggestions---------------
    public static List<String> getSuggestions(String type, String input) {
        List<String> results = new ArrayList<>();
        String column;

        switch (type.toLowerCase()) {
            case "isbn": column = "isbn"; break;
            case "title": column = "title"; break;
            case "category": column = "category"; break;
            default: return results;
        }

        String query = "SELECT DISTINCT " + column + " FROM books WHERE " + column + " LIKE ? LIMIT 10";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, input + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // ------------Search Books------------
    public static Book searchBook(String searchType, String searchText) {
        Book book = null;

        try (Connection connection = DBConnection.getConnection()) {
            PreparedStatement preparedStatement;

            if ("ISBN".equals(searchType)) {
                preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE isbn = ? AND status = 'AVAILABLE'");
            } else if ("Title".equals(searchType)) {
                preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE title = ? AND status = 'AVAILABLE'");
            } else {
                return null;
            }

            preparedStatement.setString(1, searchText.trim());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                book = new Book(
                        resultSet.getInt("book_id"),
                        resultSet.getString("title"),
                        resultSet.getString("author"),
                        resultSet.getString("category"),
                        resultSet.getString("isbn"),
                        resultSet.getInt("copies")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }
}
