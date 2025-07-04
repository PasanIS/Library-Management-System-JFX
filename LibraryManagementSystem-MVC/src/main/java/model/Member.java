package model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {

    private String memberId;
    private String fullName;
    private String contact;
    private String nic;
    private String email;
    private String address;
    private LocalDate registeredDate;

    public Member(String memberId, String fullName, String contact, String nic, LocalDate registeredDate) {
    }


    @Override
    public String toString() {
        return fullName;  // Show only member name in ComboBox
    }

}
