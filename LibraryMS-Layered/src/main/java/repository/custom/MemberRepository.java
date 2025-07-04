package repository.custom;

import entity.Member;
import repository.SuperRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface MemberRepository extends SuperRepository<Member, String> {

    Optional<Member> findByEmail(String email, Connection connection) throws SQLException;

    boolean markAsDeleted(String id, Connection connection) throws SQLException;

    Optional<Member> findByNic(String nic, Connection connection) throws SQLException;
}
