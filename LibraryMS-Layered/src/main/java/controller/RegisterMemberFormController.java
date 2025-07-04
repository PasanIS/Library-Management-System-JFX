package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dto.MemberDTO;
import service.custom.MemberService;
import service.ServiceFactory;
import util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class RegisterMemberFormController implements Initializable {

    @FXML private JFXButton btnBack;
    @FXML private JFXButton btnClear;
    @FXML private JFXButton btnRegister;
    @FXML private JFXButton btnRemove;
    @FXML private JFXButton btnSearch;
    @FXML private JFXButton btnUpdate;
    @FXML private JFXButton btnView;
    @FXML private JFXTextField txtAddress;
    @FXML private JFXTextField txtContact;
    @FXML private JFXTextField txtEmail;
    @FXML private JFXTextField txtMemberID;
    @FXML private JFXTextField txtNIC;
    @FXML private JFXTextField txtName;
    @FXML private TextField txtSearch;

    private MemberService memberService;
    private static final String AUTO_GENERATED_PLACEHOLDER = "Auto Generated";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        memberService = ServiceFactory.getMemberService();
        generateNewMemberId();
    }

    private void generateNewMemberId() {
        txtMemberID.setText("M" + UUID.randomUUID().toString().substring(0, 7).toUpperCase());
        txtMemberID.setEditable(false);
    }

    private void populateForm(MemberDTO member) {
        if (member != null) {
            txtMemberID.setText(member.getMemberId());
            txtName.setText(member.getName());
            txtContact.setText(member.getPhone());
            txtEmail.setText(member.getEmail());
            txtAddress.setText(member.getAddress());
            txtNIC.setText(member.getNic());
            txtMemberID.setEditable(false);
        }
    }

    private void clearForm() {
        txtName.clear();
        txtContact.clear();
        txtEmail.clear();
        txtAddress.clear();
        txtNIC.clear();
        txtSearch.clear();
        generateNewMemberId();
    }

    private boolean validateInput() {
        if (txtName.getText().trim().isEmpty() ||
                txtContact.getText().trim().isEmpty() ||
                txtEmail.getText().trim().isEmpty() ||
                txtAddress.getText().trim().isEmpty() ||
                txtNIC.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields (Name, Contact, Email, Address, NIC) must be filled out.");
            return false;
        }

        // Email validation
        if (!txtEmail.getText().trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid email address.");
            return false;
        }

        // Phone number validation (e.g., 10 to 15 digits)
        if (!txtContact.getText().trim().matches("^\\d{10,15}$")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid phone number (10-15 digits).");
            return false;
        }

        // NIC validation (e.g., 9 digits with V/X or 12 digits)
        if (!txtNIC.getText().trim().matches("^[0-9]{9}[vVxX]$|^[0-9]{12}$")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid NIC (e.g., 9 digits with optional 'V' or 'X', or 12 digits).");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- FXML Action Event Handlers ---

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        NavigationUtil.navigateTo(stage, "/view/dashboard_view.fxml", "Library Management System - Dashboard");
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        if (txtMemberID.getText().equals(AUTO_GENERATED_PLACEHOLDER)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Member ID is not properly generated. Please try clearing the form.");
            return;
        }

        MemberDTO memberDTO = new MemberDTO(
                txtMemberID.getText(),
                txtName.getText(),
                txtEmail.getText(),
                txtContact.getText(),
                txtAddress.getText(),
                txtNIC.getText(),
                LocalDate.now(),
                false
        );

        if (memberService.save(memberDTO)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member registered successfully!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register member. This might be due to a duplicate Email/NIC or a database error.");
        }
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        String memberIdToDelete = txtMemberID.getText();

        // --- REVISED CHECK FOR REMOVAL ---
        if (memberIdToDelete == null || memberIdToDelete.trim().isEmpty() || memberIdToDelete.equals(AUTO_GENERATED_PLACEHOLDER)) {
            showAlert(Alert.AlertType.WARNING, "No Member Selected", "Please search for a member first to remove them.");
            return;
        }
        // --- END REVISED CHECK ---

        Optional<MemberDTO> memberOptional = memberService.findById(memberIdToDelete);
        if (memberOptional.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Member Not Found", "No active member found with this ID to remove.");
            return;
        }
        MemberDTO memberToRemove = memberOptional.get();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Mark Member as Removed: " + memberToRemove.getName() + " (" + memberToRemove.getMemberId() + ")");
        confirmAlert.setContentText("Are you sure you want to mark this member as removed from active use? Their historical borrowing data will be preserved.");

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (memberService.markAsDeleted(memberIdToDelete)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member successfully marked as removed (soft deleted).");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to mark member as removed. Check logs for details.");
            }
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Input Empty", "Please enter a Member ID, Email, or NIC to search.");
            return;
        }

        Optional<MemberDTO> foundMember = Optional.empty();

        foundMember = memberService.findById(query);

        if (foundMember.isEmpty()) {
            foundMember = memberService.findByEmail(query);
        }

        if (foundMember.isEmpty()) {
            foundMember = memberService.findByNic(query);
        }

        if (foundMember.isEmpty()) {
            List<MemberDTO> searchResults = memberService.searchMembers(query);
            if (!searchResults.isEmpty()) {
                foundMember = Optional.of(searchResults.get(0));
                if (searchResults.size() > 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Multiple Results",
                            "Multiple active members found. Displaying the first match. Please refine your search if needed.");
                }
            }
        }

        if (foundMember.isPresent()) {
            populateForm(foundMember.get());
            showAlert(Alert.AlertType.INFORMATION, "Member Found", "Member details loaded.");
            txtMemberID.setEditable(false);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Member Not Found", "No active member found matching the search query.");
            clearForm();
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String memberIdToUpdate = txtMemberID.getText();

        // --- REVISED CHECK FOR UPDATE ---
        if (memberIdToUpdate == null || memberIdToUpdate.trim().isEmpty() || memberIdToUpdate.equals(AUTO_GENERATED_PLACEHOLDER)) {
            showAlert(Alert.AlertType.WARNING, "No Member Selected", "Please search for a member first to update their details.");
            return;
        }
        // --- END REVISED CHECK ---

        if (!validateInput()) {
            return;
        }

        Optional<MemberDTO> existingMember = memberService.findById(memberIdToUpdate);
        if (existingMember.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Member not found for update. It might have been removed or an invalid ID.");
            clearForm();
            return;
        }

        MemberDTO originalMember = existingMember.get();

        MemberDTO memberDTO = new MemberDTO(
                memberIdToUpdate,
                txtName.getText(),
                txtEmail.getText(),
                txtContact.getText(),
                txtAddress.getText(),
                txtNIC.getText(),
                originalMember.getRegisteredDate(),
                originalMember.isDeleted()
        );

        if (memberService.update(memberDTO)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member details updated successfully!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update member details. Check logs for details.");
        }
    }

    @FXML
    void btnViewOnAction(ActionEvent event) {
        Stage stage = (Stage) btnView.getScene().getWindow();
        NavigationUtil.navigateTo(stage, "/view/viewAll_view.fxml", "Library Management System - All Members");
    }
}
