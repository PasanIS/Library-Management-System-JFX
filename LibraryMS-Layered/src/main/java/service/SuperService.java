package service;

import java.util.List;
import java.util.Optional;

public interface SuperService<T, ID> {

    boolean save(T dto);

    Optional<T> findById(ID id);

    boolean update(T dto);

    boolean delete(ID id);

    List<T> findAll();
}
