package service.custom;

import dto.FineDTO;
import service.SuperService;

import java.util.List;
import java.util.Optional;

public interface FineService extends SuperService<FineDTO, String> {

    Double calculateFineAmount(java.time.LocalDate dueDate, java.time.LocalDate returnDate);

    boolean createFine(FineDTO fineDTO);

    boolean payFine(String fineId);

    Optional<FineDTO> findFineByBorrowId(String borrowId);

    List<FineDTO> findUnpaidFines();
}
