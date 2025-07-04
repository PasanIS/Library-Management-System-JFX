package model.dto;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MemberDTO implements Serializable {
    private String memberId;
    private String fullName;
    private String contact;
    private String nic;
    private String email;
    private String address;
    private LocalDate registeredDate;

}