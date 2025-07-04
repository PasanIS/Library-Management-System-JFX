package service.custom;

import model.dto.MemberDTO;
import service.SuperService;

import java.util.List;
import java.util.Optional;

public interface MemberService extends SuperService {

    boolean registerMember(MemberDTO memberDTO);
    boolean updateMember(MemberDTO memberDTO);
    boolean removeMember(String memberId);
    Optional<MemberDTO> getMemberById(String memberId);
    List<MemberDTO> getAllMembers();
    Optional<MemberDTO> searchMember(String keyword);
    String generateNextMemberId();

}
