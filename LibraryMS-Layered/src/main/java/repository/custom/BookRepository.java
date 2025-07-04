package repository.custom;

import entity.Book;
import repository.SuperRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends SuperRepository<Book, String> {

    Optional<Book> findByIsbn(String isbn, Connection connection) throws SQLException;

    boolean updateCopiesAvailable(String bookId, int newCopiesAvailable, Connection connection) throws SQLException;

    boolean save(Book book, Connection connection) throws SQLException;
    Optional<Book> findById(String id, Connection connection) throws SQLException;
    boolean update(Book book, Connection connection) throws SQLException;
    boolean markAsDeleted(String id, Connection connection) throws SQLException;

    List<Book> findAll(Connection connection) throws SQLException;
}
