package repository.custom.impl;

import model.dto.AuthorDTO;
import model.dto.BookDTO;
import model.dto.CategoryDTO;
import model.dto.PublisherDTO;
import model.entity.Author;
import model.entity.Book;
import model.entity.Category;
import model.entity.Publisher;
import repository.custom.BookRepository;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryImpl implements BookRepository {

    //-----------Optional (Helping) method to map a ResultSet row to a Book entity
    private Optional<Book> mapResultSetToBook(ResultSet resultSet) throws SQLException {
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
            return Optional.of(book);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Book entity) throws SQLException {
        String sql = "INSERT INTO books (book_id, title, isbn, author_id, category_id, publisher_id, total_quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getBookId());
            preparedStatement.setString(2, entity.getTitle());
            preparedStatement.setString(3, entity.getIsbn());
            preparedStatement.setString(4, entity.getAuthor().getAuthorId());
            preparedStatement.setString(5, entity.getCategory().getCategoryId());
            preparedStatement.setString(6, entity.getPublisher().getPublisherId());
            preparedStatement.setInt(7, entity.getTotalQty());
            preparedStatement.setInt(8, entity.getAvailableQty());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Book entity) throws SQLException {
        String sql = "UPDATE books SET title = ?, isbn = ?, author_id = ?, category_id = ?, publisher_id = ?, total_quantity = ?, available_quantity = ? WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setString(2, entity.getIsbn());
            preparedStatement.setString(3, entity.getAuthor().getAuthorId());
            preparedStatement.setString(4, entity.getCategory().getCategoryId());
            preparedStatement.setString(5, entity.getPublisher().getPublisherId());
            preparedStatement.setInt(6, entity.getTotalQty());
            preparedStatement.setInt(7, entity.getAvailableQty());
            preparedStatement.setString(8, entity.getBookId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Book> findById(String id) throws SQLException {
        String sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                "WHERE b.book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToBook(resultSet);
            }
        }
    }

    @Override
    public List<Book> findAll() throws SQLException {
        String sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "JOIN publishers p ON b.publisher_id = p.publisher_id";
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                mapResultSetToBook(resultSet).ifPresent(books::add);
            }
        }
        return books;
    }

    @Override
    public List<Book> findBooksByCategory(String categoryId) throws SQLException {
        String sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                "WHERE b.category_id = ?";
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    mapResultSetToBook(resultSet).ifPresent(books::add);
                }
            }
        }
        return books;
    }

    @Override
    public Optional<Book> findBookByIsbn(String isbn) throws SQLException {
        String sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                "WHERE b.isbn = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, isbn);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToBook(resultSet);
            }
        }
    }

    @Override
    public Optional<Book> searchBook(String searchType, String searchText) throws SQLException {
        String sql;
        String normalizedSearchText = searchText != null ? searchText.replace("-", "").toLowerCase() : "";

        if ("ISBN".equalsIgnoreCase(searchType)) {
            sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                    "FROM books b " +
                    "JOIN authors a ON b.author_id = a.author_id " +
                    "JOIN categories c ON b.category_id = c.category_id " +
                    "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                    "WHERE LOWER(REPLACE(b.isbn, '-', '')) LIKE ?";
        } else if ("Title".equalsIgnoreCase(searchType)) {
            sql = "SELECT b.*, a.name AS author_name, c.name AS category_name, p.name AS publisher_name " +
                    "FROM books b " +
                    "JOIN authors a ON b.author_id = a.author_id " +
                    "JOIN categories c ON b.category_id = c.category_id " +
                    "JOIN publishers p ON b.publisher_id = p.publisher_id " +
                    "WHERE LOWER(b.title) LIKE ?";
        } else {
            // Handle unsupported search types, e.g., throw an exception or return empty
            return Optional.empty();
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, "%" + normalizedSearchText + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToBook(resultSet);
            }
        }
    }

    @Override
    public List<String> getSuggestions(String type, String input) throws SQLException {
        List<String> suggestions = new ArrayList<>();
        String sql;
        String normalizedInput = input != null ? input.replace("-", "").toLowerCase() : "";

        if ("ISBN".equalsIgnoreCase(type)) {
            sql = "SELECT isbn FROM books WHERE LOWER(REPLACE(isbn, '-', '')) LIKE ? LIMIT 10";
        } else if ("Title".equalsIgnoreCase(type)) {
            sql = "SELECT title FROM books WHERE LOWER(title) LIKE ? LIMIT 10";
        } else {
            return suggestions;
        }

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + normalizedInput + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    suggestions.add(resultSet.getString(1));
                }
            }
        }
        return suggestions;
    }


    // Methods related to Author, Category, Publisher that were in BookDAO

    @Override
    public Optional<Author> findAuthorByName(String name) throws SQLException {
        String sql = "SELECT * FROM authors WHERE name = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Author(resultSet.getString("author_id"), resultSet.getString("name")));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Author saveAuthor(Author author) throws SQLException {
        String insert = "INSERT INTO authors (author_id, name) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertPreparedStatement = connection.prepareStatement(insert)) {
            insertPreparedStatement.setString(1, author.getAuthorId());
            insertPreparedStatement.setString(2, author.getName());
            insertPreparedStatement.executeUpdate();
            return author; // Return the saved author with generated ID
        }
    }

    @Override
    public Optional<Category> findCategoryByName(String name) throws SQLException {
        String sql = "SELECT * FROM categories WHERE name = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Category(resultSet.getString("category_id"), resultSet.getString("name")));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Category saveCategory(Category category) throws SQLException {
        String insert = "INSERT INTO categories (category_id, name) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertPreparedStatement = connection.prepareStatement(insert)) {
            insertPreparedStatement.setString(1, category.getCategoryId());
            insertPreparedStatement.setString(2, category.getName());
            insertPreparedStatement.executeUpdate();
            return category; // Return the saved category with generated ID
        }
    }

    // ---------- Publisher related methods, extracted from BookDAO
    @Override
    public Optional<Publisher> findPublisherById(String publisherId) throws SQLException {
        String sql = "SELECT * FROM publishers WHERE publisher_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, publisherId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Publisher(publisherId, resultSet.getString("name")));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Publisher savePublisher(Publisher publisher) throws SQLException {
        String insert = "INSERT INTO publishers (publisher_id, name) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement insertPreparedStatement = connection.prepareStatement(insert)) {
            insertPreparedStatement.setString(1, publisher.getPublisherId());
            insertPreparedStatement.setString(2, publisher.getName());
            insertPreparedStatement.executeUpdate();
            return publisher; // -----Return the saved publisher with ID
        }
    }

    @Override
    public void reduceAvailableQuantity(String bookId) throws SQLException {
        String sql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, bookId);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void increaseAvailableQuantity(String bookId) throws SQLException {
        String sql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, bookId);
            preparedStatement.executeUpdate();
        }
    }
}
