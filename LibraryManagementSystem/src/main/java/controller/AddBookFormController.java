package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dao.BookDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Book;
import util.NavigationUtil;

import java.io.IOException;
import java.sql.SQLException;

public class AddBookFormController {

    @FXML
    private JFXTextField txtIsbn;

    @FXML
    private JFXButton btnAddBook;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnRemoveBook;

    @FXML
    private JFXButton btnSearchBack;

    @FXML
    private JFXButton btnUpdateBook;

    @FXML
    private Label lblAuthor;

    @FXML
    private Label lblCategory;

    @FXML
    private Label lblCopies;

    @FXML
    private Label lblTitle;

    @FXML
    private JFXTextField txtAuthor;

    @FXML
    private JFXTextField txtCategory;

    @FXML
    private JFXTextField txtCopies;

    @FXML
    private TextField txtSearch;

    @FXML
    private JFXTextField txtTitle;

    @FXML
    void btnAddBookOnAction(ActionEvent event) {

        String isbn = txtIsbn.getText();
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String category = txtCategory.getText();
        String copiesText = txtCopies.getText();  // keep this as String

        if (isbn == null || title == null || author == null || category == null || copiesText == null ||
                isbn.isEmpty() ||
                title.isEmpty() ||
                author.isEmpty() ||
                category.isEmpty() ||
                copiesText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled . . .");
            return;
        }

        int copiesCount;
        try {
            copiesCount = Integer.parseInt(copiesText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Copies must be a valid number . . .");
            return;
        }

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setCopies(copiesCount);

        BookDAO bookDAO = new BookDAO();
        try {
            boolean added = BookDAO.addBook(book);
            if (added) {
                showAlert(Alert.AlertType.CONFIRMATION, "Book added successfully . . .");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to add book . . .");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
            e.printStackTrace();
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
    }

    @FXML
    void btnRemoveBookOnAction(ActionEvent event) {
        String isbn = txtIsbn.getText();
        if (isbn == null || isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Enter ISBN to remove . . .");
            return;
        }

        BookDAO bookDAO = new BookDAO();

        try {
            boolean deleted = bookDAO.removeBook(isbn);
            if (deleted) {
                showAlert(Alert.AlertType.CONFIRMATION, "Book removed successfully . . .");
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to remove book . . .");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) throws SQLException {
        String isbn = txtSearch.getText();
        if (isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Enter ISBN to search . . .");
            return;
        }

        BookDAO bookDAO = new BookDAO();
        Book book = bookDAO.getBookByIsbn(isbn);
        if (book != null) {
            txtIsbn.setText(book.getIsbn());
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthor());
            txtCategory.setText(book.getCategory());
            txtCopies.setText(String.valueOf(book.getCopies()));
        } else {
            showAlert(Alert.AlertType.WARNING, "Book not found . . .");
        }
    }

    @FXML
    void btnUpdateBookOnAction(ActionEvent event) {
        String isbn = txtIsbn.getText();
        String title = txtTitle.getText();
        String author = txtAuthor.getText();
        String category = txtCategory.getText();
        String copiesText = txtCopies.getText();

        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || category.isEmpty() || copiesText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled . . .");
            return;
        }

        int copies;
        try {
            copies = Integer.parseInt(copiesText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Copies must be a valid number . . .");
            return;
        }

        Book updatedBook = new Book();
        updatedBook.setIsbn(isbn);
        updatedBook.setTitle(title);
        updatedBook.setAuthor(author);
        updatedBook.setCategory(category);
        updatedBook.setCopies(copies);

        BookDAO bookDAO = new BookDAO();
        boolean updated = bookDAO.updateBook(updatedBook);

        if (updated) {
            showAlert(Alert.AlertType.CONFIRMATION, "Book updated successfully . . .");
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to update book . . .");
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

    // ------------Clear Fields------------
    private void clearFields() {
        txtIsbn.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtCategory.clear();
        txtCopies.clear();
    }


}
