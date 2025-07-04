package repository.custom.impl;

import entity.Fine;
import repository.custom.FineRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FineRepositoryImpl implements FineRepository {
    @Override
    public boolean save(Fine fine, Connection connection) throws SQLException {
        String sql = "INSERT INTO Fine (fine_id, borrow_id, amount, paid) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fine.getFineId());
            ps.setString(2, fine.getBorrowId());
            ps.setDouble(3, fine.getAmount());
            ps.setString(4, fine.getPaid().name()); // Store enum as string
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Fine> findById(String id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Fine WHERE fine_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFine(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Fine fine, Connection connection) throws SQLException {
        String sql = "UPDATE Fine SET borrow_id = ?, amount = ?, paid = ? WHERE fine_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fine.getBorrowId());
            ps.setDouble(2, fine.getAmount());
            ps.setString(3, fine.getPaid().name()); // Store enum as string
            ps.setString(4, fine.getFineId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id, Connection connection) throws SQLException {
        String sql = "DELETE FROM Fine WHERE fine_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Fine> findAll(Connection connection) throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM Fine";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                fines.add(mapResultSetToFine(rs));
            }
        }
        return fines;
    }

    @Override
    public Optional<Fine> findByBorrowId(String borrowId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Fine WHERE borrow_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, borrowId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFine(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updatePaidStatus(String fineId, Fine.FinePaidStatus paidStatus, Connection connection) throws SQLException {
        String sql = "UPDATE Fine SET paid = ? WHERE fine_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, paidStatus.name());
            ps.setString(2, fineId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Fine> findUnpaidFines(Connection connection) throws SQLException {
        List<Fine> unpaidFines = new ArrayList<>();
        String sql = "SELECT * FROM Fine WHERE paid = 'no'";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                unpaidFines.add(mapResultSetToFine(rs));
            }
        }
        return unpaidFines;
    }

    private Fine mapResultSetToFine(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setFineId(rs.getString("fine_id"));
        fine.setBorrowId(rs.getString("borrow_id"));
        fine.setAmount(rs.getDouble("amount"));
        fine.setPaid(Fine.FinePaidStatus.valueOf(rs.getString("paid")));
        return fine;
    }
}
