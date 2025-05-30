package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class IssueBookFormController {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnIssueBook;

    @FXML
    private JFXComboBox<?> cboxBook;

    @FXML
    private JFXComboBox<?> cboxCategory;

    @FXML
    private JFXComboBox<?> cboxSelectMember;

    @FXML
    private DatePicker datePickerIssueDate;

    @FXML
    private Label lblCurrentTime;

    @FXML
    private Label lblIssueDate;

    @FXML
    private Label lblSelectBook;

    @FXML
    private Label lblSelectMember;

    @FXML
    void btnBackOnAction(ActionEvent event) {
        // TODO document why this method is empty
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        // TODO document why this method is empty
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        // TODO document why this method is empty
    }

}
