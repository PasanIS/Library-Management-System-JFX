package model.dto;

import lombok.*;

import java.io.Serializable;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO implements Serializable {
    private String address;
    private String phone;
    private String email;
}
