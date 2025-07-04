package repository.custom.impl;

import model.entity.Book;
import model.entity.Borrow;
import model.entity.Member;
import repository.custom.BorrowRepository;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowRepositoryImpl implements BorrowRepository {

    // ----------Optional(Helping) method to map a ResultSet row to a Borrow entity
    private Borrow mapResultSetToBorrow(ResultSet resultSet) throws SQLException {
        LocalDateTime issueDate = resultSet.getTimestamp("borrow_date").toLocalDateTime();
        Timestamp returnTimestamp = resultSet.getTimestamp("return_date");
        LocalDateTime returnDate = returnTimestamp != null ? returnTimestamp.toLocalDateTime() : null;

        Borrow borrow = new Borrow();
        borrow.setBorrowId(resultSet.getString("borrow_id"));

        Member member = new Member();
        member.setMemberId(resultSet.getString("member_id"));
        member.setFullName(resultSet.getString("full_name")); // Assuming full_name is joined

        Book book = new Book();
        book.setBookId(resultSet.getString("book_id"));
        book.setTitle(resultSet.getString("title")); // Assuming title is joined

        borrow.setMember(member);
        borrow.setBook(book);
        borrow.setIssueDate(issueDate);
        borrow.setReturnDate(returnDate);
        borrow.setFine(resultSet.getDouble("fine_amount")); // Assuming fine_amount is joined

        return borrow;
    }

    @Override
    public boolean save(Borrow borrow) throws SQLException {
        // This is primarily handled by the issueBook method, but kept for CrudRepository compliance.
        // It's assumed borrow.getBorrowId() is already set, perhaps by generateNextBorrowId().
        String sql = "INSERT INTO borrowings (borrow_id, member_id, book_id, borrow_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, borrow.getBorrowId());
            preparedStatement.setString(2, borrow.getMember().getMemberId());
            preparedStatement.setString(3, borrow.getBook().getBookId());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(borrow.getIssueDate()));
            int rows = preparedStatement.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public boolean update(Borrow borrow) throws SQLException {
        // This method is for general updates. For return-specific updates, use updateReturnDateAndFine.
        String sql = "UPDATE borrowings SET member_id = ?, book_id = ?, borrow_date = ?, return_date = ? WHERE borrow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, borrow.getMember().getMemberId());
            preparedStatement.setString(2, borrow.getBook().getBookId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(borrow.getIssueDate()));
            if (borrow.getReturnDate() != null) {
                preparedStatement.setTimestamp(4, Timestamp.valueOf(borrow.getReturnDate()));
            } else {
                preparedStatement.setNull(4, Types.TIMESTAMP);
            }
            preparedStatement.setString(5, borrow.getBorrowId());
            int rows = preparedStatement.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public boolean delete(String borrowId) throws SQLException {
        String sql = "DELETE FROM borrowings WHERE borrow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, borrowId);
            int rows = preparedStatement.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public Optional<Borrow> findById(String borrowId) throws SQLException {
        String sql = "SELECT b.*, bo.title, m.full_name, COALESCE(f.fine_amount, 0.0) AS fine_amount " +
                "FROM borrowings b " +
                "JOIN books bo ON b.book_id = bo.book_id " +
                "JOIN members m ON b.member_id = m.member_id " +
                "LEFT JOIN fines f ON b.borrow_id = f.borrow_id " +
                "WHERE b.borrow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, borrowId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToBorrow(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Borrow> findAll() throws SQLException {
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT b.*, bo.title, m.full_name, COALESCE(f.fine_amount, 0.0) AS fine_amount " +
                "FROM borrowings b " +
                "JOIN books bo ON b.book_id = bo.book_id " +
                "JOIN members m ON b.member_id = m.member_id " +
                "LEFT JOIN fines f ON b.borrow_id = f.borrow_id";
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                list.add(mapResultSetToBorrow(resultSet));
            }
        }
        return list;
    }

    @Override
    public boolean hasAlreadyBorrowed(String memberId, String bookId) throws SQLException {
        String query = "SELECT * FROM borrowings WHERE member_id = ? AND book_id = ? AND return_date IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, memberId);
            preparedStatement.setString(2, bookId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public void issueBook(Borrow borrow) throws SQLException {
        // Renamed from original DAO to accept a Borrow entity, assuming ID is pre-generated.
        String sql = "INSERT INTO borrowings (borrow_id, member_id, book_id, borrow_date) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, borrow.getBorrowId());
            preparedStatement.setString(2, borrow.getMember().getMemberId());
            preparedStatement.setString(3, borrow.getBook().getBookId());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(borrow.getIssueDate()));
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Borrow> findBorrowingsByMember(String memberId) throws SQLException {
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT b.*, bo.title, m.full_name, COALESCE(f.fine_amount, 0.0) AS fine_amount " +
                "FROM borrowings b " +
                "JOIN books bo ON b.book_id = bo.book_id " +
                "JOIN members m ON b.member_id = m.member_id " +
                "LEFT JOIN fines f ON b.borrow_id = f.borrow_id " +
                "WHERE b.member_id = ? AND b.return_date IS NULL";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapResultSetToBorrow(resultSet));
                }
            }
        }
        return list;
    }

    @Override
    public void updateReturnDateAndFine(String borrowId, LocalDateTime returnDate, double fineAmount) throws SQLException {
        // ---------Removed direct fine insertion from here.
        // ---------FineRepository will handle that.
        String sqlUpdateBorrow = "UPDATE borrowings SET return_date = ? WHERE borrow_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps1 = connection.prepareStatement(sqlUpdateBorrow)) {
            ps1.setTimestamp(1, Timestamp.valueOf(returnDate));
            ps1.setString(2, borrowId);
            ps1.executeUpdate();
        }
    }

    @Override
    public String generateNextBorrowId() throws SQLException {
        String sql = "SELECT borrow_id FROM borrowings ORDER BY borrow_id DESC";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String lastId = rs.getString("borrow_id");
                if (lastId != null && lastId.startsWith("BR") && lastId.length() >= 5) {
                    try {
                        int number = Integer.parseInt(lastId.substring(2));
                        return String.format("BR%03d", number + 1);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
            }
        }
        return "BR001";
    }

    @Override
    public int countActiveBorrowingsByMember(String memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM borrowings WHERE member_id = ? AND return_date IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, memberId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }
}
