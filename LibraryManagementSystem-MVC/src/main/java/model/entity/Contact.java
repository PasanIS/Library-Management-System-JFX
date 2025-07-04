package model.entity;

import lombok.*;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor @ToString
public class Contact {
    private String address;
    private String phone;
    private String email;
}
