package model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Publisher {
    private String publisherId;  // e.g., P001
    private String name;
}