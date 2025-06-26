package dao;

import model.Fine;
import model.*;
import util.DBConnection;

import java.sql.*;

public class FineDAO {
    public void addFine(Fine fine) throws SQLException {
        String sql = "INSERT INTO fines (borrow_id, fine_amount, calculated_date, paid, paid_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fine.getBorrowId());
            stmt.setDouble(2, fine.getFineAmount());
            stmt.setDate(3, Date.valueOf(fine.getCalculatedDate()));
            stmt.setBoolean(4, fine.isPaid());

            if (fine.getPaidDate() != null) {
                stmt.setDate(5, Date.valueOf(String.valueOf(fine.getPaidDate())));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.executeUpdate();
        }
    }
}
