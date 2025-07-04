package repository.custom.impl;

import entity.Member;
import repository.custom.MemberRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberRepositoryImpl implements MemberRepository {
    @Override
    public boolean save(Member member, Connection connection) throws SQLException {
        String sql = "INSERT INTO Member (member_id, name, email, phone, address, nic, registered_date, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, member.getMemberId());
            ps.setString(2, member.getName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPhone());
            ps.setString(5, member.getAddress());
            ps.setString(6, member.getNic());
            ps.setDate(7, Date.valueOf(member.getRegisteredDate()));
            ps.setBoolean(8, member.isDeleted());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Member> findById(String id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Member WHERE member_id = ? AND is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Member member, Connection connection) throws SQLException {
        String sql = "UPDATE Member SET name = ?, email = ?, phone = ?, address = ?, nic = ?, registered_date = ? WHERE member_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());
            ps.setString(5, member.getNic());
            ps.setDate(6, Date.valueOf(member.getRegisteredDate()));
            ps.setString(7, member.getMemberId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id, Connection connection) throws SQLException {
//        String sql = "DELETE FROM Member WHERE member_id = ?";
//        try (PreparedStatement ps = connection.prepareStatement(sql)) {
//            ps.setString(1, id);
//            return ps.executeUpdate() > 0;
//        }
        throw new UnsupportedOperationException("Delete operation is not supported. Use markAsDeleted instead.");
    }

    @Override
    public List<Member> findAll(Connection connection) throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Member WHERE is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        }
        return members;
    }

    @Override
    public Optional<Member> findByEmail(String email, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Member WHERE email = ? AND is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean markAsDeleted(String id, Connection connection) throws SQLException {
        String sql = "UPDATE Member SET is_deleted = TRUE WHERE member_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Member> findByNic(String nic, Connection connection) throws SQLException {
        String sql = "SELECT * FROM Member WHERE nic = ? AND is_deleted = FALSE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nic);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setMemberId(rs.getString("member_id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setAddress(rs.getString("address"));
        member.setNic(rs.getString("nic"));
        member.setRegisteredDate(rs.getDate("registered_date").toLocalDate());
        member.setDeleted(rs.getBoolean("is_deleted"));
        return member;
    }
}
