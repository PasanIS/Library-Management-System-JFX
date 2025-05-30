package model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Member {

    private int memberId;
    private String name;
    private String contact;
    private String nic;

    @Override
    public String toString() {
        return name;  // Show only member name in ComboBox
    }

}
