package repository.custom.impl;

import entity.Book;
import repository.custom.BookRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepositoryImpl implements BookRepository {
    @Override
    public boolean save(Book book, Connection connection) throws SQLException {
        String sql = "INSERT INTO Book (book_id, title, author, category, publisher, isbn, copies_available, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getCategory());
            ps.setString(5, book.getPublisher());
            ps.setString(6, book.getIsbn());
            ps.setInt(7, book.getCopiesAvailable());
            ps.setBoolean(8, book.isDeleted());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Book> findById(String id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Book WHERE book_id = ? AND is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Book book, Connection connection) throws SQLException {
        String sql = "UPDATE Book SET title = ?, author = ?, category = ?, publisher = ?, isbn = ?, copies_available = ? WHERE book_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setString(4, book.getPublisher());
            ps.setString(5, book.getIsbn());
            ps.setInt(6, book.getCopiesAvailable());
            ps.setString(7, book.getBookId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markAsDeleted(String id, Connection connection) throws SQLException {
        String sql = "UPDATE Book SET is_deleted = TRUE WHERE book_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id, Connection connection) throws SQLException {
//        String sql = "DELETE FROM Book WHERE book_id = ?";
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, id);
//            return ps.executeUpdate() > 0;
//        }
        throw new UnsupportedOperationException("Hard delete is not supported. Use markAsDeleted instead.");
    }

    @Override
    public List<Book> findAll(Connection connection) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Book WHERE is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        }
        return books;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Book WHERE isbn = ? AND is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updateCopiesAvailable(String bookId, int newCopiesAvailable, Connection connection) throws SQLException {
        String sql = "UPDATE Book SET copies_available = ? WHERE book_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newCopiesAvailable);
            ps.setString(2, bookId);
            return ps.executeUpdate() > 0;
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getString("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setCategory(rs.getString("category"));
        book.setPublisher(rs.getString("publisher"));
        book.setIsbn(rs.getString("isbn"));
        book.setCopiesAvailable(rs.getInt("copies_available"));
        book.setDeleted(rs.getBoolean("is_deleted"));
        return book;
    }
}
