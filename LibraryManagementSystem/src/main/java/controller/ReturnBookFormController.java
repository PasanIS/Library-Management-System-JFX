package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dao.BookDAO;
import dao.BorrowDAO;
import dao.MemberDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.Book;
import model.Borrow;
import model.Member;
import util.NavigationUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReturnBookFormController {

    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnReturnBook;

    @FXML
    private JFXComboBox<Member> cboxSelectMember;

    @FXML
    private DatePicker datePickerReturnDate;

    @FXML
    private Label lblCurrentTime;

    @FXML
    private Label lblFine;

    @FXML
    private Label lblFinePreview;

    @FXML
    private Label lblIssuedBooks;

    @FXML
    private Label lblReturnDate;

    @FXML
    private Label lblSelectMember;

    @FXML
    private TableView<Borrow> tblBookDetails;

    @FXML
    private TableColumn<Borrow, Integer> colBookId;

    @FXML
    private TableColumn<Borrow, LocalDateTime> colIssueDateAndTime;

    @FXML
    private TableColumn<Borrow, String> colTitle;

    // ------------------------------------------
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final BookDAO bookDAO = new BookDAO();

    private ObservableList<Borrow> borrows = FXCollections.observableArrayList();
    // ------------------------------------------

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
        tblBookDetails.getItems().clear();
        datePickerReturnDate.setValue(LocalDate.now());
        lblFinePreview.setText("Your fine will display here . . .");
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        Borrow selected = tblBookDetails.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a book to return . . .");
            return;
        }

        LocalDate returnDate = datePickerReturnDate.getValue();
        LocalDate issueDate = selected.getIssueDate().toLocalDate();
        if (returnDate == null || returnDate.isBefore(issueDate)) {
            showAlert(Alert.AlertType.ERROR, "Return date must be on or after issue date . . .");
            return;
        }

        long overdueDays = returnDate.toEpochDay() - issueDate.toEpochDay();
        double fine = overdueDays > 14 ? (overdueDays - 14) * 10 : 0;

        try {
            borrowDAO.returnBook(selected.getBorrowId(), returnDate, fine);
            bookDAO.increaseCopies(selected.getBookId());
            loadBorrowedBooks(selected.getMemberId());

            lblFinePreview.setText("Fine paid: Rs. " + fine);
            showAlert(Alert.AlertType.INFORMATION, "Book returned successfully . . .");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to return book . . .");
        }
    }

    // ------------Initialization------------
    public void initialize() {
        setupClock();
        setupTable();
        loadMembers();

        datePickerReturnDate.setValue(LocalDate.now());

        cboxSelectMember.setOnAction(actionEvent -> {
            Member member = cboxSelectMember.getValue();
            if (member != null) {
                loadBorrowedBooks(member.getMemberId());
            }
        });

        // ----------When a book is selected, try to calculate fine immediately
        tblBookDetails
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {
            calculateFinePreview();
        });

        // ----------When the return date is changed, update fine preview
        datePickerReturnDate
                .valueProperty()
                .addListener((obs, oldDate, newDate) -> {
            calculateFinePreview();
        });
    }


    // ------------Setup Clock------------
    public void setupClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            lblCurrentTime.setText(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    // ------------Setup Table------------
    private void setupTable() {
        tblBookDetails.setItems(borrows);
        tblBookDetails.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(cellData -> {
                    try {
                        int bookId = cellData.getValue().getBookId();
                        Book book = bookDAO.getBookById(bookId);
                        if (book != null) {
                            return new ReadOnlyStringWrapper(book.getTitle());
                        } else {
                            return new ReadOnlyStringWrapper("Unknown Title");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return new ReadOnlyStringWrapper("Error");
                    }
                });
        colIssueDateAndTime.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
    }

    // ------------Load Members------------
    public void loadMembers() {
        try {
            cboxSelectMember.getItems().addAll(memberDAO.getAllMembers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------Load Borrowed Books------------
    private void loadBorrowedBooks(String memberId) {
        try {
            borrows.clear();
            borrows.addAll(borrowDAO.getBorrowingsByMember(memberId));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------Fine Preview------------
    private void calculateFinePreview() {
        Borrow selected = tblBookDetails.getSelectionModel().getSelectedItem();
        LocalDate returnDate = datePickerReturnDate.getValue();

        if (selected != null && returnDate != null) {
            LocalDate issueDate = selected.getIssueDate().toLocalDate();

            if (!returnDate.isBefore(issueDate)) {
                long overdueDays = returnDate.toEpochDay() - issueDate.toEpochDay();
                double fine = overdueDays > 14 ? (overdueDays - 14) * 10 : 0;
                lblFinePreview.setText(fine > 0 ? "Overdue Fine : Rs. " + fine : "No fine . . .");

                if (fine > 0) {
                    showAlert(Alert.AlertType.WARNING, "Fine alert: Member must pay Rs. " + fine);
                }
            } else {
                lblFinePreview.setText("Invalid return date . . .");
            }
        } else {
            lblFinePreview.setText("Your Fine will display here . . .");
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
