package repository.custom;

import entity.Fine;
import repository.SuperRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FineRepository extends SuperRepository<Fine, String> {

    Optional<Fine> findByBorrowId(String borrowId, Connection connection) throws SQLException;

    boolean updatePaidStatus(String fineId, Fine.FinePaidStatus paidStatus, Connection connection) throws SQLException;

    List<Fine> findUnpaidFines(Connection connection) throws SQLException;
}
