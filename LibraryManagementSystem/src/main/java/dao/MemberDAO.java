package dao;

import model.Member;
import util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
                        resultSet.getInt("member_id"),
                        resultSet.getString("name"),
                        resultSet.getString("contact"),
                        resultSet.getString("nic")
                );
                members.add(member);
            }
        }
        return members;
    }
}
