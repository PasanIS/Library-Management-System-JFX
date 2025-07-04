package service.custom.impl;

import model.dto.BookDTO;
import model.dto.CategoryDTO;
import model.entity.Author;
import model.entity.Book;
import model.entity.Category;
import model.entity.Publisher;
import repository.RepositoryFactory;
import repository.custom.BookRepository;
import service.custom.BookService;
import util.mapper.BookMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository = (BookRepository) RepositoryFactory
            .getInstance()
            .getRepository(RepositoryFactory.RepositoryType.BOOK);
    private final RandomIdGenerator idGenerator = new RandomIdGenerator(); // -----For random ID generation

    @Override
    public boolean addBook(BookDTO bookDTO) {
        try {
            // Get or create Author
            Author author = bookRepository.findAuthorByName(bookDTO.getAuthor().getName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author(idGenerator.generateAuthorId(),
                                bookDTO.getAuthor().getName());
                        try {
                            return bookRepository.saveAuthor(newAuthor);
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to save new author", e);
                        }
                    });
            bookDTO.getAuthor().setAuthorId(author.getAuthorId()); // -----Update DTO with ID

            // Get or create Category
            Category category = bookRepository.findCategoryByName(bookDTO.getCategory().getName())
                    .orElseGet(() -> {
                        Category newCategory = new Category(idGenerator.generateCategoryId(),
                                bookDTO.getCategory().getName());
                        try {
                            return bookRepository.saveCategory(newCategory);
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to save new category", e);
                        }
                    });
            bookDTO.getCategory().setCategoryId(category.getCategoryId()); // -----Update DTO with ID

            // Get or create Publisher
            Publisher publisher = bookRepository.findPublisherById("P001")
                    .orElseGet(() -> {
                        Publisher newPublisher = new Publisher("P001", "Default Publisher");
                        try {
                            return bookRepository.savePublisher(newPublisher);
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to save new publisher", e);
                        }
                    });
            bookDTO.getPublisher().setPublisherId(publisher.getPublisherId()); // -----Update DTO with ID

            // Map DTO to Entity and save book
            Book book = BookMapper.toEntity(bookDTO);
            book.setBookId(generateBookId());
            book.setAuthor(author);
            book.setCategory(category);
            book.setPublisher(publisher);
            book.setAvailableQty(book.getTotalQty());

            return bookRepository.save(book);

        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateBook(BookDTO bookDTO) {
        try {
            // Retrieve existing book to get current Author/Category/Publisher IDs
            Optional<Book> existingBookOpt = bookRepository.findBookByIsbn(bookDTO.getIsbn());
            if (existingBookOpt.isEmpty()) {
                return false; // -----Book not found for update
            }
            Book existingBook = existingBookOpt.get();

            // Handle Author update/getOrCreate
            Author author = bookRepository.findAuthorByName(bookDTO.getAuthor().getName())
                    .orElseGet(() -> {
                        Author newAuthor = new Author(idGenerator.generateAuthorId(), bookDTO.getAuthor().getName());
                        try {
                            return bookRepository.saveAuthor(newAuthor);
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to save new author", e);
                        }
                    });
            bookDTO.getAuthor().setAuthorId(author.getAuthorId());

            // Handle Category update/getOrCreate
            Category category = bookRepository.findCategoryByName(bookDTO.getCategory().getName())
                    .orElseGet(() -> {
                        Category newCategory = new Category(idGenerator.generateCategoryId(), bookDTO.getCategory().getName());
                        try {
                            return bookRepository.saveCategory(newCategory);
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to save new category", e);
                        }
                    });
            bookDTO.getCategory().setCategoryId(category.getCategoryId());

            // Handle Publisher update/getOrCreate
            Publisher publisher = bookRepository.findPublisherById("P001")
                    .orElseGet(() -> {
                        Publisher newPublisher = new Publisher("P001", "Default Publisher");
                        try {
                            return bookRepository.savePublisher(newPublisher);
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to save new publisher", e);
                        }
                    });
            bookDTO.getPublisher().setPublisherId(publisher.getPublisherId());


            // Update the entity with DTO values
            existingBook.setTitle(bookDTO.getTitle());
            existingBook.setIsbn(bookDTO.getIsbn());
            existingBook.setTotalQty(bookDTO.getTotalQty());

            existingBook.setAvailableQty(bookDTO.getAvailableQty());
            existingBook.setAuthor(author);
            existingBook.setCategory(category);
            existingBook.setPublisher(publisher);

            return bookRepository.update(existingBook);
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteBook(String bookId) {
        try {
            return bookRepository.delete(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<BookDTO> getBookById(String bookId) {
        try {
            return bookRepository.findById(bookId)
                    .map(BookMapper::toDto);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<BookDTO> findBookByIsbn(String isbn) {
        try {
            return bookRepository.findBookByIsbn(isbn)
                    .map(BookMapper::toDto);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<BookDTO> getBooksByCategory(String categoryId) {
        try {
            List<Book> books = bookRepository.findBooksByCategory(categoryId);
            return BookMapper.toDtoList(books);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of(); // -----Return empty list on error
        }
    }

    @Override
    public Optional<BookDTO> searchBook(String searchType, String searchText) {
        try {
            return bookRepository.searchBook(searchType, searchText)
                    .map(BookMapper::toDto);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<String> getBookSuggestions(String type, String input) {
        try {
            return bookRepository.getSuggestions(type, input);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean reduceBookQuantity(String bookId) {
        try {
            bookRepository.reduceAvailableQuantity(bookId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean increaseBookQuantity(String bookId) {
        try {
            bookRepository.increaseAvailableQuantity(bookId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        try {
            List<Category> categories = ((repository.custom.CategoryRepository) RepositoryFactory.getInstance()
                    .getRepository(RepositoryFactory.RepositoryType.CATEGORY)).findAllCategories();
            return BookMapper.toCategoryDtoList(categories);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public String generateBookId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // For generating IDs
    private static class RandomIdGenerator {

        private static int categoryCounter = 0;

        public String generateAuthorId() {
            return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        public String generateCategoryId() {
            categoryCounter++;
            return String.format("C%03d", categoryCounter);
        }
    }
}