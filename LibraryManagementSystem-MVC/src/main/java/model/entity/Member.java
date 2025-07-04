package model.entity;

import lombok.*;
import model.dto.MemberDTO;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member extends MemberDTO {

    private String memberId;
    private String fullName;
    private String contact;
    private String nic;
    private String email;
    private String address;
    private LocalDate registeredDate;

    public Member(String memberId, String fullName, String contact, String nic, LocalDate registeredDate) {
    }
}
