package model.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class FineDTO implements Serializable {
    private Integer fineId;
    private String borrowId;
    private double fineAmount;
    private LocalDate calculatedDate;
    private boolean paid;
    private LocalDate paidDate;
}
