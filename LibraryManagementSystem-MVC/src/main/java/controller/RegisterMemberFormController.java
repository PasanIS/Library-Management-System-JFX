package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import util.NavigationUtil;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class RegisterMemberFormController {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnRegister;

    @FXML
    private JFXButton btnRemove;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXButton btnUpdate;

    @FXML
    private JFXButton btnView;

    @FXML
    private JFXTextField txtAddress;

    @FXML
    private JFXTextField txtContact;

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtMemberID;

    @FXML
    private JFXTextField txtNIC;

    @FXML
    private JFXTextField txtName;

    @FXML
    private TextField txtSearch;

    // -----------------------------------------------------
    // -----------------Database Connection Parameters------
    private final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "1100";
    // -----------------------------------------------------

    @FXML
    public void initialize() {
        generateNextMemberID();
    }

    private void generateNextMemberID() {
        String sql = "SELECT member_id FROM members ORDER BY member_id DESC LIMIT 1";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            if (resultSet.next()) {
                String lastId = resultSet.getString("member_id"); // e.g., M012
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                txtMemberID.setText(String.format("M%03d", num));
            } else {
                txtMemberID.setText("M001"); // no members yet
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to generate Member ID: " + e.getMessage());
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        try {
            NavigationUtil.loadScene("dashboard_view.fxml", btnBack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
        generateNextMemberID();
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {

        if (validateInput()) return;

        String memberId = txtMemberID.getText();
        String fullName = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String nic = txtNIC.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        String sqlMember = "INSERT INTO members (member_id, full_name, nic) VALUES (?, ?, ?)";
        String sqlContact = "INSERT INTO contacts (member_id, address, contact, email) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Start transaction
            connection.setAutoCommit(false);

            try (
                    PreparedStatement psMember = connection.prepareStatement(sqlMember);
                    PreparedStatement psContact = connection.prepareStatement(sqlContact)
            ) {
                // Insert into members table
                psMember.setString(1, memberId);
                psMember.setString(2, fullName);
                psMember.setString(3, nic.isEmpty() ? null : nic);
                psMember.executeUpdate();

                // Insert into contacts table
                psContact.setString(1, memberId);
                psContact.setString(2, address);
                psContact.setString(3, contact);
                psContact.setString(4, email);
                psContact.executeUpdate();

                // Commit transaction
                connection.commit();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Member registered successfully...!");
                clearFields();
                generateNextMemberID();

            } catch (SQLIntegrityConstraintViolationException ex) {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "Email or phone already exists...");
            } catch (SQLException e) {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to register member: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String memberId = txtMemberID.getText();
        if (memberId.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please load a member to remove.");
            return;
        }

        String deleteContactSQL = "DELETE FROM contacts WHERE member_id = ?";
        String deleteMemberSQL = "DELETE FROM members WHERE member_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false); // Begin transaction

            try (
                    PreparedStatement contactStmt = connection.prepareStatement(deleteContactSQL);
                    PreparedStatement memberStmt = connection.prepareStatement(deleteMemberSQL)
            ) {
                contactStmt.setString(1, memberId);
                contactStmt.executeUpdate();

                memberStmt.setString(1, memberId);
                int affected = memberStmt.executeUpdate();

                if (affected > 0) {
                    connection.commit();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Member removed successfully!");
                    clearFields();
                    generateNextMemberID();
                } else {
                    connection.rollback();
                    showAlert(Alert.AlertType.WARNING, "Not Found", "Member ID does not exist.");
                }

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to remove member: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Database connection failed: " + e.getMessage());
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter Member ID, Name, or NIC to search.");
            return;
        }

        String sql = "SELECT m.member_id, m.full_name, m.nic, m.registered_date, " +
                "c.contact, c.email, c.address " +
                "FROM members m LEFT JOIN contacts c ON m.member_id = c.member_id " +
                "WHERE m.member_id = ? OR m.full_name = ? OR m.nic = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, keyword);
            preparedStatement.setString(2, keyword);
            preparedStatement.setString(3, keyword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    txtMemberID.setText(resultSet.getString("member_id"));
                    txtName.setText(resultSet.getString("full_name"));
                    txtNIC.setText(Optional.ofNullable(resultSet.getString("nic")).orElse(""));
                    txtContact.setText(Optional.ofNullable(resultSet.getString("contact")).orElse(""));
                    txtEmail.setText(Optional.ofNullable(resultSet.getString("email")).orElse(""));
                    txtAddress.setText(Optional.ofNullable(resultSet.getString("address")).orElse(""));
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Not Found", "No matching member found.");
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to search member: " + e.getMessage());
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        if (validateInput()) return;

        String memberId = txtMemberID.getText();
        String fullName = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String nic = txtNIC.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        String sqlMember = "UPDATE members SET full_name = ?, nic = ? WHERE member_id = ?";
        String sqlContact = "UPDATE contacts SET contact = ?, email = ?, address = ? WHERE member_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            connection.setAutoCommit(false); // Begin transaction

            try (
                    PreparedStatement psMember = connection.prepareStatement(sqlMember);
                    PreparedStatement psContact = connection.prepareStatement(sqlContact)
            ) {
                // Update members table
                psMember.setString(1, fullName);
                psMember.setString(2, nic.isEmpty() ? null : nic);
                psMember.setString(3, memberId);
                psMember.executeUpdate();

                // Update contacts table
                psContact.setString(1, contact);
                psContact.setString(2, email);
                psContact.setString(3, address);
                psContact.setString(4, memberId);
                psContact.executeUpdate();

                connection.commit(); // Commit transaction
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member details updated successfully!");
                clearFields();
                generateNextMemberID();

            } catch (SQLIntegrityConstraintViolationException ex) {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Duplicate Entry", "NIC or Email already exists.");
            } catch (SQLException e) {
                connection.rollback();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update member: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Could not connect to DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnViewOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/viewAll_view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("All Registered Members");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load member view: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // -------------------Clear Fields------------
    private void clearFields() {
        txtMemberID.clear();
        txtName.clear();
        txtContact.clear();
        txtNIC.clear();
        txtEmail.clear();
        txtSearch.clear();
        txtAddress.clear();
    }

    // --------------------Show Alert-------------
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ---------------------Validate Inputs----------
    private boolean validateInput() {
        if (txtName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Full Name is required...");
            return true;
        }
        if (txtContact.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Phone is required...");
            return true;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Email is required...");
            return true;
        }
        if (!txtEmail.getText().contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Invalid email address...");
            return true;
        }
        return false;
    }
}
