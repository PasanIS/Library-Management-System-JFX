package service.custom.impl;

import dto.StaffDTO;
import entity.Staff;
import lombok.SneakyThrows;
import repository.RepositoryFactory;
import repository.custom.StaffRepository;
import service.custom.StaffService;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;

    public StaffServiceImpl() {
        this.staffRepository = RepositoryFactory.getStaffRepository();
    }

    private StaffDTO convertToDTO(Staff staff) {
        if (staff == null) return null;
        return new StaffDTO(
                staff.getStaffId(),
                staff.getName(),
                staff.getUsername(),
                staff.getPassword(),
                staff.getRole(),
                staff.getEmail()
        );
    }

    private Staff convertToEntity(StaffDTO dto) {
        if (dto == null) return null;
        return new Staff(
                dto.getStaffId(),
                dto.getName(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getRole(),
                dto.getEmail()
        );
    }

    @SneakyThrows
    @Override
    public boolean save(StaffDTO staffDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = staffRepository.save(convertToEntity(staffDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error saving staff: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public Optional<StaffDTO> findById(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return staffRepository.findById(id, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding staff by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean update(StaffDTO staffDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = staffRepository.update(convertToEntity(staffDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error updating staff: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean delete(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = staffRepository.delete(id, connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error deleting staff: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public List<StaffDTO> findAll() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return staffRepository.findAll(connection).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding all staff: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public Optional<StaffDTO> findByUsername(String username) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return staffRepository.findByUsername(username, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding staff by username: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public Optional<StaffDTO> authenticateStaff(String username, String password) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            Optional<Staff> staffOptional = staffRepository.findByUsername(username, connection);
            if (staffOptional.isPresent()) {
                Staff staff = staffOptional.get();
                if (staff.getPassword().equals(password)) {
                    return Optional.of(convertToDTO(staff));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            System.err.println("Error authenticating staff: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }
}
