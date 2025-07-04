package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dto.BookDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.ServiceFactory;
import service.custom.BookService;
import util.NavigationUtil;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

public class AddBookFormController implements Initializable {
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
    private JFXTextField txtBookId;
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

    private BookService bookService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the BookService using the ServiceFactory
        bookService = ServiceFactory.getBookService();
        // Generate a new Book ID when the form is initialized
        generateNewBookId();
    }

    private void generateNewBookId() {
        // Ensure txtBookId is not null before calling setText
        if (txtBookId != null) {
            txtBookId.setText("B" + UUID.randomUUID().toString().substring(0, 7).toUpperCase());
            txtBookId.setDisable(true); // Make it non-editable by user
        } else {
            System.err.println("Error: txtBookId is null in generateNewBookId(). FXML injection failed.");
        }
    }

    private void populateForm(BookDTO book) {
        if (book != null) {
            txtBookId.setText(book.getBookId());
            txtTitle.setText(book.getTitle());
            txtAuthor.setText(book.getAuthor());
            txtCategory.setText(book.getCategory());
            txtPublisher.setText(book.getPublisher());
            txtIsbn.setText(book.getIsbn());
            txtTotalQty.setText(String.valueOf(book.getCopiesAvailable()));
        }
    }

    private void clearForm() {
        txtTitle.clear();
        txtAuthor.clear();
        txtCategory.clear();
        txtPublisher.clear();
        txtIsbn.clear();
        txtTotalQty.clear();
        txtSearch.clear();
        generateNewBookId();
    }

    private boolean validateInput() {
        if (txtTitle.getText().trim().isEmpty() ||
                txtAuthor.getText().trim().isEmpty() ||
                txtCategory.getText().trim().isEmpty() ||
                txtPublisher.getText().trim().isEmpty() ||
                txtIsbn.getText().trim().isEmpty() ||
                txtTotalQty.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields must be filled out.");
            return false;
        }
        try {
            int copies = Integer.parseInt(txtTotalQty.getText().trim());
            if (copies < 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Total Quantity must be a non-negative number.");
                return false;
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Total Quantity must be a valid integer.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- FXML Action Event Handlers ---


    @FXML
    void btnAddOnAction(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        BookDTO bookDTO = new BookDTO(
                txtBookId.getText(),
                txtTitle.getText(),
                txtAuthor.getText(),
                txtCategory.getText(),
                txtPublisher.getText(),
                txtIsbn.getText(),
                Integer.parseInt(txtTotalQty.getText()),
                false // New books are not deleted by default
        );

        if (bookService.save(bookDTO)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add book. Check logs for details. (ISBN might be duplicate)");
        }
    }

    @FXML
    void btnUpdateBookOnAction(ActionEvent event) {
        if (txtBookId.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Book Selected", "Please search for a book first to update it.");
            return;
        }
        if (!validateInput()) {
            return;
        }

        BookDTO bookDTO = new BookDTO(
                txtBookId.getText(),
                txtTitle.getText(),
                txtAuthor.getText(),
                txtCategory.getText(),
                txtPublisher.getText(),
                txtIsbn.getText(),
                Integer.parseInt(txtTotalQty.getText()),
                false
        );

        if (bookService.update(bookDTO)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book. Check logs for details.");
        }
    }

    @FXML
    void btnRemoveBookOnAction(ActionEvent event) {

        String bookIdToRemove = txtBookId.getText();

        if (bookIdToRemove == null || bookIdToRemove.trim().isEmpty() || "Auto Generated".equals(bookIdToRemove)) {
            showWarningAlert("Input Required", "No Book ID Entered", "Please enter or select a Book ID to remove.");
            return;
        }

        Optional<BookDTO> bookCheck = bookService.findById(bookIdToRemove);
        if (bookCheck.isEmpty()) {
            showErrorAlert("Book Not Found", "Invalid Book ID or Already Removed", "No active book found with the ID: " + bookIdToRemove + ". It might have already been removed.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Removal");
        confirmationAlert.setHeaderText("Remove Book from Active Circulation?");
        // Include the book title in the confirmation if found
        confirmationAlert.setContentText("Are you sure you want to mark '" + bookCheck.get().getTitle() + "' (ID: " + bookIdToRemove + ") as removed? Its borrowing history will be preserved.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = bookService.markAsDeleted(bookIdToRemove);

            if (success) {
                System.out.println("Book with ID " + bookIdToRemove + " successfully marked as deleted (soft delete).");
                showInfoAlert("Success", "Book Removed", "The book has been successfully marked as removed from active circulation.");
                clearForm();
                generateNewBookId();
            } else {
                System.err.println("Failed to mark book with ID " + bookIdToRemove + " as deleted.");
                showErrorAlert("Error", "Removal Failed", "Could not mark the book as removed. A database error occurred.");
            }
        } else {
            System.out.println("Book removal cancelled by user.");
        }
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Input Empty", "Please enter a Book ID, ISBN, Title, or Author to search.");
            return;
        }

        Optional<BookDTO> foundBook = Optional.empty();

        // 1. Try searching by Book ID directly
        if (query.startsWith("B") && query.length() > 1) {
            foundBook = bookService.findById(query);
        }

        // 2. If not found by ID, try by ISBN
        if (foundBook.isEmpty()) {
            foundBook = bookService.findByIsbn(query);
        }

        // 3. If still not found, try a general search by title/author/category/isbn
        if (foundBook.isEmpty()) {
            List<BookDTO> searchResults = bookService.searchBooks(query);
            if (!searchResults.isEmpty()) {

                foundBook = Optional.of(searchResults.get(0));
                if (searchResults.size() > 1) {
                    showAlert(Alert.AlertType.INFORMATION, "Multiple Results",
                            "Multiple books found. Displaying the first match. Please refine your search if needed.");
                }
            }
        }


        if (foundBook.isPresent()) {
            populateForm(foundBook.get());
            showAlert(Alert.AlertType.INFORMATION, "Book Found", "Book details loaded.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Book Not Found", "No book found matching the search query.");
            clearForm();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "Form Cleared", "All fields have been cleared.");
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        NavigationUtil.navigateTo(stage, "/view/dashboard_view.fxml", "Library Management System - Dashboard"); // Corrected path
    }
}
