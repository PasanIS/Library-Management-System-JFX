package repository.custom;

import model.entity.Member;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.Optional;

public interface MemberRepository extends CRUDRepository<Member, String> {

    Optional<Member> searchMember(String keyword) throws SQLException;
    String generateNextMemberId() throws SQLException;

}
