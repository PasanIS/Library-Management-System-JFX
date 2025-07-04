package entity;


import lombok.*;

import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Staff {
    private String staffId;
    private String name;
    private String username;
    private String password;
    private StaffRole role;
    private String email;

    // Enum for Staff Role
    public enum StaffRole {
        admin,
        librarian
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(staffId, staff.staffId) &&
                Objects.equals(name, staff.name) &&
                Objects.equals(username, staff.username) &&
                Objects.equals(password, staff.password) &&
                role == staff.role &&
                Objects.equals(email, staff.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId, name, username, password, role, email);
    }
}
