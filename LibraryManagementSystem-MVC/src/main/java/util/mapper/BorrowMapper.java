package util.mapper;

import model.dto.BookDTO;
import model.dto.BorrowDTO;
import model.dto.MemberDTO;
import model.entity.Book;
import model.entity.Borrow;
import model.entity.Member;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BorrowMapper {

    // --- DTO to Entity ---
    public static Borrow toEntity(BorrowDTO dto) {
        if (dto == null) {
            return null;
        }

        Borrow entity = new Borrow();
        entity.setBorrowId(dto.getBorrowId());
        entity.setIssueDate(dto.getIssueDate());

        entity.setReturnDate(dto.getReturnDate() != null ? dto.getReturnDate().atStartOfDay() : null);
        entity.setFineAmount(dto.getFine());

        entity.setBookId(dto.getBookId());
        entity.setMemberId(dto.getMemberId());

        if (dto.getBookDTO() != null) {
            entity.setBook(BookMapper.toEntity(dto.getBookDTO()));
        } else if (dto.getBookId() != null) {
            entity.setBook(new Book(dto.getBookId(), null, null, null, null, null, 0, 0)); // Minimal constructor
        }

        if (dto.getMemberDTO() != null) {
            entity.setMember(MemberMapper.toEntity(dto.getMemberDTO()));
        } else if (dto.getMemberId() != null) {
            entity.setMember(new Member(dto.getMemberId(), null, null, null, null, null, null)); // Minimal constructor
        }


        return entity;
    }

    // --- Entity to DTO ---
    public static BorrowDTO toDto(Borrow entity) {
        if (entity == null) {
            return null;
        }

        BorrowDTO dto = new BorrowDTO();
        dto.setBorrowId(entity.getBorrowId());
        dto.setIssueDate(entity.getIssueDate());

        dto.setReturnDate(entity.getReturnDate() != null ? entity.getReturnDate().toLocalDate() : null);
        dto.setFine(entity.getFineAmount());

        dto.setBookId(entity.getBookId());
        dto.setMemberId(entity.getMemberId());


        if (entity.getBook() != null) {
            dto.setBookDTO(BookMapper.toDto(entity.getBook()));
            dto.setBookTitle(entity.getBook().getTitle());
        } else {
            dto.setBookTitle(null);
        }

        if (entity.getMember() != null) {
            dto.setMemberDTO(MemberMapper.toDto(entity.getMember()));
            dto.setMemberName(entity.getMember().getFullName());
        } else {
            dto.setMemberName(null);
        }

        if (entity.getIssueDate() != null) {
            dto.setDueDate(entity.getIssueDate().toLocalDate().plusDays(14));
        }

        return dto;
    }

    public static List<BorrowDTO> toDtoList(List<Borrow> entities) {
        return entities.stream()
                .map(BorrowMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<Borrow> toEntityList(List<BorrowDTO> dtos) {
        return dtos.stream()
                .map(BorrowMapper::toEntity)
                .collect(Collectors.toList());
    }
}
