package service.custom.impl;

import dto.FineDTO;
import entity.Fine;
import lombok.SneakyThrows;
import repository.RepositoryFactory;
import repository.custom.FineRepository;
import service.custom.FineService;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FineServiceImpl implements FineService {
    private final FineRepository fineRepository;

    // Define fine rate per day
    private static final Double FINE_RATE_PER_DAY = new Double("10.00");

    public FineServiceImpl() {
        this.fineRepository = RepositoryFactory.getFineRepository();
    }

    private FineDTO convertToDTO(Fine fine) {
        if (fine == null) return null;
        return new FineDTO(
                fine.getFineId(),
                fine.getBorrowId(),
                null, // bookTitle not directly in Fine entity
                null, // memberName not directly in Fine entity
                fine.getAmount(),
                fine.getPaid()
        );
    }

    private Fine convertToEntity(FineDTO dto) {
        if (dto == null) return null;
        return new Fine(
                dto.getFineId(),
                dto.getBorrowId(),
                dto.getAmount(),
                dto.getPaid()
        );
    }

    @SneakyThrows
    @Override
    public boolean save(FineDTO fineDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = fineRepository.save(convertToEntity(fineDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error saving fine: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public Optional<FineDTO> findById(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return fineRepository.findById(id, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding fine by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean update(FineDTO fineDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = fineRepository.update(convertToEntity(fineDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error updating fine: " + e.getMessage());
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
            boolean result = fineRepository.delete(id, connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error deleting fine: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public List<FineDTO> findAll() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return fineRepository.findAll(connection).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding all fines: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public Double calculateFineAmount(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate.isAfter(dueDate)) {
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
            return FINE_RATE_PER_DAY * daysOverdue;
        }
        return 0.00;
    }

    @Override
    public boolean createFine(FineDTO fineDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return fineRepository.save(convertToEntity(fineDTO), connection);
        } catch (SQLException e) {
            System.err.println("Error creating fine: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean payFine(String fineId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = fineRepository.updatePaidStatus(fineId, Fine.FinePaidStatus.yes, connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error paying fine: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public Optional<FineDTO> findFineByBorrowId(String borrowId) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return fineRepository.findByBorrowId(borrowId, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding fine by borrowing ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<FineDTO> findUnpaidFines() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return fineRepository.findUnpaidFines(connection).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding unpaid fines: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }
}
