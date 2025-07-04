package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import dao.BookDAO;
import dao.BorrowDAO;
import dao.FineDAO;
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
import model.Fine;
import model.Member;
import util.NavigationUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private TableColumn<Borrow, String> colBookId;

    @FXML
    private TableColumn<Borrow, LocalDateTime> colIssueDateAndTime;

    @FXML
    private TableColumn<Borrow, String> colTitle;

    @FXML
    private JFXCheckBox cboxPaid;

    // ------------------------------------------
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final BookDAO bookDAO = new BookDAO();

    private ObservableList<Borrow> borrowList = FXCollections.observableArrayList();
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
        borrowList.clear();
        lblFinePreview.setText("Your fine will display here...");
        datePickerReturnDate.setValue(LocalDate.now());
        cboxPaid.setSelected(false);
    }

    @FXML
    void btnReturnBookOnAction(ActionEvent event) {
        Borrow borrow = tblBookDetails.getSelectionModel().getSelectedItem();
        LocalDate returnDate = datePickerReturnDate.getValue();

        if (borrow == null || returnDate == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a borrowed book and return date...");
            return;
        }

        if (returnDate.isBefore(borrow.getIssueDate().toLocalDate())) {
            showAlert(Alert.AlertType.ERROR, "Return date cannot be before issue date...");
            return;
        }

        long overdue = returnDate.toEpochDay() - borrow.getIssueDate().toLocalDate().toEpochDay();
        double fine = overdue > 14 ? (overdue - 14) * 10 : 0;

        boolean isFinePaid = cboxPaid.isSelected();
        LocalDate paidDate = isFinePaid ? LocalDate.now() : null;

        try {
            // 1. Update borrow record (return date)
            borrowDAO.returnBook(borrow.getBorrowId(), returnDate, fine);

            // 2. Add fine record
            if (fine > 0) {
                FineDAO fineDAO = new FineDAO();
                fineDAO.addFine(new Fine(
                        null, // fine_id (auto-increment)
                        borrow.getBorrowId(),
                        fine,
                        LocalDate.now(),
                        isFinePaid,
                        paidDate
                ));
            }

            bookDAO.increaseAvailableQty(borrow.getBookId());

            loadBorrowedBooks(borrow.getMemberId());

            lblFinePreview.setText("Fine paid: Rs. " + fine);
            showAlert(Alert.AlertType.INFORMATION, "Book returned successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Return failed due to DB error.");
        }
    }

    // ------------Initialization------------
    public void initialize() {
        setupClock();
        setupTable();
        loadMembers();

        datePickerReturnDate.setValue(LocalDate.now());

        cboxSelectMember.setOnAction(e -> {
            Member member = cboxSelectMember.getValue();
            if (member != null) {
                loadBorrowedBooks(member.getMemberId());
            }
        });

        tblBookDetails.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> calculateFinePreview());
        datePickerReturnDate.valueProperty().addListener((obs, oldVal, newVal) -> calculateFinePreview());
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
        tblBookDetails.setItems(borrowList);
        tblBookDetails.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Use lambdas to access nested Book fields inside Borrow
        colBookId.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getBook().getBookId())
        );

        colTitle.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().getBook().getTitle())
        );

        colIssueDateAndTime.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
    }

    // ------------Load Members------------
    public void loadMembers() {
        try {
            cboxSelectMember.getItems().setAll(memberDAO.getAllMembers());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to load members");
        }
    }

    // ------------Load Borrowed Books-----------
    private void loadBorrowedBooks(String memberId) {
        System.out.println("Loading borrowings for memberId: " + memberId);
        try {
            borrowList.clear();
            List<Borrow> borrows = borrowDAO.getBorrowingsByMember(memberId);
            System.out.println("Borrowings fetched: " + borrows.size());
            borrowList.addAll(borrows);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Failed to load borrowings");
        }
    }

    // ------------Fine Preview------------
    private void calculateFinePreview() {
        Borrow borrow = tblBookDetails.getSelectionModel().getSelectedItem();
        LocalDate returnDate = datePickerReturnDate.getValue();

        if (borrow != null && returnDate != null) {
            LocalDate issueDate = borrow.getIssueDate().toLocalDate();
            if (!returnDate.isBefore(issueDate)) {
                long overdueDays = returnDate.toEpochDay() - issueDate.toEpochDay();
                double fine = overdueDays > 14 ? (overdueDays - 14) * 10 : 0;
                lblFinePreview.setText(fine > 0 ? "Overdue Fine: Rs. " + fine : "No fine.");
            } else {
                lblFinePreview.setText("Invalid return date.");
            }
        } else {
            lblFinePreview.setText("Your fine will display here...");
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
