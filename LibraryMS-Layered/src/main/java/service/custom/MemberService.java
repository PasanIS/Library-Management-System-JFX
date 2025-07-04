package service.custom;

import dto.MemberDTO;
import service.SuperService;

import java.util.List;
import java.util.Optional;

public interface MemberService extends SuperService<MemberDTO, String> {

    Optional<MemberDTO> findByEmail(String email);

    List<MemberDTO> searchMembers(String query);

    boolean markAsDeleted(String id);

    Optional<MemberDTO> findByNic(String nic);
}
