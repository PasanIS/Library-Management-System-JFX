package repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SuperRepository<T, ID> {

    boolean save(T entity, Connection connection) throws SQLException;


    Optional<T> findById(ID id, Connection connection) throws SQLException;


    boolean update(T entity, Connection connection) throws SQLException;


    boolean delete(ID id, Connection connection) throws SQLException;


    List<T> findAll(Connection connection) throws SQLException;
}
