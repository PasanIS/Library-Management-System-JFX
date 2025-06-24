package model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Author {
    private String authorId;   // e.g., A001
    private String name;
}