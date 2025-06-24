package model;

import lombok.*;

import java.time.LocalDateTime;

@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Borrow {

    private String borrowId;

    private Member member;
    private Book book;

    private LocalDateTime issueDate;
    private LocalDateTime returnDate;

    private Double fine;

}
