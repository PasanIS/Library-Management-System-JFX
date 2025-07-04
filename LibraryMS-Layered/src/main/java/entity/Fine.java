package entity;


import lombok.*;

import java.util.Objects;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Fine {
    private String fineId;
    private String borrowId;
    private Double amount;
    private FinePaidStatus paid; // Using an enum for paid status

    // Enum for Fine Paid Status
    public enum FinePaidStatus {
        yes,
        no
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fine fine = (Fine) o;
        return Objects.equals(fineId, fine.fineId) &&
                Objects.equals(borrowId, fine.borrowId) &&
                Objects.equals(amount, fine.amount) &&
                paid == fine.paid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fineId, borrowId, amount, paid);
    }
}
