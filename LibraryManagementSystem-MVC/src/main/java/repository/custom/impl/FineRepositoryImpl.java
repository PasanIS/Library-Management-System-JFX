package repository.custom.impl;

import model.entity.Fine;
import repository.custom.FineRepository;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FineRepositoryImpl implements FineRepository {

    // ----------Optional(Helping) method to map a ResultSet row to a Fine entity
    private Fine mapResultSetToFine(ResultSet resultSet) throws SQLException {
        Fine fine = new Fine();
        fine.setFineId(resultSet.getInt("fine_id"));
        fine.setBorrowId(resultSet.getString("borrow_id"));
        fine.setFineAmount(resultSet.getDouble("fine_amount"));
        fine.setCalculatedDate(resultSet.getDate("calculated_date").toLocalDate());
        fine.setPaid(resultSet.getBoolean("paid"));
        Date paidDateSql = resultSet.getDate("paid_date");
        fine.setPaidDate(paidDateSql != null ? paidDateSql.toLocalDate() : null);
        return fine;
    }

    @Override
    public boolean save(Fine fine) throws SQLException {
        String sql = "INSERT INTO fines (borrow_id, fine_amount, calculated_date, paid, paid_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // -----To get generated fine_id

            stmt.setString(1, fine.getBorrowId());
            stmt.setDouble(2, fine.getFineAmount());
            stmt.setDate(3, Date.valueOf(fine.getCalculatedDate()));
            stmt.setBoolean(4, fine.isPaid());

            if (fine.getPaidDate() != null) {
                stmt.setDate(5, Date.valueOf(fine.getPaidDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        fine.setFineId(generatedKeys.getInt(1)); // -----Set the auto-generated ID back to the entity
                    }
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean update(Fine fine) throws SQLException {
        String sql = "UPDATE fines SET borrow_id = ?, fine_amount = ?, calculated_date = ?, paid = ?, paid_date = ? WHERE fine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fine.getBorrowId());
            stmt.setDouble(2, fine.getFineAmount());
            stmt.setDate(3, Date.valueOf(fine.getCalculatedDate()));
            stmt.setBoolean(4, fine.isPaid());

            if (fine.getPaidDate() != null) {
                stmt.setDate(5, Date.valueOf(fine.getPaidDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setInt(6, fine.getFineId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer fineId) throws SQLException {
        String sql = "DELETE FROM fines WHERE fine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Fine> findById(Integer fineId) throws SQLException {
        String sql = "SELECT * FROM fines WHERE fine_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFine(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Fine> findAll() throws SQLException {
        List<Fine> fines = new ArrayList<>();
        String sql = "SELECT * FROM fines";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                fines.add(mapResultSetToFine(rs));
            }
        }
        return fines;
    }
}
