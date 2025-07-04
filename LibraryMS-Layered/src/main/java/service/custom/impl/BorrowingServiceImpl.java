package service.custom.impl;

import dto.BorrowingDTO;
import dto.FineDTO;
import entity.Book;
import entity.Borrowing;
import entity.Fine;
import entity.Member;
import lombok.SneakyThrows;
import repository.RepositoryFactory;
import repository.custom.BookRepository;
import repository.custom.BorrowingRepository;
import repository.custom.MemberRepository;
import service.ServiceFactory;
import service.custom.BorrowingService;
import service.custom.FineService;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class BorrowingServiceImpl implements BorrowingService {
    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final FineService fineService; // Dependency on FineService

    // Define a default borrowing period (e.g., 14 days)
    private static final int BORROW_PERIOD_DAYS = 14;

    public BorrowingServiceImpl() {
        this.borrowingRepository = RepositoryFactory.getBorrowingRepository();
        this.bookRepository = RepositoryFactory.getBookRepository();
        this.memberRepository = RepositoryFactory.getMemberRepository();
        this.fineService = ServiceFactory.getFineService(); // Get instance of FineService
    }

    private BorrowingDTO convertToDTO(Borrowing borrowing, Connection connection) throws SQLException {
        if (borrowing == null) return null;

        String memberName = null;
        Optional<Member> memberOptional = memberRepository.findById(borrowing.getMemberId(), connection);
        if (memberOptional.isPresent()) {
            memberName = memberOptional.get().getName();
        }

        String bookTitle = null;
        Optional<Book> bookOptional = bookRepository.findById(borrowing.getBookId(), connection);
        if (bookOptional.isPresent()) {
            bookTitle = bookOptional.get().getTitle();
        }

        return new BorrowingDTO(
                borrowing.getBorrowId(),
                borrowing.getMemberId(),
                memberName,
                borrowing.getBookId(),
                bookTitle,
                borrowing.getBorrowDate(),
                borrowing.getDueDate(),
                borrowing.getReturnDate(),
                borrowing.getStatus()
        );
    }

    private Borrowing convertToEntity(BorrowingDTO dto) {
        if (dto == null) return null;
        // Note: memberName and bookTitle are not part of the entity, so they are not set here.
        return new Borrowing(
                dto.getBorrowId(),
                dto.getMemberId(),
                dto.getBookId(),
                dto.getBorrowDate(),
                dto.getDueDate(),
                dto.getReturnDate(),
                dto.getStatus()
        );
    }

    @SneakyThrows
    @Override
    public boolean save(BorrowingDTO borrowingDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = borrowingRepository.save(convertToEntity(borrowingDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error saving borrowing record: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public Optional<BorrowingDTO> findById(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            Connection finalConnection = connection;
            return borrowingRepository.findById(id, connection)
                    .map(borrowing -> {
                        try {
                            return convertToDTO(borrowing, finalConnection);
                        } catch (SQLException e) {
                            System.err.println("Error converting Borrowing entity to DTO: " + e.getMessage());
                            return null;
                        }
                    });
        } catch (SQLException e) {
            System.err.println("Error finding borrowing by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean update(BorrowingDTO borrowingDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = borrowingRepository.update(convertToEntity(borrowingDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error updating borrowing record: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean delete(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = borrowingRepository.delete(id, connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error deleting borrowing record: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public List<BorrowingDTO> findAll() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            Connection finalConnection = connection;
            return borrowingRepository.findAll(connection).stream()
                    .map(borrowing -> {
                        try {
                            return convertToDTO(borrowing, finalConnection);
                        } catch (SQLException e) {
                            System.err.println("Error converting Borrowing entity to DTO in findAll: " + e.getMessage());
                            return null; // Or handle more robustly
                        }
                    })
                    .filter(Objects::nonNull) // Filter out any nulls from conversion errors
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding all borrowing records: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }


    @SneakyThrows
    @Override
    public boolean borrowBook(BorrowingDTO borrowingDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection); // Start transaction

            // 1. Check book availability
            Optional<Book> bookOptional = bookRepository.findById(borrowingDTO.getBookId(), connection);
            if (bookOptional.isEmpty()) {
                System.err.println("Book with ID " + borrowingDTO.getBookId() + " not found.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }
            Book book = bookOptional.get();
            if (book.getCopiesAvailable() <= 0) {
                System.err.println("No copies available for book: " + book.getTitle());
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            // 2. Decrement book copies
            int newCopiesAvailable = book.getCopiesAvailable() - 1;
            boolean bookUpdateSuccess = bookRepository.updateCopiesAvailable(book.getBookId(), newCopiesAvailable, connection);
            if (!bookUpdateSuccess) {
                System.err.println("Failed to update copies for book: " + book.getTitle());
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            // 3. Create new borrowing record
            // Generate a unique borrow ID
            borrowingDTO.setBorrowId("BOR" + UUID.randomUUID().toString().substring(0, 7).toUpperCase());
            borrowingDTO.setBorrowDate(LocalDate.now());
            borrowingDTO.setDueDate(calculateDueDate(borrowingDTO.getBorrowDate()));
            borrowingDTO.setStatus(Borrowing.BorrowingStatus.borrowed);
            borrowingDTO.setReturnDate(null);

            boolean borrowingSaveSuccess = borrowingRepository.save(convertToEntity(borrowingDTO), connection);
            if (!borrowingSaveSuccess) {
                System.err.println("Failed to save borrowing record.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            DBConnection.commitTransaction(connection); // Commit if all successful
            System.out.println("Book '" + book.getTitle() + "' borrowed successfully by member " + borrowingDTO.getMemberId());
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed during borrowBook: " + e.getMessage());
            DBConnection.rollbackTransaction(connection); // Rollback on any exception
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection); // Finalize connection
        }
    }


    @SneakyThrows
    @Override
    public boolean returnBook(String borrowId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection); // Start transaction

            // 1. Find the borrowing record
            Optional<Borrowing> borrowingOptional = borrowingRepository.findById(borrowId, connection);
            if (borrowingOptional.isEmpty()) {
                System.err.println("Borrowing record with ID " + borrowId + " not found.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }
            Borrowing borrowing = borrowingOptional.get();

            // Check if already returned
            if (borrowing.getStatus() == Borrowing.BorrowingStatus.returned) {
                System.err.println("Book already returned for borrowing ID: " + borrowId);
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            LocalDate actualReturnDate = LocalDate.now();
            Borrowing.BorrowingStatus newStatus = Borrowing.BorrowingStatus.returned;

            // Check for late return and calculate fine
            Double fineAmount = 0.00;
            if (actualReturnDate.isAfter(borrowing.getDueDate())) {
                fineAmount = fineService.calculateFineAmount(borrowing.getDueDate(), actualReturnDate);
                newStatus = Borrowing.BorrowingStatus.late; // Mark as late if a fine is incurred
            }

            // 2. Update borrowing record status and return date
            boolean borrowingUpdateSuccess = borrowingRepository.updateReturnStatus(
                    borrowing.getBorrowId(), actualReturnDate, newStatus, connection);
            if (!borrowingUpdateSuccess) {
                System.err.println("Failed to update borrowing record status for ID: " + borrowId);
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            // 3. Increment book copies
            Optional<Book> bookOptional = bookRepository.findById(borrowing.getBookId(), connection);
            if (bookOptional.isEmpty()) {
                System.err.println("Associated book with ID " + borrowing.getBookId() + " not found.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }
            Book book = bookOptional.get();
            boolean bookUpdateSuccess = bookRepository.updateCopiesAvailable(
                    book.getBookId(), book.getCopiesAvailable() + 1, connection);
            if (!bookUpdateSuccess) {
                System.err.println("Failed to increment copies for book: " + book.getTitle());
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            // 4. Create fine record if applicable
            if (fineAmount > 0.00) {
                FineDTO fineDTO = new FineDTO(
                        "FIN" + UUID.randomUUID().toString().substring(0, 7).toUpperCase(), // Generate unique fine ID
                        borrowing.getBorrowId(),
                        book.getTitle(), // For DTO display
                        memberRepository.findById(borrowing.getMemberId(), connection).map(Member::getName).orElse("Unknown Member"), // For DTO display
                        fineAmount,
                        Fine.FinePaidStatus.no
                );

                boolean fineCreationSuccess = fineService.createFine(fineDTO);
                if (!fineCreationSuccess) {
                    System.err.println("Failed to create fine record for borrowing ID: " + borrowId);
                    DBConnection.rollbackTransaction(connection);
                    return false;
                }
            }

            DBConnection.commitTransaction(connection); // Commit if all successful
            System.out.println("Book '" + book.getTitle() + "' returned successfully for borrowing ID: " + borrowId);
            return true;

        } catch (SQLException e) {
            System.err.println("Transaction failed during returnBook: " + e.getMessage());
            DBConnection.rollbackTransaction(connection); // Rollback on any exception
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection); // Finalize connection
        }
    }

    @Override
    public List<BorrowingDTO> findBorrowingsByMemberId(String memberId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            Connection finalConnection = connection;
            return borrowingRepository.findByMemberId(memberId, connection).stream()
                    .map(borrowing -> {
                        try {
                            return convertToDTO(borrowing, finalConnection);
                        } catch (SQLException e) {
                            System.err.println("Error converting Borrowing entity to DTO in findBorrowingsByMemberId: " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding borrowings by member ID: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<BorrowingDTO> findBorrowedBooks() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            Connection finalConnection = connection;
            return borrowingRepository.findBorrowedBooks(connection).stream()
                    .map(borrowing -> {
                        try {
                            return convertToDTO(borrowing, finalConnection);
                        } catch (SQLException e) {
                            System.err.println("Error converting Borrowing entity to DTO in findBorrowedBooks: " + e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding borrowed books: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public LocalDate calculateDueDate(LocalDate borrowDate) {
        return borrowDate.plusDays(BORROW_PERIOD_DAYS);
    }
}
