package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dao.MemberDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Member;
import util.NavigationUtil;

import java.io.IOException;
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
    private Label lblContact;

    @FXML
    private Label lblMemberID;

    @FXML
    private Label lblNIC;

    @FXML
    private Label lblName;

    @FXML
    private JFXTextField txtContact;

    @FXML
    private JFXTextField txtMemberID;

    @FXML
    private JFXTextField txtNIC;

    @FXML
    private JFXTextField txtName;

    @FXML
    private TextField txtSearch;

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
        clearForm();
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();
        String nic = txtNIC.getText().trim();

        if (name.isEmpty() || nic.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name & NIC are required..!");
            return;
        }

        Member member = new Member(0, name, contact, nic);
        boolean registered = MemberDAO.addMember(member);

        if (registered) {
            showAlert(Alert.AlertType.INFORMATION, "Registered", "Member registered successfully...");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Member registration failed...");
        }
    }

    @FXML
    void btnRemoveOnAction(ActionEvent event) {
        try {
            int id = Integer.parseInt(txtMemberID.getText().trim());
            boolean removed = MemberDAO.deleteMember(id);

            if (removed) {
                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Member removed successfully.");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Member deletion failed.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID", "Please enter a valid member ID.");
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search", "Please enter Member Name or NIC to search...");
            return;
        }

        Optional<Member> member = MemberDAO.searchMember(keyword);

        if (member.isPresent()) {
            Member mbr = member.get();
            txtMemberID.setText(String.valueOf(mbr.getMemberId()));
            txtName.setText(mbr.getName());
            txtContact.setText(mbr.getContact());
            txtNIC.setText(mbr.getNic());
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Not Found", "No member found with that Name or NIC...");
        }
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        try {
            int id = Integer.parseInt(txtMemberID.getText().trim());
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();
            String nic = txtNIC.getText().trim();

            Member member = new Member(id, name, contact, nic);
            boolean updated = MemberDAO.updateMember(member);

            if (updated) {
                showAlert(Alert.AlertType.INFORMATION, "Updated", "Member updated successfully...");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Update failed...");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid ID", "Member ID must be a number...");
        }
    }

    @FXML
    void btnViewOnAction(ActionEvent event) {
//        ------------------to do----------
    }

    // ---------Clear Form-----------
    private void clearForm() {
        txtMemberID.clear();
        txtName.clear();
        txtContact.clear();
        txtNIC.clear();
        txtSearch.clear();
    }

    // ---------Show Alert-----------
    private void showAlert(Alert.AlertType type, String msg, String s) {
        Alert alert = new Alert(type);
        alert.setTitle("Library System");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
