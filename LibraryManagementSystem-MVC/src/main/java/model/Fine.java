package model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Fine {
    private Integer fineId;
    private String borrowId;
    private double fineAmount;
    private LocalDate calculatedDate;
    private boolean paid;
    private LocalDate paidDate;
}
