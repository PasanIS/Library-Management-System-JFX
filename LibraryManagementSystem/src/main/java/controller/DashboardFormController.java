package controller;

import com.jfoenix.controls.JFXButton;
import dao.BookDAO;
import dao.BorrowDAO;
import dao.MemberDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import util.NavigationUtil;

import java.io.IOException;

public class DashboardFormController {

    @FXML
    private JFXButton btnAdd;

    @FXML
    private JFXButton btnIssue;

    @FXML
    private JFXButton btnRegister;

    @FXML
    private JFXButton btnReturn;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private Label lblDashboard;

    @FXML
    void btnAddOnAction(ActionEvent event) {

        try {
            NavigationUtil.loadScene("addBook_view.fxml", btnAdd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnIssueOnAction(ActionEvent event) {

        try {
            NavigationUtil.loadScene("issueBook_view.fxml", btnIssue);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        // TODO document why this method is empty
    }

    @FXML
    void btnReturnOnAction(ActionEvent event) {

        try {
            NavigationUtil.loadScene("returnBook_view.fxml", btnReturn);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        // TODO document why this method is empty
    }

}
