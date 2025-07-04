package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dto.BookDTO;
import service.custom.BookService;
import service.ServiceFactory;
import util.NavigationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SearchBookFormController implements Initializable {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnSearchBooks;

    @FXML
    private JFXComboBox<String> cmbSearchBy;

    @FXML
    private TableColumn<BookDTO, String> colAuthor;

    @FXML
    private TableColumn<BookDTO, String> colCategory;

    @FXML
    private TableColumn<BookDTO, Integer> colCopies;

    @FXML
    private TableColumn<BookDTO, String> colIsbn;

    @FXML
    private TableColumn<BookDTO, String> colTitle;

    @FXML
    private TableView<BookDTO> tblSearchResults;

    @FXML
    private TextField txtSearch;

    private BookService bookService;
    private ObservableList<BookDTO> searchResultsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the BookService
        bookService = ServiceFactory.getBookService();

        // Initialize the ObservableList for TableView
        searchResultsList = FXCollections.observableArrayList();
        tblSearchResults.setItems(searchResultsList);

        // Populate the "Search By" ComboBox
        ObservableList<String> searchOptions = FXCollections.observableArrayList(
                "All", "Book ID", "ISBN", "Title", "Author", "Category"
        );
        cmbSearchBy.setItems(searchOptions);
        cmbSearchBy.getSelectionModel().selectFirst();

        // Set up cell value factories for TableView columns
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCopies.setCellValueFactory(new PropertyValueFactory<>("copiesAvailable"));

        // loadAllBooks();
    }

    private void loadAllBooks() {
        searchResultsList.clear();
        List<BookDTO> allBooks = bookService.findAll();
        searchResultsList.addAll(allBooks);
        if (allBooks.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Books Found", "There are no books registered in the system.");
        }
    }

    private void clearForm() {
        txtSearch.clear();
        cmbSearchBy.getSelectionModel().selectFirst(); // Reset to "All"
        searchResultsList.clear();
        showAlert(Alert.AlertType.INFORMATION, "Form Cleared", "Search fields and results have been cleared.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // --- FXML Action Event Handlers ---

    @FXML
    void btnBackOnAction(ActionEvent event) {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        NavigationUtil.navigateTo(stage, "/view/dashboard_view.fxml", "Library Management System - Dashboard");
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearForm();
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String searchBy = cmbSearchBy.getSelectionModel().getSelectedItem();
        String query = txtSearch.getText().trim();

        if (searchBy == null) {
            showAlert(Alert.AlertType.ERROR, "Search Error", "Please select a search criterion.");
            return;
        }

        searchResultsList.clear();

        if ("All".equals(searchBy)) {
            loadAllBooks();
            return;
        }

        if (query.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Search Input Empty", "Please enter a value to search for.");
            return;
        }

        List<BookDTO> results = null;

        List<BookDTO> allBooks = bookService.findAll(); // Fetch all to filter locally

        switch (searchBy) {
            case "Book ID":
                results = allBooks.stream()
                        .filter(book -> book.getBookId().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                break;
            case "ISBN":
                results = allBooks.stream()
                        .filter(book -> book.getIsbn().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                break;
            case "Title":
                results = allBooks.stream()
                        .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                break;
            case "Author":
                results = allBooks.stream()
                        .filter(book -> book.getAuthor().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                break;
            case "Category":
                results = allBooks.stream()
                        .filter(book -> book.getCategory().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
                break;
            default:
                results = bookService.searchBooks(query);
                break;
        }

        if (results != null && !results.isEmpty()) {
            searchResultsList.addAll(results);
            showAlert(Alert.AlertType.INFORMATION, "Search Complete", results.size() + " book(s) found.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No books found matching your search criteria.");
        }
    }
}
