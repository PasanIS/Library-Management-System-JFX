package controller;

import com.jfoenix.controls.JFXButton;
import dao.MemberDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Member;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewAllMembersFormController {

    @FXML
    private JFXButton btnReload;

    @FXML
    private TableView<Member> tblMembers;
    @FXML
    private TableColumn<Member, String> colId;
    @FXML
    private TableColumn<Member, String> colName;
    @FXML
    private TableColumn<Member, String> colContact;
    @FXML
    private TableColumn<Member, String> colNIC;
    @FXML
    private TableColumn<Member, String> colEmail;
    @FXML
    private TableColumn<Member, String> colAddress;
    @FXML
    private TableColumn<Member, String> colDate;

    private final MemberDAO memberDAO = new MemberDAO();

    public void initialize() {
        // Initialize table columns and load data
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getMemberId()));
        colName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        colContact.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getContact()));
        colNIC.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNic()));
        colEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colAddress.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                data.getValue().getRegisteredDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        );

        loadMemberData();
    }

    private void loadMemberData() {
        try {
            List<Member> members = memberDAO.getAllMembers();
            ObservableList<Member> list = FXCollections.observableArrayList(members);
            tblMembers.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load members: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {
        loadMemberData();
    }

}
