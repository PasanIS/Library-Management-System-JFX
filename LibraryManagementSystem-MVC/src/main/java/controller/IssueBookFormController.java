package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dao.BookDAO;
import dao.BorrowDAO;
import dao.MemberDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.Duration;
import model.Book;
import model.Category;
import model.Member;
import util.DBConnection;
import util.NavigationUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IssueBookFormController {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnIssueBook;

    @FXML
    private JFXComboBox<Book> cboxBook;

    @FXML
    private JFXComboBox<Category> cboxCategory;

    @FXML
    private JFXComboBox<Member> cboxSelectMember;

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

    //--------------------------------------------
    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();
    //--------------------------------------------

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
        cboxSelectMember.getSelectionModel().clearSelection();
        cboxCategory.getSelectionModel().clearSelection();
        cboxBook.getItems().clear(); //------ Clear books when category is reset
        datePickerIssueDate.setValue(LocalDate.now()); //------Set to default date
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        Book book = cboxBook.getValue();
        Member member = cboxSelectMember.getValue();
        LocalDate selectedDate = datePickerIssueDate.getValue();

        if (book == null || member == null || selectedDate == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a book, a member, and a date...");
            return;
        }

        if (book.getAvailableQty() <= 0) {
            showAlert(Alert.AlertType.WARNING, "This book is out of stock...");
            return;
        }

        try {
            boolean alreadyBorrowed = borrowDAO.hasAlreadyBorrowed(member.getMemberId(), book.getBookId());
            if (alreadyBorrowed) {
                showAlert(Alert.AlertType.WARNING, "This member has already borrowed this book...");
                return;
            }

            // Enforce 2-book borrowing limit
            borrowDAO.issueBookIfAllowed(member.getMemberId(), book.getBookId(), selectedDate.atStartOfDay());

            bookDAO.reduceAvailableQty(book.getBookId());
            showAlert(Alert.AlertType.INFORMATION, "Book issued successfully...!");
            loadBooksByCategory(String.valueOf(cboxCategory.getValue()));
        } catch (IllegalStateException ise) {
            // This exception is thrown from issueBookIfAllowed if member has 2 active borrowings
            showAlert(Alert.AlertType.WARNING, ise.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error occurred...");
        }
    }

    //----------Initialization------------
    public void initialize() {
        startClock();           // Start the digital clock
        loadCategories();       // Load all category objects into cboxCategory
        loadMembers();          // Load all member objects into cboxSelectMember

        // Set listener for category selection to dynamically load books
        cboxCategory.setOnAction(e -> {
            Category selected = cboxCategory.getValue();  // Get selected Category object
            if (selected != null) {
                loadBooksByCategory(selected.getCategoryId()); // Load books by category ID
            }
        });

        datePickerIssueDate.setValue(LocalDate.now()); // Set today's date as default
    }


    //------------Start Clock-------------
    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            lblCurrentTime.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    //------------Load Categories-------------
    private void loadCategories() {
        try {
            cboxCategory.getItems().addAll(bookDAO.getAllCategories());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------Load Books By Category-------------
    private void loadBooksByCategory(String categoryId) {
        try {
            cboxBook.getItems().setAll(bookDAO.getBooksByCategory(categoryId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------Load Members-------------
    private void loadMembers() {
        try {
            cboxSelectMember.getItems().addAll(memberDAO.getAllMembers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //------------Show Alert-------------
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Library Management");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
