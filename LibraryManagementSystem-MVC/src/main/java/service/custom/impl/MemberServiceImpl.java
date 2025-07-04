package service.custom.impl;

import model.dto.MemberDTO;
import model.entity.Member;
import repository.RepositoryFactory;
import repository.custom.MemberRepository;
import service.custom.MemberService;
import util.mapper.BookMapper;
import util.mapper.MemberMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository = (MemberRepository) RepositoryFactory
            .getInstance()
            .getRepository(RepositoryFactory.RepositoryType.MEMBER);

    @Override
    public boolean registerMember(MemberDTO memberDTO) {
        try {
            // Generate ID
            if (memberDTO.getMemberId() == null || memberDTO.getMemberId().isEmpty()) {
                memberDTO.setMemberId(memberRepository.generateNextMemberId());
            }

            Member member = MemberMapper.toEntity(memberDTO);
            return memberRepository.save(member);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateMember(MemberDTO memberDTO) {
        try {
            Member member = MemberMapper.toEntity(memberDTO);
            return memberRepository.update(member);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeMember(String memberId) {
        try {
            return memberRepository.delete(memberId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Optional<MemberDTO> getMemberById(String memberId) {
        try {
            return memberRepository.findById(memberId)
                    .map(MemberMapper::toDto);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        try {
            List<Member> members = memberRepository.findAll();
            return MemberMapper.toDtoList(members);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<MemberDTO> searchMember(String keyword) {
        try {
            return memberRepository.searchMember(keyword)
                    .map(MemberMapper::toDto);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public String generateNextMemberId() {
        try {
            return memberRepository.generateNextMemberId();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
