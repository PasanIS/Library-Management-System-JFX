package service.custom.impl;

import dto.BookDTO;
import entity.Book;
import lombok.SneakyThrows;
import repository.RepositoryFactory;
import repository.custom.BookRepository;
import service.custom.BookService;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl() {
        this.bookRepository = RepositoryFactory.getBookRepository();
    }

    private BookDTO convertToDTO(Book book) {
        if (book == null) return null;
        return new BookDTO(
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getPublisher(),
                book.getIsbn(),
                book.getCopiesAvailable(),
                book.isDeleted()
        );
    }

    private Book convertToEntity(BookDTO dto) {
        if (dto == null) return null;
        return new Book(
                dto.getBookId(),
                dto.getTitle(),
                dto.getAuthor(),
                dto.getCategory(),
                dto.getPublisher(),
                dto.getIsbn(),
                dto.getCopiesAvailable(),
                dto.isDeleted()
        );
    }

    @SneakyThrows
    @Override
    public boolean save(BookDTO bookDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection); // Start transaction
            boolean result = bookRepository.save(convertToEntity(bookDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection); // Commit if successful
            } else {
                DBConnection.rollbackTransaction(connection); // Rollback if not successful
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error saving book: " + e.getMessage());
            DBConnection.rollbackTransaction(connection); // Rollback on exception
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection); // Finalize connection
        }
    }

    @Override
    public Optional<BookDTO> findById(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return bookRepository.findById(id, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding book by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean update(BookDTO bookDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection); // Start transaction
            boolean result = bookRepository.update(convertToEntity(bookDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection); // Commit if successful
            } else {
                DBConnection.rollbackTransaction(connection); // Rollback if not successful
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage()); // Log error
            DBConnection.rollbackTransaction(connection); // Rollback on exception
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection); // Finalize connection
        }
    }

    @SneakyThrows
    @Override
    public boolean markAsDeleted(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = bookRepository.markAsDeleted(id, connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error marking book as deleted: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean delete(String id) {
//        Connection connection = null;
//        try {
//            connection = DBConnection.getConnection();
//            DBConnection.beginTransaction(connection); // Start transaction
//            boolean result = bookRepository.delete(id, connection);
//            if (result) {
//                DBConnection.commitTransaction(connection); // Commit if successful
//            } else {
//                DBConnection.rollbackTransaction(connection); // Rollback if not successful
//            }
//            return result;
//        } catch (SQLException e) {
//            System.err.println("Error deleting book: " + e.getMessage()); // Log error
//            DBConnection.rollbackTransaction(connection); // Rollback on exception
//            return false;
//        } finally {
//            DBConnection.finalizeTransaction(connection); // Finalize connection
//        }
        throw new UnsupportedOperationException("Hard delete is not supported. Use markAsDeleted instead.");
    }

    @Override
    public List<BookDTO> findAll() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return bookRepository.findAll(connection).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding all books: " + e.getMessage());
            return List.of(); // Return empty list on error
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public Optional<BookDTO> findByIsbn(String isbn) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return bookRepository.findByIsbn(isbn, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding book by ISBN: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean updateCopiesAvailable(String bookId, int newCopiesAvailable) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = bookRepository.updateCopiesAvailable(bookId, newCopiesAvailable, connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error updating book copies available: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public List<BookDTO> searchBooks(String query) {
        Connection connection = null;
        List<Book> books = new ArrayList<>();
        try {
            connection = DBConnection.getConnection();
            books = bookRepository.findAll(connection);
            String lowerCaseQuery = query.toLowerCase();
            return books.stream()
                    .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                            book.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                            book.getCategory().toLowerCase().contains(lowerCaseQuery) ||
                            book.getIsbn().toLowerCase().contains(lowerCaseQuery))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }
}
