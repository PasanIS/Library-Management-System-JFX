package service.custom.impl;

import model.dto.BorrowDTO;
import model.entity.Borrow;
import model.entity.Fine;
import repository.RepositoryFactory;
import repository.custom.BookRepository;
import repository.custom.BorrowRepository;
import repository.custom.FineRepository;
import service.custom.BorrowService;
import util.mapper.BorrowMapper;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository = (BorrowRepository) RepositoryFactory
            .getInstance().getRepository(RepositoryFactory.RepositoryType.BORROW);
    private final BookRepository bookRepository = (BookRepository) RepositoryFactory
            .getInstance().getRepository(RepositoryFactory.RepositoryType.BOOK);
    private final FineRepository fineRepository = (FineRepository) RepositoryFactory
            .getInstance().getRepository(RepositoryFactory.RepositoryType.FINE);

    @Override
    public boolean issueBook(BorrowDTO borrowDTO) throws RuntimeException { // Propagate RuntimeException
        try {
            issueBookIfAllowed(borrowDTO);

            if (borrowDTO.getBorrowId() == null || borrowDTO.getBorrowId().isEmpty()) {
                borrowDTO.setBorrowId(borrowRepository.generateNextBorrowId());
            }

            if (borrowDTO.getIssueDate() == null) {
                borrowDTO.setIssueDate(LocalDateTime.now());
            }

            Borrow borrow = BorrowMapper.toEntity(borrowDTO); // Mapper needs to handle nested DTOs to entities

            // --- Begin Transaction (pseudo-code) ---
            try {
                borrowRepository.save(borrow);
                // Use borrow.getBook().getBookId() if the entity has the full book object
                // If not, you might need to fetch the book entity from its repository
                bookRepository.reduceAvailableQuantity(borrow.getBook().getBookId()); // Update book quantity
                // conn.commit();
                return true;
            } catch (SQLException e) {
                // conn.rollback();
                throw new RuntimeException("Database error during book issue transaction: " + e.getMessage(), e);
            } finally {
                // conn.setAutoCommit(true);
                // conn.close();
            }
            // --- End Transaction ---

        } catch (SQLException e) {
            throw new RuntimeException("Database error during book issue: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean returnBook(String borrowId, LocalDate returnDate, boolean isFinePaid) throws RuntimeException {
        try {
            // 1. Get the borrow record
            Optional<Borrow> borrowOptional = borrowRepository.findById(borrowId);
            if (!borrowOptional.isPresent()) {
                throw new RuntimeException("Borrow record not found for ID: " + borrowId);
            }
            Borrow borrow = borrowOptional.get();

            // Check if already returned
            if (borrow.getReturnDate() != null) {
                throw new RuntimeException("Book with borrowing ID " + borrowId + " has already been returned.");
            }

            // 2. Calculate fine (Business Logic)
            LocalDate issueDate = borrow.getIssueDate().toLocalDate();
            LocalDate dueDate = issueDate.plusDays(14);

            long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
            double fineAmount = (overdueDays > 0) ? overdueDays * 10 : 0;

            // --- Begin Transaction (pseudo-code) ---
            try {
                // 3. Update borrow record (return date and calculated fine)
                borrowRepository.updateReturnDateAndFine(borrowId, returnDate.atStartOfDay(), fineAmount); // Your DAO method

                // 4. Add fine record if fine exists
                if (fineAmount > 0) {
                    Fine fine = new Fine(null, borrowId, fineAmount, LocalDate.now(), isFinePaid, isFinePaid ? LocalDate.now() : null);
                    fineRepository.save(fine);
                }

                // 5. Increase book available quantity
                String bookIdToUpdate = borrow.getBook().getBookId();
                bookRepository.increaseAvailableQuantity(bookIdToUpdate);

                return true;

            } catch (SQLException e) {
                throw new RuntimeException("SQL Error during book return transaction: " + e.getMessage(), e);
            } finally {
                // conn.setAutoCommit(true);
                // conn.close();
            }
            // --- End Transaction ---

        } catch (SQLException e) {
            throw new RuntimeException("Database error during return operation: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean returnBook(String borrowId, LocalDate returnDate, double fineAmount, boolean isFinePaid, LocalDate paidDate) {
        try {
            borrowRepository.updateReturnDateAndFine(borrowId, returnDate.atStartOfDay(), fineAmount);

            if (fineAmount > 0) {
                Fine fine = new Fine(null, borrowId, fineAmount, LocalDate.now(), isFinePaid, paidDate);
                fineRepository.save(fine);
            }

            Optional<Borrow> borrowOptional = borrowRepository.findById(borrowId);
            if (borrowOptional.isPresent()) {
                bookRepository.increaseAvailableQuantity(borrowOptional.get().getBook().getBookId());
            } else {
                System.err.println("Error: Borrow record not found for ID: " + borrowId);
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean hasAlreadyBorrowed(String memberId, String bookId) throws RuntimeException {
        try {
            return borrowRepository.hasAlreadyBorrowed(memberId, bookId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error checking if book is already borrowed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BorrowDTO> getBorrowingsByMember(String memberId) throws RuntimeException {
        try {
            List<Borrow> borrows = borrowRepository.findBorrowingsByMember(memberId);
            return BorrowMapper.toDtoList(borrows);
        } catch (SQLException e) {
            throw new RuntimeException("Database error fetching borrowings by member: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNextBorrowId() throws RuntimeException {
        try {
            return borrowRepository.generateNextBorrowId();
        } catch (SQLException e) {
            throw new RuntimeException("Database error generating next borrow ID: " + e.getMessage(), e);
        }
    }

    @Override
    public int countActiveBorrowingsByMember(String memberId) throws RuntimeException {
        try {
            return borrowRepository.countActiveBorrowingsByMember(memberId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error counting active borrowings: " + e.getMessage(), e);
        }
    }

    @Override
    public void issueBookIfAllowed(BorrowDTO borrowDTO) throws IllegalStateException, IllegalArgumentException {
        if (borrowDTO.getMemberDTO() == null || borrowDTO.getMemberDTO().getMemberId() == null ||
                borrowDTO.getBookDTO() == null || borrowDTO.getBookDTO().getBookId() == null) {
            throw new IllegalArgumentException("BorrowDTO must contain valid memberDTO and bookDTO with IDs.");
        }
        String memberId = borrowDTO.getMemberDTO().getMemberId();
        String bookId = borrowDTO.getBookDTO().getBookId();

        try {
            // Check if already borrowed
            if (borrowRepository.hasAlreadyBorrowed(memberId, bookId)) {
                throw new IllegalStateException("This member has already borrowed this book.");
            }

            // Enforce 2-book borrowing limit
            int activeBorrowings = borrowRepository.countActiveBorrowingsByMember(memberId);
            if (activeBorrowings >= 2) {
                throw new IllegalStateException("This member has reached the maximum borrowing limit of 2 books.");
            }
            // Check if book is available
        } catch (SQLException e) {
            throw new IllegalStateException("Database error during book issue check: " + e.getMessage(), e);
        }
    }
}
