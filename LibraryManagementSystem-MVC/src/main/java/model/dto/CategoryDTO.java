package model.dto;

import lombok.*;

import java.io.Serializable;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO implements Serializable {
    private String categoryId;
    private String name;
}
