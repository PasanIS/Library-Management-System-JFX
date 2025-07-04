package service.custom;

import dto.StaffDTO;
import service.SuperService;

import java.util.Optional;

public interface StaffService extends SuperService<StaffDTO, String> {

    Optional<StaffDTO> findByUsername(String username);

    Optional<StaffDTO> authenticateStaff(String username, String password);
}
