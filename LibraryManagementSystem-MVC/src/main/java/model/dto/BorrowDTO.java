package model.dto;

import lombok.*;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BorrowDTO implements Serializable {
    private String borrowId;
    private String bookId;
    private String memberId;

    private LocalDateTime issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    private Double fineAmount;
    private Boolean finePaid;
    private Double fine;

    private String bookTitle;
    private String memberName;

    private MemberDTO memberDTO;
    private BookDTO bookDTO;

}
