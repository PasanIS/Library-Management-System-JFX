package service.custom.impl;

import dto.MemberDTO;
import entity.Member;
import javafx.scene.control.Alert;
import lombok.SneakyThrows;
import repository.RepositoryFactory;
import repository.custom.MemberRepository;
import service.custom.MemberService;
import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl() {
        this.memberRepository = RepositoryFactory.getMemberRepository();
    }

    private MemberDTO convertToDTO(Member member) {
        if (member == null) return null;
        return new MemberDTO(
                member.getMemberId(),
                member.getName(),
                member.getEmail(),
                member.getPhone(),
                member.getAddress(),
                member.getNic(), // Convert NIC
                member.getRegisteredDate(),
                member.isDeleted()
        );
    }

    private Member convertToEntity(MemberDTO dto) {
        if (dto == null) return null;
        return new Member(
                dto.getMemberId(),
                dto.getName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getAddress(),
                dto.getNic(),
                dto.getRegisteredDate(),
                dto.isDeleted()
        );
    }

    @SneakyThrows
    @Override
    public boolean save(MemberDTO memberDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);

            if (memberRepository.findByEmail(memberDTO.getEmail(), connection).isPresent()) {
                System.err.println("Error saving member: Email already exists.");
                showAlert(Alert.AlertType.ERROR, "Duplicate Email", "This email is already registered to another member.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }
            if (memberRepository.findByNic(memberDTO.getNic(), connection).isPresent()) {
                System.err.println("Error saving member: NIC already exists.");
                showAlert(Alert.AlertType.ERROR, "Duplicate NIC", "This NIC is already registered to another member.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            boolean result = memberRepository.save(convertToEntity(memberDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error saving member: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @Override
    public Optional<MemberDTO> findById(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return memberRepository.findById(id, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding member by ID: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean update(MemberDTO memberDTO) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);

            Optional<Member> existingByEmail = memberRepository.findByEmail(memberDTO.getEmail(), connection);
            if (existingByEmail.isPresent() && !existingByEmail.get().getMemberId().equals(memberDTO.getMemberId())) {
                System.err.println("Error updating member: Email already exists for another member.");
                showAlert(Alert.AlertType.ERROR, "Duplicate Email", "This email is already registered to another member.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }
            Optional<Member> existingByNic = memberRepository.findByNic(memberDTO.getNic(), connection);
            if (existingByNic.isPresent() && !existingByNic.get().getMemberId().equals(memberDTO.getMemberId())) {
                System.err.println("Error updating member: NIC already exists for another member.");
                showAlert(Alert.AlertType.ERROR, "Duplicate NIC", "This NIC is already registered to another member.");
                DBConnection.rollbackTransaction(connection);
                return false;
            }

            boolean result = memberRepository.update(convertToEntity(memberDTO), connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean delete(String id) {
//        Connection connection = null;
//        try {
//            connection = DBConnection.getConnection();
//            DBConnection.beginTransaction(connection);
//            boolean result = memberRepository.delete(id, connection);
//            if (result) {
//                DBConnection.commitTransaction(connection);
//            } else {
//                DBConnection.rollbackTransaction(connection);
//            }
//            return result;
//        } catch (SQLException e) {
//            System.err.println("Error deleting member: " + e.getMessage());
//            DBConnection.rollbackTransaction(connection);
//            return false;
//        } finally {
//            DBConnection.finalizeTransaction(connection);
//        }
        throw new UnsupportedOperationException("Delete operation is not supported. Use markAsDeleted instead.");
    }

    @Override
    public List<MemberDTO> findAll() {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return memberRepository.findAll(connection).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error finding all members: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public Optional<MemberDTO> findByEmail(String email) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return memberRepository.findByEmail(email, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding member by email: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public Optional<MemberDTO> findByNic(String nic) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            return memberRepository.findByNic(nic, connection).map(this::convertToDTO);
        } catch (SQLException e) {
            System.err.println("Error finding member by NIC: " + e.getMessage());
            return Optional.empty();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @Override
    public List<MemberDTO> searchMembers(String query) {
        Connection connection = null;
        List<Member> members = new ArrayList<>();
        try {
            connection = DBConnection.getConnection();
            members = memberRepository.findAll(connection);
            String lowerCaseQuery = query.toLowerCase();
            return members.stream()
                    .filter(member -> member.getName().toLowerCase().contains(lowerCaseQuery) ||
                            member.getEmail().toLowerCase().contains(lowerCaseQuery) ||
                            member.getPhone().toLowerCase().contains(lowerCaseQuery) ||
                            (member.getNic() != null && member.getNic().toLowerCase().contains(lowerCaseQuery)) ||
                            member.getMemberId().toLowerCase().contains(lowerCaseQuery))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
            return List.of();
        } finally {
            DBConnection.closeConnection(connection);
        }
    }

    @SneakyThrows
    @Override
    public boolean markAsDeleted(String id) {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            DBConnection.beginTransaction(connection);
            boolean result = memberRepository.markAsDeleted(id, connection);
            if (result) {
                DBConnection.commitTransaction(connection);
            } else {
                DBConnection.rollbackTransaction(connection);
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Error marking member as deleted: " + e.getMessage());
            DBConnection.rollbackTransaction(connection);
            return false;
        } finally {
            DBConnection.finalizeTransaction(connection);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
