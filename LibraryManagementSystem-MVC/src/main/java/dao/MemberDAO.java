package dao;

import model.Member;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAO {

    // -------Get All Members-----------
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();

        String query = "SELECT m.member_id, m.full_name, m.nic, m.registered_date, " +
                "c.contact, c.email, c.address " +
                "FROM members m LEFT JOIN contacts c ON m.member_id = c.member_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Member member = new Member();
                member.setMemberId(resultSet.getString("member_id"));
                member.setFullName(resultSet.getString("full_name"));
                member.setNic(resultSet.getString("nic"));
                member.setRegisteredDate(resultSet.getTimestamp("registered_date").toLocalDateTime().toLocalDate());

                // Optional contact info
                member.setContact(resultSet.getString("contact"));
                member.setEmail(resultSet.getString("email"));
                member.setAddress(resultSet.getString("address"));

                members.add(member);
            }
        }
        return members;
    }

    // ---------Add Member-----------
    public static boolean addMember(Member member) {
        String memberSql = "INSERT INTO members (member_id, full_name, nic, registered_date) VALUES (?, ?, ?, ?)";
        String contactSql = "INSERT INTO contacts (member_id, contact) VALUES (?, ?)";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement memberStatement = connection.prepareStatement(memberSql);
                 PreparedStatement contactStatement = connection.prepareStatement(contactSql)) {

                memberStatement.setString(1, member.getMemberId());
                memberStatement.setString(2, member.getFullName());
                memberStatement.setString(3, member.getNic());
                memberStatement.setTimestamp(4, Timestamp.valueOf(member.getRegisteredDate().atStartOfDay()));
                memberStatement.executeUpdate();

                contactStatement.setString(1, member.getMemberId());
                contactStatement.setString(2, member.getContact());
                contactStatement.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------Search Member-----------
    public static Optional<Member> searchMember(String keyword) {
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

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Member member = new Member(
                        rs.getString("member_id"),
                        rs.getString("full_name"),
                        rs.getString("contact"), // Make sure your Member class has a 'contact' field
                        rs.getString("nic"),
                        rs.getTimestamp("registered_date").toLocalDateTime().toLocalDate()
                );

                // If needed, you can also set email and address like this:
                // member.setEmail(rs.getString("email"));
                // member.setAddress(rs.getString("address"));

                return Optional.of(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    // --------------Update Member--------------
    public static boolean updateMember(Member member) {
        String memberSql = "UPDATE members SET full_name = ?, nic = ?, registered_date = ? WHERE member_id = ?";
        String contactSql = "UPDATE contacts SET contact = ? WHERE member_id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement memberStmt = connection.prepareStatement(memberSql);
                 PreparedStatement contactStmt = connection.prepareStatement(contactSql)) {

                memberStmt.setString(1, member.getFullName());
                memberStmt.setString(2, member.getNic());
                memberStmt.setTimestamp(3, Timestamp.valueOf(member.getRegisteredDate().atStartOfDay()));
                memberStmt.setString(4, member.getMemberId());
                memberStmt.executeUpdate();

                contactStmt.setString(1, member.getContact());
                contactStmt.setString(2, member.getMemberId());
                contactStmt.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------Remove Member----------------
    public static boolean deleteMember(String memberId) {
        String deleteContactSql = "DELETE FROM contacts WHERE member_id = ?";
        String deleteMemberSql = "DELETE FROM members WHERE member_id = ?";

        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement contactStmt = connection.prepareStatement(deleteContactSql);
                 PreparedStatement memberStmt = connection.prepareStatement(deleteMemberSql)) {

                contactStmt.setString(1, memberId);
                contactStmt.executeUpdate();

                memberStmt.setString(1, memberId);
                memberStmt.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
