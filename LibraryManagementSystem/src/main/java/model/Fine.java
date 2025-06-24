package model;

import lombok.*;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Fine {
    private String fineId;
    private Borrow borrow;
    private double fineAmount;
    private boolean paid;
    private LocalDateTime paidDate;
}
