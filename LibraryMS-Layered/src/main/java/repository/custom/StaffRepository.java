package repository.custom;

import entity.Staff;
import repository.SuperRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public interface StaffRepository extends SuperRepository<Staff, String> {

    Optional<Staff> findByUsername(String username, Connection connection) throws SQLException;
}
