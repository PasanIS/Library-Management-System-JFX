package model;

import javafx.beans.property.*;

public class BookTM {
    private final StringProperty isbn;
    private final StringProperty title;
    private final StringProperty author;
    private final StringProperty category;
    private final IntegerProperty copies;

    public BookTM(String isbn, String title, String author, String category, int copies) {
        this.isbn = new SimpleStringProperty(isbn);
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
        this.category = new SimpleStringProperty(category);
        this.copies = new SimpleIntegerProperty(copies);
    }

    public StringProperty isbnProperty() { return isbn; }
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }
    public StringProperty categoryProperty() { return category; }
    public IntegerProperty copiesProperty() { return copies; }
}
