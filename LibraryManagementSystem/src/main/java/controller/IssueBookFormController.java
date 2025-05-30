package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dao.BookDAO;
import dao.BorrowDAO;
import dao.MemberDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.util.Duration;
import model.Book;
import model.Member;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

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
    private JFXComboBox<String> cboxCategory;

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
    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();
    //--------------------------------------------

    @FXML
    void btnBackOnAction(ActionEvent event) {
        // TODO document why this method is empty
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        // TODO document why this method is empty
    }

    @FXML
    void btnIssueBookOnAction(ActionEvent event) {
        Book book = cboxBook.getValue();
        Member member = cboxSelectMember.getValue();
        LocalDateTime now = LocalDateTime.now();

        if (book == null || member == null) {
            showAlert(Alert.AlertType.ERROR, "Please select both a book & a member...");
            return;
        }

        if (book.getCopies() <= 0) {
            showAlert(Alert.AlertType.WARNING, "This book is out of stocks...");
            return;
        }

        try {
            boolean alreadyBorrowed = borrowDAO.hasAlreadyBorrowed(member.getMemberId(),book.getBookId());
            if (alreadyBorrowed) {
                showAlert(Alert.AlertType.WARNING, "This member has already borrowed this book...");
                return;
            }

            borrowDAO.issueBook(member.getMemberId(), book.getBookId(), now);
            bookDAO.reduceCopies(book.getBookId());
            showAlert(Alert.AlertType.INFORMATION, "Book issued successfully..!");

            //------Refresh the list (to show Updated Copies)
            loadBooksByCategory(cboxCategory.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error occurred...");
        }
    }

    //----------Initialization------------
    public void initialize(URL location, ResourceBundle resources) {
        startClock();
        loadCategories();
        loadMembers();

        cboxCategory.setOnAction(e -> {
            String category = cboxCategory.getValue();
            if (category != null) {
                loadBooksByCategory(category);
            }
        });
        datePickerIssueDate.setValue(LocalDate.now()); //------Default (Current Date)
    }

    //------------Start Clock-------------
    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            lblCurrentTime.setText(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
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
    private void loadBooksByCategory(String category) {
        try {
            cboxBook.getItems().setAll(bookDAO.getBooksByCategory(category));
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
