package dto;


import entity.Fine;
import lombok.*;

import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FineDTO {
    public static Fine.FinePaidStatus FinePaidStatus;
    private String fineId;
    private String borrowId;
    private String bookTitle; // Added for UI convenience
    private String memberName; // Added for UI convenience
    private Double amount;
    private Fine.FinePaidStatus paid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FineDTO fineDTO = (FineDTO) o;
        return Objects.equals(fineId, fineDTO.fineId) &&
                Objects.equals(borrowId, fineDTO.borrowId) &&
                Objects.equals(bookTitle, fineDTO.bookTitle) &&
                Objects.equals(memberName, fineDTO.memberName) &&
                Objects.equals(amount, fineDTO.amount) &&
                paid == fineDTO.paid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fineId, borrowId, bookTitle, memberName, amount, paid);
    }
}
