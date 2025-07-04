package dto;


import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberDTO {
    private String memberId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String nic;
    private LocalDate registeredDate;
    private boolean isDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDTO memberDTO = (MemberDTO) o;
        return Objects.equals(memberId, memberDTO.memberId) &&
                Objects.equals(name, memberDTO.name) &&
                Objects.equals(email, memberDTO.email) &&
                Objects.equals(phone, memberDTO.phone) &&
                Objects.equals(address, memberDTO.address) &&
                Objects.equals(nic, memberDTO.nic) &&
                Objects.equals(registeredDate, memberDTO.registeredDate) &&
                isDeleted == memberDTO.isDeleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, name, email, phone, address, nic, registeredDate, isDeleted);
    }
}
