package dao;

import model.Member;
import util.DBConnection;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberDAO {

    // -------Get All Members-----------
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();

        String query = "SELECT * FROM members";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                Member member = new Member(
                        resultSet.getString("member_id"),
                        resultSet.getString("name"),
                        resultSet.getString("contact"),
                        resultSet.getString("nic")
                );
                members.add(member);
            }
        }
        return members;
    }

    // ---------Add Member-----------
    public static boolean addMember(Member member) {
        String sql = "INSERT INTO members (name, contact, nic) VALUES (?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, member.getName());
            preparedStatement.setString(2, member.getContact());
            preparedStatement.setString(3, member.getNic());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ---------Search Member-----------
    public static Optional<Member> searchMember(String keyword) {
        String sql = "SELECT * FROM members WHERE name=? OR nic=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, keyword);
            preparedStatement.setString(2, keyword);
            ResultSet resultSet =preparedStatement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(new Member(
                        resultSet.getString("member_id"),
                        resultSet.getString("name"),
                        resultSet.getString("contact"),
                        resultSet.getString("nic")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // --------------Update Member--------------
    public static boolean updateMember(Member member) {
        String sql = "UPDATE members SET name=?, contact=?, nic=? WHERE name=? OR nic=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, member.getName());
            preparedStatement.setString(2, member.getContact());
            preparedStatement.setString(3, member.getNic());
            preparedStatement.setString(4, member.getMemberId());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // -------------Remove Member----------------
    public static boolean deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE name=? OR nic=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, memberId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
