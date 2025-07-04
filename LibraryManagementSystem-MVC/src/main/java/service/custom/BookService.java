package service.custom;

import model.dto.BookDTO;
import model.dto.CategoryDTO;
import service.SuperService;

import java.util.List;
import java.util.Optional;

public interface BookService extends SuperService {

    // Operations for Book
    boolean addBook(BookDTO bookDTO);
    boolean updateBook(BookDTO bookDTO);
    boolean deleteBook(String bookId);
    Optional<BookDTO> getBookById(String bookId);
    Optional<BookDTO> findBookByIsbn(String isbn);


    List<BookDTO> getBooksByCategory(String categoryId);
    Optional<BookDTO> searchBook(String searchType, String searchText);
    List<String> getBookSuggestions(String type, String input);

    // Operations for managing quantities
    boolean reduceBookQuantity(String bookId);
    boolean increaseBookQuantity(String bookId);

    // Operations for Categories
    List<CategoryDTO> getAllCategories();

    // Utility for generating Book IDs
    String generateBookId();
}
