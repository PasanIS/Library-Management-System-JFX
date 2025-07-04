package util.mapper;

import model.dto.MemberDTO;
import model.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public class MemberMapper {

    public static MemberDTO toDto(Member member) {
        if (member == null) {
            return null;
        }
        MemberDTO dto = new MemberDTO();
        dto.setMemberId(member.getMemberId());
        dto.setFullName(member.getFullName());
        dto.setNic(member.getNic());
        dto.setRegisteredDate(member.getRegisteredDate());
        dto.setContact(member.getContact());
        dto.setEmail(member.getEmail());
        dto.setAddress(member.getAddress());
        return dto;
    }

    public static Member toEntity(MemberDTO dto) {
        if (dto == null) {
            return null;
        }
        Member entity = new Member();
        entity.setMemberId(dto.getMemberId());
        entity.setFullName(dto.getFullName());
        entity.setNic(dto.getNic());
        entity.setRegisteredDate(dto.getRegisteredDate());
        entity.setContact(dto.getContact());
        entity.setEmail(dto.getEmail());
        entity.setAddress(dto.getAddress());
        return entity;
    }

    public static List<MemberDTO> toDtoList(List<Member> members) {
        return members.stream()
                .map(MemberMapper::toDto)
                .collect(Collectors.toList());
    }
}
