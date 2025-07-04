package model.entity;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Category {
    private String categoryId;  // e.g., C001
    private String name;
}
