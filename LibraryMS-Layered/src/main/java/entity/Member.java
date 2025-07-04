package entity;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Member {
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
        Member member = (Member) o;
        return Objects.equals(memberId, member.memberId) &&
                Objects.equals(name, member.name) &&
                Objects.equals(email, member.email) &&
                Objects.equals(phone, member.phone) &&
                Objects.equals(address, member.address) &&
                Objects.equals(nic, member.nic) &&
                Objects.equals(registeredDate, member.registeredDate) &&
                isDeleted == member.isDeleted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, name, email, phone, address, nic, registeredDate, isDeleted);
    }
}
