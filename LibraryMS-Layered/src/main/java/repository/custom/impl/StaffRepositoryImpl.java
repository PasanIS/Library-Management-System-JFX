package repository.custom.impl;

import entity.Staff;
import repository.custom.StaffRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffRepositoryImpl implements StaffRepository {
    @Override
    public boolean save(Staff staff, Connection connection) throws SQLException {
        String sql = "INSERT INTO Staff (staff_id, name, username, password, role, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staff.getStaffId());
            ps.setString(2, staff.getName());
            ps.setString(3, staff.getUsername());
            ps.setString(4, staff.getPassword());
            ps.setString(5, staff.getRole().name()); // Store enum as string
            ps.setString(6, staff.getEmail());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Staff> findById(String id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE staff_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStaff(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Staff staff, Connection connection) throws SQLException {
        String sql = "UPDATE Staff SET name = ?, username = ?, password = ?, role = ?, email = ? WHERE staff_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, staff.getName());
            ps.setString(2, staff.getUsername());
            ps.setString(3, staff.getPassword());
            ps.setString(4, staff.getRole().name()); // Store enum as string
            ps.setString(5, staff.getEmail());
            ps.setString(6, staff.getStaffId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id, Connection connection) throws SQLException {
        String sql = "DELETE FROM Staff WHERE staff_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Staff> findAll(Connection connection) throws SQLException {
        List<Staff> staffMembers = new ArrayList<>();
        String sql = "SELECT * FROM Staff";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                staffMembers.add(mapResultSetToStaff(rs));
            }
        }
        return staffMembers;
    }

    @Override
    public Optional<Staff> findByUsername(String username, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Staff WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStaff(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffId(rs.getString("staff_id"));
        staff.setName(rs.getString("name"));
        staff.setUsername(rs.getString("username"));
        staff.setPassword(rs.getString("password")); // Remember to hash passwords in a real app
        staff.setRole(Staff.StaffRole.valueOf(rs.getString("role"))); // Convert string back to enum
        staff.setEmail(rs.getString("email"));
        return staff;
    }
}
