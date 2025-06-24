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
    private String name;
    private Contact contact;
    private String nic;
    private LocalDate registeredDate;

    @Override
    public String toString() {
        return name;  // Show only member name in ComboBox
    }

}
