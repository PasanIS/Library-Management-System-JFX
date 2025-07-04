package dto;


import entity.Staff;
import lombok.*;

import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StaffDTO {
    private String staffId;
    private String name;
    private String username;
    private String password;
    private Staff.StaffRole role;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffDTO staffDTO = (StaffDTO) o;
        return Objects.equals(staffId, staffDTO.staffId) &&
                Objects.equals(name, staffDTO.name) &&
                Objects.equals(username, staffDTO.username) &&
                Objects.equals(password, staffDTO.password) &&
                role == staffDTO.role &&
                Objects.equals(email, staffDTO.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId, name, username, password, role, email);
    }
}
