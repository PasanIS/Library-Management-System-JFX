package model.dto;

import lombok.*;

import java.io.Serializable;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class PublisherDTO implements Serializable {
    private String publisherId;
    private String name;
}
