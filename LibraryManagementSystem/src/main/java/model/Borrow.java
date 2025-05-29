package model;

import lombok.*;

import java.time.LocalDate;

@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Borrow {

    private int borrowId;
    private int memberId;
    private int bookId;
    private LocalDate issueDate;

}
