package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class ReturnBookFormController {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnReturnBook;

    @FXML
    private JFXComboBox<?> cboxSelectMember;

    @FXML
    private DatePicker datePickerReturnDate;

    @FXML
    private Label lblCurrentTime;

    @FXML
    private Label lblFine;

    @FXML
    private Label lblFinePreview;

    @FXML
    private Label lblIssuedBooks;

    @FXML
    private Label lblReturnDate;

    @FXML
    private Label lblSelectMember;

    @FXML
    private TableView<?> tblBookDetails;

    @FXML
    void btnBackOnAction(ActionEvent event) {

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {

    }

}
