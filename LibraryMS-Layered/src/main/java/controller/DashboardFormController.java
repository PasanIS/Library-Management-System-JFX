package controller;

import com.jfoenix.controls.JFXButton;
import javafx.stage.Stage;
import util.NavigationUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


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
            Stage stage = (Stage) btnAdd.getScene().getWindow();
            // ENSURE THIS PATH IS EXACTLY "/view/addBook_view.fxml"
            NavigationUtil.navigateTo(stage, "/view/addBook_view.fxml", "Library Management System - Add Book");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not open Add Book Form");
            alert.setContentText("An error occurred while trying to open the Add Book form. Please check the logs.");
            alert.showAndWait();
        }
    }

    @FXML
    void btnIssueOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnIssue.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/view/issueBook_view.fxml", "Library Management System - Issue Book");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not open Issue Book Form");
            alert.setContentText("An error occurred while trying to open the Issue Book form. Please check the logs.");
            alert.showAndWait();
        }
    }

    @FXML
    void btnRegisterOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/view/registerMember_view.fxml", "Library Management System - Register Member");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not open Register Member Form");
            alert.setContentText("An error occurred while trying to open the Register Member form. Please check the logs.");
            alert.showAndWait();
        }
    }

    @FXML
    void btnReturnOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnReturn.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/view/returnBook_view.fxml", "Library Management System - Return Book");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not open Return Book Form");
            alert.setContentText("An error occurred while trying to open the Return Book form. Please check the logs.");
            alert.showAndWait();
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) btnSearch.getScene().getWindow();
            NavigationUtil.navigateTo(stage, "/view/searchBook_view.fxml", "Library Management System - Search Book");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Could not open Search Book Form");
            alert.setContentText("An error occurred while trying to open the Search Book form. Please check the logs.");
            alert.showAndWait();
        }
    }
}
