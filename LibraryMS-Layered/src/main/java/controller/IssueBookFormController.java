package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dto.BookDTO;
import dto.BorrowingDTO;
import dto.MemberDTO;
import service.custom.BookService;
import service.custom.BorrowingService;
import service.custom.MemberService;
import service.ServiceFactory;
import util.NavigationUtil;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class IssueBookFormController implements Initializable {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnIssueBook;

    @FXML
    private JFXComboBox<BookDTO> cboxBook;
    @FXML
    private JFXComboBox<String> cboxCategory;
    @FXML
    private JFXComboBox<MemberDTO> cboxSelectMember;

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

    private MemberService memberService;
    private BookService bookService;
    private BorrowingService borrowingService;

    private ObservableList<MemberDTO> memberList;
    private ObservableList<BookDTO> allBooksList; // To hold all books for filtering
    private ObservableList<String> categoryList;
    private ObservableList<BookDTO> filteredBooksList; // Books displayed in cboxBook

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize services
        memberService = ServiceFactory.getMemberService();
        bookService = ServiceFactory.getBookService();
        borrowingService = ServiceFactory.getBorrowingService();

        // Initialize ObservableLists
        memberList = FXCollections.observableArrayList();
        allBooksList = FXCollections.observableArrayList();
        categoryList = FXCollections.observableArrayList();
        filteredBooksList = FXCollections.observableArrayList();


        cboxSelectMember.setConverter(new StringConverter<MemberDTO>() {

            @Override
            public String toString(MemberDTO member) {
                return member != null ? member.getName() + " (" + member.getMemberId() + ")" : "";
            }

            @Override
            public MemberDTO fromString(String string) {
                return null;
            }
        });

        cboxBook.setConverter(new StringConverter<BookDTO>() {

            @Override
            public String toString(BookDTO book) {
                return book != null ? book.getTitle() + " (" + book.getBookId() + ")" : "";
            }

            @Override
            public BookDTO fromString(String string) {

                return null;
            }
        });

        cboxBook.setItems(filteredBooksList);

        // Load initial data
        loadMembers();
        loadBooksAndCategories();

        // Set current date to the DatePicker
        datePickerIssueDate.setValue(LocalDate.now());

        // Set up real-time clock for lblCurrentTime
        setupRealTimeClock();

        // Add listener to category combobox to filter books
        cboxCategory.valueProperty()
                .addListener((obs, oldCategory, newCategory) ->
                        filterBooksByCategory(newCategory));
    }

    private void setupRealTimeClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            lblCurrentTime.setText(currentTime.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }


    private void loadMembers() {
        memberList.clear();
        List<MemberDTO> members = memberService.findAll();
        memberList.addAll(members);
        cboxSelectMember.setItems(memberList);
    }

    private void loadBooksAndCategories() {
        allBooksList.clear();
        categoryList.clear();

        List<BookDTO> books = bookService.findAll();
        allBooksList.addAll(books);

        List<String> distinctCategories = allBooksList.stream()
                .map(BookDTO::getCategory)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        categoryList.add("All Categories");
        categoryList.addAll(distinctCategories);
        cboxCategory.setItems(categoryList);
        cboxCategory.getSelectionModel().selectFirst();
    }

    private void filterBooksByCategory(String selectedCategory) {
        filteredBooksList.clear();
        if (selectedCategory == null || "All Categories".equals(selectedCategory)) {
            filteredBooksList.addAll(allBooksList);
        } else {
            List<BookDTO> filtered = allBooksList.stream()
                    .filter(book -> selectedCategory.equals(book.getCategory()))
                    .collect(Collectors.toList());
            filteredBooksList.addAll(filtered);
        }
        cboxBook.getSelectionModel().clearSelection(); // Clear previous book selection
    }

    private void clearForm() {
        cboxSelectMember.getSelectionModel().clearSelection();
        cboxCategory.getSelectionModel().selectFirst(); // Reset to "All Categories"
        cboxBook.getSelectionModel().clearSelection();
        datePickerIssueDate.setValue(LocalDate.now());
        showAlert(Alert.AlertType.INFORMATION, "Form Cleared", "All fields have been cleared.");
    }

    private boolean validateInput() {
        if (cboxSelectMember.getSelectionModel().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a member.");
            return false;
        }
        if (cboxBook.getSelectionModel().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a book.");
            return false;
        }
        if (datePickerIssueDate.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select an issue date.");
            return false;
        }
        if (datePickerIssueDate.getValue().isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Issue Date cannot be in the future.");
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
    void btnIssueBookOnAction(ActionEvent event) {
        if (!validateInput()) {
            return;
        }

        MemberDTO selectedMember = cboxSelectMember.getSelectionModel().getSelectedItem();
        BookDTO selectedBook = cboxBook.getSelectionModel().getSelectedItem();
        LocalDate issueDate = datePickerIssueDate.getValue();

        // Create a BorrowingDTO
        BorrowingDTO borrowingDTO = new BorrowingDTO();
        borrowingDTO.setMemberId(selectedMember.getMemberId());
        borrowingDTO.setBookId(selectedBook.getBookId());
        borrowingDTO.setBorrowDate(issueDate);

        if (borrowingService.borrowBook(borrowingDTO)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book issued successfully! Due Date: " + borrowingDTO.getDueDate());
            clearForm();
            loadBooksAndCategories();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to issue book. It might be unavailable or an error occurred.");
        }
    }
}
