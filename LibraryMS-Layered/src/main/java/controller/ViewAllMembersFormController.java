package controller;


import com.jfoenix.controls.JFXButton;
import dto.MemberDTO;
import service.custom.MemberService;
import service.ServiceFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ViewAllMembersFormController implements Initializable {

    @FXML
    private JFXButton btnReload;

    @FXML
    private TableColumn<MemberDTO, String> colAddress;

    @FXML
    private TableColumn<MemberDTO, String> colContact;

    @FXML
    private TableColumn<MemberDTO, LocalDate> colDate;

    @FXML
    private TableColumn<MemberDTO, String> colEmail;

    @FXML
    private TableColumn<MemberDTO, String> colId;

    @FXML
    private TableColumn<MemberDTO, String> colNIC;

    @FXML
    private TableColumn<MemberDTO, String> colName;

    @FXML
    private TableView<MemberDTO> tblMembers;

    private MemberService memberService;
    private ObservableList<MemberDTO> memberList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the MemberService
        memberService = ServiceFactory.getMemberService();

        // Initialize the ObservableList for TableView
        memberList = FXCollections.observableArrayList();
        tblMembers.setItems(memberList);

        // Set up cell value factories for TableView columns
        colId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colNIC.setCellValueFactory(new PropertyValueFactory<>("nic"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("registeredDate"));

        loadMembers();
    }

    private void loadMembers() {
        memberList.clear(); // Clear existing data
        List<MemberDTO> members = memberService.findAll();
        if (members != null && !members.isEmpty()) {
            memberList.addAll(members);
        } else {
            showAlert("No Members Found", "There are no registered members in the system.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- FXML Action Event Handlers ---

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadMembers();
        showAlert("Data Reloaded", "Member list has been refreshed.");
    }
}
