package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import dao.BookDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import model.Book;
import model.BookTM;
import util.DBConnection;
import util.NavigationUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SearchBookFormController {

    @FXML
    private JFXComboBox<String> cmbSearchBy;

    @FXML
    private TextField txtSearch;

    @FXML
    private JFXButton btnSearchBooks;

    @FXML
    private TableView<BookTM> tblSearchResults;

    @FXML
    private TableColumn<BookTM, String> colIsbn;

    @FXML
    private TableColumn<BookTM, String> colTitle;

    @FXML
    private TableColumn<BookTM, String> colAuthor;

    @FXML
    private TableColumn<BookTM, String> colCategory;

    @FXML
    private TableColumn<BookTM, Integer> colCopies;

    @FXML
    private JFXButton btnClear;

    @FXML
    private JFXButton btnBack;

    private ObservableList<BookTM> bookList = FXCollections.observableArrayList();

    public void initialize() {
        cmbSearchBy.setItems(FXCollections.observableArrayList("ISBN", "Title", "Category"));
        colIsbn.setCellValueFactory(data -> data.getValue().isbnProperty());
        colTitle.setCellValueFactory(data -> data.getValue().titleProperty());
        colAuthor.setCellValueFactory(data -> data.getValue().authorProperty());
        colCategory.setCellValueFactory(data -> data.getValue().categoryProperty());
        colCopies.setCellValueFactory(data -> data.getValue().copiesProperty().asObject());
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String criteria = cmbSearchBy.getValue();
        String searchText = txtSearch.getText().trim();

        if (criteria == null || searchText.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a search type and enter a value!").show();
            return;
        }

        searchBooks(criteria, searchText);
    }

    private void searchBooks(String criteria, String value) {
        bookList.clear();

        String column;
        if ("ISBN".equals(criteria)) {
            column = "b.isbn";
        } else if ("Title".equals(criteria)) {
            column = "b.title";
        } else if ("Category".equals(criteria)) {
            column = "c.name";
        } else {
            return;
        }

        String query = "SELECT b.book_id, b.isbn, b.title, a.name AS author, c.name AS category, b.available_quantity " +
                "FROM books b " +
                "JOIN authors a ON b.author_id = a.author_id " +
                "JOIN categories c ON b.category_id = c.category_id " +
                "WHERE b.available_quantity > 0 AND " + column + " LIKE ?";


        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, "%" + value + "%");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                bookList.add(new BookTM(
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("available_quantity")
                ));
            }

            tblSearchResults.setItems(bookList);

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error occurred while searching books.").show();
        }
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        txtSearch.clear();
        cmbSearchBy.getSelectionModel().clearSelection();
        tblSearchResults.getItems().clear();
    }

    @FXML
    void btnBackOnAction(ActionEvent event) {
        try {
            NavigationUtil.loadScene("dashboard_view.fxml", btnBack);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}