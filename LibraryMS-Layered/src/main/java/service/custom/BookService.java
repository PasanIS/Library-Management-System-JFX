package service.custom;

import dto.BookDTO;
import service.SuperService;

import java.util.List;
import java.util.Optional;

public interface BookService extends SuperService<BookDTO, String> {

    boolean markAsDeleted(String id);

    Optional<BookDTO> findByIsbn(String isbn);

    boolean updateCopiesAvailable(String bookId, int newCopiesAvailable);

    List<BookDTO> searchBooks(String query);

//    boolean delete(String id);
}
