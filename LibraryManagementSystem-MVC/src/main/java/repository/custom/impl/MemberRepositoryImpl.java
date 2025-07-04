package repository.custom.impl;

import model.entity.Member;
import repository.custom.MemberRepository;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRepositoryImpl implements MemberRepository {

    // ----------Optional(Helping) method to map a ResultSet row to a Member entity
    private Optional<Member> mapResultSetToMember(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setFullName(rs.getString("full_name"));
            member.setNic(rs.getString("nic"));
            member.setRegisteredDate(rs.getTimestamp("registered_date").toLocalDateTime().toLocalDate());

            // Optional contact info (from LEFT JOIN)
            member.setContact(rs.getString("contact"));
            member.setEmail(rs.getString("email"));
            member.setAddress(rs.getString("address"));
            return Optional.of(member);
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Member member) throws SQLException {
        String memberSql = "INSERT INTO members (member_id, full_name, nic, registered_date) VALUES (?, ?, ?, ?)";

        String contactSql = "INSERT INTO contacts (member_id, contact, email, address) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false); // ------Begin transaction

            try (PreparedStatement memberStatement = connection.prepareStatement(memberSql);
                 PreparedStatement contactStatement = connection.prepareStatement(contactSql)) {

                // Insert into members table
                memberStatement.setString(1, member.getMemberId());
                memberStatement.setString(2, member.getFullName());
                memberStatement.setString(3, member.getNic());
                memberStatement.setTimestamp(4, Timestamp.valueOf(member.getRegisteredDate().atStartOfDay()));
                memberStatement.executeUpdate();

                // Insert into contacts table
                contactStatement.setString(1, member.getMemberId());
                contactStatement.setString(2, member.getContact());
                contactStatement.setString(3, member.getEmail());
                contactStatement.setString(4, member.getAddress());
                contactStatement.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e; // -----Re-throw to be handled by service/controller for specific alerts
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean update(Member member) throws SQLException {
        String memberSql = "UPDATE members SET full_name = ?, nic = ?, registered_date = ? WHERE member_id = ?";
        String contactSql = "UPDATE contacts SET contact = ?, email = ?, address = ? WHERE member_id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false); // -------Begin transaction

            try (PreparedStatement memberStmt = connection.prepareStatement(memberSql);
                 PreparedStatement contactStmt = connection.prepareStatement(contactSql)) {

                // Update members table
                memberStmt.setString(1, member.getFullName());
                memberStmt.setString(2, member.getNic());
                memberStmt.setTimestamp(3, Timestamp.valueOf(member.getRegisteredDate().atStartOfDay()));
                memberStmt.setString(4, member.getMemberId());
                memberStmt.executeUpdate();

                // Update contacts table
                contactStmt.setString(1, member.getContact());
                contactStmt.setString(2, member.getEmail());
                contactStmt.setString(3, member.getAddress());
                contactStmt.setString(4, member.getMemberId());
                contactStmt.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e; // -----Re-throw to be handled by service/controller
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public boolean delete(String memberId) throws SQLException {
        String deleteContactSql = "DELETE FROM contacts WHERE member_id = ?";
        String deleteMemberSql = "DELETE FROM members WHERE member_id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false); // --------Begin transaction

            try (PreparedStatement contactStmt = connection.prepareStatement(deleteContactSql);
                 PreparedStatement memberStmt = connection.prepareStatement(deleteMemberSql)) {

                // Delete from contacts first due to foreign key constraints
                contactStmt.setString(1, memberId);
                contactStmt.executeUpdate();

                // Delete from members
                memberStmt.setString(1, memberId);
                int affectedRows = memberStmt.executeUpdate();

                if (affectedRows > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback(); // -----Member not found in members table
                    return false;
                }

            } catch (SQLException e) {
                connection.rollback();
                throw e; // -----Re-throw to be handled by service/controller
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    @Override
    public Optional<Member> findById(String memberId) throws SQLException {
        String sql = "SELECT m.member_id, m.full_name, m.nic, m.registered_date, " +
                "c.contact, c.email, c.address " +
                "FROM members m LEFT JOIN contacts c ON m.member_id = c.member_id " +
                "WHERE m.member_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return mapResultSetToMember(resultSet);
            }
        }
    }

    @Override
    public List<Member> findAll() throws SQLException {
        List<Member> members = new ArrayList<>();
        String query = "SELECT m.member_id, m.full_name, m.nic, m.registered_date, " +
                "c.contact, c.email, c.address " +
                "FROM members m LEFT JOIN contacts c ON m.member_id = c.member_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Manually map to Member entity
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setFullName(resultSet.getString("full_name"));
                member.setNic(resultSet.getString("nic"));
                member.setRegisteredDate(resultSet.getTimestamp("registered_date").toLocalDateTime().toLocalDate());
                member.setContact(resultSet.getString("contact"));
                member.setEmail(resultSet.getString("email"));
                member.setAddress(resultSet.getString("address"));
                members.add(member);
            }
        }
        return members;
    }

    @Override
    public Optional<Member> searchMember(String keyword) throws SQLException {
        String sql = "SELECT m.member_id, m.full_name, m.nic, m.registered_date, " +
                "c.contact, c.email, c.address " +
                "FROM members m " +
                "LEFT JOIN contacts c ON m.member_id = c.member_id " +
                "WHERE m.member_id = ? OR m.full_name = ? OR m.nic = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);

            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSetToMember(rs);
            }
        }
    }

    @Override
    public String generateNextMemberId() throws SQLException {
        String sql = "SELECT member_id FROM members ORDER BY member_id DESC LIMIT 1";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                String lastId = resultSet.getString("member_id"); // --- M012
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("M%03d", num);
            } else {
                return "M001"; // ---Default
            }
        }
    }
}
