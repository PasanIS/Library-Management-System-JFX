package repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T, ID> extends SuperRepository {
    boolean save(T entity) throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean delete(ID id) throws SQLException;
    Optional<T> findById(ID id) throws SQLException;
    List<T> findAll() throws SQLException;
}

