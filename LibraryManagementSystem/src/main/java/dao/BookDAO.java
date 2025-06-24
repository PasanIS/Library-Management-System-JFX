package dao;

import model.Author;
import model.Book;
import model.Category;
import model.Publisher;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // -------Get All Categories-----------
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();

        String query = "SELECT * FROM categories";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getString("category_id"),
                        resultSet.getString("name")
                ));
            }
        }
        return categories;
    }

    // -------Get Books By Category-----------
    public List<Book> getBooksByCategory(String categoryId) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                "WHERE b.category_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, categoryId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Book book = new Book();
                book.setBookId(resultSet.getString("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setTotalQty(resultSet.getInt("total_quantity"));
                book.setAvailableQty(resultSet.getInt("available_quantity"));

                Author author = new Author(resultSet.getString("author_id"), resultSet.getString("author_name"));
                Category category = new Category(resultSet.getString("category_id"), resultSet.getString("category_name"));
                Publisher publisher = new Publisher(resultSet.getString("publisher_id"), resultSet.getString("publisher_name"));

                book.setAuthor(author);
                book.setCategory(category);
                book.setPublisher(publisher);

                books.add(book);
            }
        }

        return books;
    }

    // ----------Get Book By ID-----------
    public Book getBookById(String bookId) throws SQLException {

        String query = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                "WHERE b.book_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, bookId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Book book = new Book();
                book.setBookId(resultSet.getString("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setTotalQty(resultSet.getInt("total_quantity"));
                book.setAvailableQty(resultSet.getInt("available_quantity"));

                Author author = new Author(resultSet.getString("author_id"), resultSet.getString("author_name"));
                Category category = new Category(resultSet.getString("category_id"), resultSet.getString("category_name"));
                Publisher publisher = new Publisher(resultSet.getString("publisher_id"), resultSet.getString("publisher_name"));

                book.setAuthor(author);
                book.setCategory(category);
                book.setPublisher(publisher);

                return book;
            }
        }
        return null;
    }

    // -------Reduce The Copies-----------
    public void reduceAvailableQty(String bookId) throws SQLException {

        String query = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id = ? AND available_quantity > 0";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, bookId);
            preparedStatement.executeUpdate();
        }
    }

    // -------Increase The Copies-----------
    public void increaseAvailableQty(String bookId) throws SQLException {

        String sql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, bookId);
            preparedStatement.executeUpdate();
        }
    }

    // ---------Add Book-----------
    public static boolean addBook(Book book) {

        String sql = "INSERT INTO books (book_id, title, isbn, author_id, category_id, publisher_id, total_quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, book.getBookId());
            preparedStatement.setString(2, book.getTitle());
            preparedStatement.setString(3, book.getIsbn());
            preparedStatement.setString(4, book.getAuthor().getAuthorId());
            preparedStatement.setString(5, book.getCategory().getCategoryId());
            preparedStatement.setString(6, book.getPublisher().getPublisherId());
            preparedStatement.setInt(7, book.getTotalQty());
            preparedStatement.setInt(8, book.getAvailableQty());

            int rows = preparedStatement.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    // ---------Get Book By ISBN-----------
//    public Book getBookByIsbn (String isbn) throws SQLException {
//
//        String sql = "SELECT * FROM books WHERE isbn = ? AND status = 'AVAILABLE'";
//        try (Connection connection = DBConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
//            preparedStatement.setString(1, isbn);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                return new Book(
//                        resultSet.getInt("book_id"),
//                        resultSet.getString("title"),
//                        resultSet.getString("author"),
//                        resultSet.getString("category"),
//                        resultSet.getString("isbn"),
//                        resultSet.getInt("copies")
//                );
//            }
//            return null;
//        }
//    }

    public boolean updateBook(Book book) {

        String sql = "UPDATE books SET title = ?, isbn = ?, author_id = ?, category_id = ?, publisher_id = ?, total_quantity = ?, available_quantity = ? WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setString(3, book.getAuthor().getAuthorId());
            preparedStatement.setString(4, book.getCategory().getCategoryId());
            preparedStatement.setString(5, book.getPublisher().getPublisherId());
            preparedStatement.setInt(6, book.getTotalQty());
            preparedStatement.setInt(7, book.getAvailableQty());
            preparedStatement.setString(8, book.getBookId());

            int rows = preparedStatement.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------Delete Book-----------
    public boolean removeBook(String bookId) {

        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, bookId);
            int rows = preparedStatement.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    // --------------Get Suggestions---------------
//    public static List<String> getSuggestions(String type, String input) {
//        List<String> results = new ArrayList<>();
//        String column;
//
//        switch (type.toLowerCase()) {
//            case "isbn": column = "isbn"; break;
//            case "title": column = "title"; break;
//            case "category": column = "category"; break;
//            default: return results;
//        }
//
//        String query = "SELECT DISTINCT " + column + " FROM books WHERE " + column + " LIKE ? LIMIT 10";
//
//        try (Connection connection = DBConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//
//            preparedStatement.setString(1, input + "%");
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                results.add(resultSet.getString(1));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return results;
//    }

    // ------------Search Books------------
    public static Book searchBook(String searchType, String searchText) {
        Book book = null;
        String sql = null;

        if ("ISBN".equalsIgnoreCase(searchType)) {
            sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                    "FROM books b " +
                    "JOIN authors a ON b.author_id = a.author_id " +
                    "JOIN categories c ON b.category_id = c.category_id " +
                    "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                    "WHERE b.isbn = ?";
        } else if ("Title".equalsIgnoreCase(searchType)) {
            sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                    "FROM books b " +
                    "JOIN authors a ON b.author_id = a.author_id " +
                    "JOIN categories c ON b.category_id = c.category_id " +
                    "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                    "WHERE b.title = ?";
        }

        if (sql == null) return null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, searchText.trim());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                book = new Book();
                book.setBookId(resultSet.getString("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setIsbn(resultSet.getString("isbn"));
                book.setTotalQty(resultSet.getInt("total_quantity"));
                book.setAvailableQty(resultSet.getInt("available_quantity"));

                Author author = new Author(resultSet.getString("author_id"), resultSet.getString("author_name"));
                Category category = new Category(resultSet.getString("category_id"), resultSet.getString("category_name"));
                Publisher publisher = new Publisher(resultSet.getString("publisher_id"), resultSet.getString("publisher_name"));

                book.setAuthor(author);
                book.setCategory(category);
                book.setPublisher(publisher);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }
}
