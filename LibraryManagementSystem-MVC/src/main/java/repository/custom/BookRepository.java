package repository.custom;

import model.dto.BookDTO;
import model.entity.Author;
import model.entity.Book;
import model.entity.Category;
import model.entity.Publisher;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends CRUDRepository<Book, String> {

    List<Book> findBooksByCategory(String categoryId) throws SQLException;
    Optional<Book> findBookByIsbn(String isbn) throws SQLException;

    // Custom search for book (by ISBN or Title, as seen in SearchBookFormController)

    Optional<Book> searchBook(String searchType, String searchText) throws SQLException;
    List<String> getSuggestions(String type, String input) throws SQLException;

    // Methods related to Author, Category, Publisher that were in BookDAO

    Optional<Author> findAuthorByName(String name) throws SQLException;
    Author saveAuthor(Author author) throws SQLException; // For getOrCreate logic
    Optional<Category> findCategoryByName(String name) throws SQLException;
    Category saveCategory(Category category) throws SQLException; // For getOrCreate logic

    Optional<Publisher> findPublisherByName(String name) throws SQLException;
    Publisher savePublisher(Publisher publisher) throws SQLException; // For getOrCreate logic

    void reduceAvailableQuantity(String bookId) throws SQLException;
    void increaseAvailableQuantity(String bookId) throws SQLException;

}
