package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dao.BookDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Author;
import model.Book;
import model.Category;
import model.Publisher;
import util.NavigationUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class AddBookFormController {

    @FXML
    private JFXButton btnAddBook;

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnRemoveBook;

    @FXML
    private JFXButton btnSearch;

    @FXML
    private JFXButton btnUpdateBook;

    @FXML
    private JFXTextField txtAuthor;

    @FXML
    private JFXTextField txtCategory;

    @FXML
    private JFXTextField txtIsbn;

    @FXML
    private JFXTextField txtPublisher;

    @FXML
    private TextField txtSearch;

    @FXML
    private JFXTextField txtTitle;

    @FXML
    private JFXTextField txtTotalQty;

    @FXML
    void btnAddBookOnAction(ActionEvent event) {

        String isbn = txtIsbn.getText();
        String title = txtTitle.getText();
        String authorName = txtAuthor.getText();
        String categoryName = txtCategory.getText();
        String totalQty = txtTotalQty.getText();

        if (isbn.isEmpty() || title.isEmpty() || authorName.isEmpty() || categoryName.isEmpty() || totalQty.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled . . .");
            return;
        }

        int copies;
        try {
            copies = Integer.parseInt(totalQty);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Copies must be a valid number . . .");
            return;
        }

        try {
            String bookId = generateId();
            BookDAO bookDAO = new BookDAO();
            Author author = bookDAO.getOrCreateAuthorByName(authorName);
            Category category = bookDAO.getOrCreateCategoryByName(categoryName);
            Publisher publisher = new BookDAO().getOrCreatePublisherById("P001", "Default Publisher");


            Book book = new Book();
            book.setBookId(bookId);
            book.setTitle(title);
            book.setIsbn(isbn);
            book.setAuthor(author);
            book.setCategory(category);
            book.setPublisher(publisher);
            book.setTotalQty(copies);
            book.setAvailableQty(copies);

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
        if (isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Enter ISBN to remove . . .");
            return;
        }

        Book book = BookDAO.searchBook("ISBN", isbn);
        if (book == null) {
            showAlert(Alert.AlertType.ERROR, "Book not found . . .");
            return;
        }

        BookDAO bookDAO = new BookDAO();
        boolean deleted = bookDAO.removeBook(book.getBookId());
        if (deleted) {
            showAlert(Alert.AlertType.CONFIRMATION, "Book removed successfully . . .");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to remove book . . .");
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) throws SQLException {
        String isbn = txtSearch.getText();
        if (isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Enter ISBN to search . . .");
            return;
        }

        Book book = BookDAO.searchBook("ISBN", isbn);
        if (book != null) {
            txtIsbn.setText(book.getIsbn());
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthor().getName());
            txtCategory.setText(book.getCategory().getName());
            txtTotalQty.setText(String.valueOf(book.getTotalQty()));
        } else {
            showAlert(Alert.AlertType.WARNING, "Book not found . . .");
        }
    }

    @FXML
    void btnUpdateBookOnAction(ActionEvent event) {
        String isbn = txtIsbn.getText();
        String title = txtTitle.getText();
        String authorName = txtAuthor.getText();
        String categoryName = txtCategory.getText();
        String copiesText = txtTotalQty.getText();

        if (isbn.isEmpty() || title.isEmpty() || authorName.isEmpty() || categoryName.isEmpty() || copiesText.isEmpty()) {
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

        Book existingBook = BookDAO.searchBook("ISBN", isbn);
        if (existingBook == null) {
            showAlert(Alert.AlertType.WARNING, "Book not found . . .");
            return;
        }

        existingBook.setTitle(title);
        existingBook.setIsbn(isbn);
        existingBook.setTotalQty(copies);
        existingBook.setAvailableQty(copies);
        existingBook.getAuthor().setName(authorName);
        existingBook.getCategory().setName(categoryName);

        BookDAO bookDAO = new BookDAO();
        boolean updated = bookDAO.updateBook(existingBook);

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
        txtTotalQty.clear();
        txtSearch.clear();
        txtPublisher.clear();
    }

    // ------------Generate ID------------
    private String generateId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
