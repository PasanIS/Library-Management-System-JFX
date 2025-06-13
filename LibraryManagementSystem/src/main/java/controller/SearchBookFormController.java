package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dao.BookDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import model.Book;
import util.NavigationUtil;

import java.io.IOException;
import java.util.List;

public class SearchBookFormController {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnSearchBooks;

    @FXML
    private JFXComboBox<String> cmbSearchBy;

    @FXML
    private Label lblAuthor;

    @FXML
    private Label lblAuthorView;

    @FXML
    private Label lblCategory;

    @FXML
    private Label lblCategoryView;

    @FXML
    private Label lblCopies;

    @FXML
    private Label lblCopiesView;

    @FXML
    private Label lblIsbn;

    @FXML
    private Label lblIsbnView;

    @FXML
    private Label lblSearchBy;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblTitleView;

    @FXML
    private TextField txtSearch;

    private final ContextMenu suggestionsMenu = new ContextMenu();

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
        txtSearch.clear();
        lblIsbnView.setText("");
        lblTitleView.setText("");
        lblAuthorView.setText("");
        lblCategoryView.setText("");
        lblCopiesView.setText("");
        cmbSearchBy.getSelectionModel().clearSelection();
        suggestionsMenu.hide();
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        // ------Get search criteria
        String searchType = cmbSearchBy.getValue();
        String searchText = txtSearch.getText();

        if (searchType == null || searchText == null || searchText.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Please select search criteria and enter a search text . . .");
            return;
        }

        // -------Retrieve book details from DB
        Book book = BookDAO.searchBook(searchType, searchText.trim());

        if (book == null) {

            showAlert(Alert.AlertType.INFORMATION, "Book not found . . .");

            lblIsbnView.setText("");
            lblTitleView.setText("");
            lblAuthorView.setText("");
            lblCategoryView.setText("");
            lblCopiesView.setText("");

            return;
        }

        lblIsbnView.setText(book.getIsbn());
        lblTitleView.setText(book.getTitle());
        lblAuthorView.setText(book.getAuthor());
        lblCategoryView.setText(book.getCategory());
        lblCopiesView.setText(String.valueOf(book.getCopies()));
    }



    // ------------Initialize------------
    public void initialize() {
        cmbSearchBy.getItems().addAll("ISBN", "Title");

        txtSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() >= 3) {
                String searchType = cmbSearchBy.getValue();
                if (searchType != null) {
                    List<String> suggestions = BookDAO.getSuggestions(searchType, newText);
                    showAutoSuggestions(suggestions);
                }
            } else {
                suggestionsMenu.hide();
            }
        });
    }

    private void showAutoSuggestions(List<String> suggestions) {
        suggestionsMenu.getItems().clear();
        for (String suggestion : suggestions) {
            MenuItem item = new MenuItem(suggestion);
            item.setOnAction(e -> txtSearch.setText(suggestion));
            suggestionsMenu.getItems().add(item);
        }

        if (!suggestionsMenu.isShowing()) {
            suggestionsMenu.show(txtSearch, Side.BOTTOM, 0, 0);
        }
    }

    // ------------Show Alert------------
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Library System");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
