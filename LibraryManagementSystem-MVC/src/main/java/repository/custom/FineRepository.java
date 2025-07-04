package repository.custom;

import model.entity.Fine;
import repository.CRUDRepository;

public interface FineRepository extends CRUDRepository<Fine, Integer> {
    // Additional methods specific to FineRepository can be defined here
    // For example, methods to find fines by member ID, book ID, etc.
}
