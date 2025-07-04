package controller;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import dto.BorrowingDTO;
import dto.FineDTO;
import dto.MemberDTO;
import entity.Borrowing;
import entity.Fine;
import service.custom.BorrowingService;
import service.custom.FineService;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReturnBookFormController implements Initializable {
    @FXML
    private JFXButton btnBack;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnReturnBook;

    @FXML
    private JFXCheckBox cboxPaid;

    @FXML
    private JFXComboBox<MemberDTO> cboxSelectMember;

    @FXML
    private TableColumn<BorrowingDTO, String> colBookId;

    @FXML
    private TableColumn<BorrowingDTO, String> colTitle;

    @FXML
    private TableColumn<BorrowingDTO, LocalDate> colIssueDateAndTime;

    @FXML
    private DatePicker datePickerReturnDate;

    @FXML
    private Label lblCurrentTime;

    @FXML
    private Label lblFinePreview;

    @FXML
    private TableView<BorrowingDTO> tblBookDetails;

    private MemberService memberService;
    private BorrowingService borrowingService;
    private FineService fineService;

    private ObservableList<MemberDTO> memberList;
    private ObservableList<BorrowingDTO> borrowedBooksList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize services
        memberService = ServiceFactory.getMemberService();
        borrowingService = ServiceFactory.getBorrowingService();
        fineService = ServiceFactory.getFineService();

        // Initialize ObservableLists
        memberList = FXCollections.observableArrayList();
        borrowedBooksList = FXCollections.observableArrayList();

        // Set up ComboBox converter for members
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

        // Set up TableView columns
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colIssueDateAndTime.setCellValueFactory(new PropertyValueFactory<>("borrowDate")); // Maps to LocalDate

        tblBookDetails.setItems(borrowedBooksList);

        // Load initial data
        loadMembers();

        // Set current date to the DatePicker
        datePickerReturnDate.setValue(LocalDate.now());

        // Set up real-time clock for lblCurrentTime
        setupRealTimeClock();

        // Add listener to member selection to load borrowed books
        cboxSelectMember.valueProperty().addListener((obs, oldMember, newMember) -> {
            if (newMember != null) {
                loadBorrowedBooksForMember(newMember.getMemberId());
            } else {
                borrowedBooksList.clear();
                lblFinePreview.setText("Your fine will display here...");
                cboxPaid.setSelected(false);
                cboxPaid.setDisable(true);
            }
        });

        // Add listener to table selection to calculate fine preview
        tblBookDetails.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                calculateAndDisplayFine(newSelection);
            } else {
                lblFinePreview.setText("Your fine will display here...");
                cboxPaid.setSelected(false);
                cboxPaid.setDisable(true);
            }
        });

        // Add listener to return date picker to recalculate fine
        datePickerReturnDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            BorrowingDTO selectedBorrowing = tblBookDetails.getSelectionModel().getSelectedItem();
            if (selectedBorrowing != null && newDate != null) {
                calculateAndDisplayFine(selectedBorrowing);
            }
        });

        // Initially disable paid checkbox
        cboxPaid.setDisable(true);
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

    private void loadBorrowedBooksForMember(String memberId) {
        borrowedBooksList.clear();
        List<BorrowingDTO> borrowings = borrowingService.findBorrowingsByMemberId(memberId);

        List<BorrowingDTO> activeBorrowings = borrowings.stream()
                .filter(b -> b.getReturnDate() == null || b.getStatus() == Borrowing.BorrowingStatus.borrowed || b.getStatus() == Borrowing.BorrowingStatus.late)
                .collect(Collectors.toList());
        borrowedBooksList.addAll(activeBorrowings);
    }

    private void calculateAndDisplayFine(BorrowingDTO selectedBorrowing) {
        if (selectedBorrowing == null) {
            lblFinePreview.setText("Your fine will display here...");
            cboxPaid.setSelected(false);
            cboxPaid.setDisable(true);
            return;
        }

        LocalDate returnDate = datePickerReturnDate.getValue();
        if (returnDate == null) {
            lblFinePreview.setText("Select a return date to calculate fine.");
            cboxPaid.setSelected(false);
            cboxPaid.setDisable(true);
            return;
        }

        Double fineAmount = fineService.calculateFineAmount(selectedBorrowing.getDueDate(), returnDate);
        lblFinePreview.setText(String.format("Fine: $%.2f", fineAmount));

        if (fineAmount > 0.00) {
            cboxPaid.setDisable(false);

            Optional<FineDTO> existingFine = fineService.findFineByBorrowId(selectedBorrowing.getBorrowId());
            if (existingFine.isPresent() && existingFine.get().getPaid() == Fine.FinePaidStatus.yes) {
                cboxPaid.setSelected(true);
                cboxPaid.setDisable(true); // Disable if already paid
                lblFinePreview.setText(String.format("Fine: $%.2f (PAID)", fineAmount));
            } else {
                cboxPaid.setSelected(false);
            }
        } else {
            cboxPaid.setSelected(false);
            cboxPaid.setDisable(true);
        }
    }

    private void clearForm() {
        cboxSelectMember.getSelectionModel().clearSelection();
        borrowedBooksList.clear();
        tblBookDetails.getSelectionModel().clearSelection();
        datePickerReturnDate.setValue(LocalDate.now());
        lblFinePreview.setText("Your fine will display here...");
        cboxPaid.setSelected(false);
        cboxPaid.setDisable(true);
        showAlert(Alert.AlertType.INFORMATION, "Form Cleared", "All fields have been cleared.");
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
    void btnReturnBookOnAction(ActionEvent event) {
        BorrowingDTO selectedBorrowing = tblBookDetails.getSelectionModel().getSelectedItem();
        if (selectedBorrowing == null) {
            showAlert(Alert.AlertType.WARNING, "No Book Selected", "Please select a book from the table to return.");
            return;
        }

        LocalDate returnDate = datePickerReturnDate.getValue();
        if (returnDate == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a return date.");
            return;
        }

        if (borrowingService.returnBook(selectedBorrowing.getBorrowId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully!");

            Double currentFineAmount = fineService.calculateFineAmount(selectedBorrowing.getDueDate(), returnDate);
            if (currentFineAmount > 0.00 && cboxPaid.isSelected()) {
                Optional<FineDTO> fineOptional = fineService.findFineByBorrowId(selectedBorrowing.getBorrowId());
                if (fineOptional.isPresent()) {
                    if (fineService.payFine(fineOptional.get().getFineId())) {
                        showAlert(Alert.AlertType.INFORMATION, "Fine Paid", "Fine has been marked as paid.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Fine Payment Error", "Failed to mark fine as paid.");
                    }
                }
            }
            clearForm();

            loadMembers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to return book. Check logs for details.");
        }
    }
}
