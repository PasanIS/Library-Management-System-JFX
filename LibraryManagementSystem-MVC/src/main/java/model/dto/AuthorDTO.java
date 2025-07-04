package model.dto;

import lombok.*;

import java.io.Serializable;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO implements Serializable {
    private String authorId;
    private String name;
}
