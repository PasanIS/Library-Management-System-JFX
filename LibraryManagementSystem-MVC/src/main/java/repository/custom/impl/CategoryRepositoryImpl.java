package repository.custom.impl;

import model.entity.Category;
import repository.custom.CategoryRepository;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepositoryImpl implements CategoryRepository {

    @Override
    public boolean save(Category category) throws SQLException {
        String sql = "INSERT INTO categories (category_id, name) VALUES (?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category.getCategoryId());
            preparedStatement.setString(2, category.getName());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ? WHERE category_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getCategoryId());
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String categoryId) throws SQLException {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, categoryId);
            return preparedStatement.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Category> findById(String categoryId) throws SQLException {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, categoryId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(new Category(
                            resultSet.getString("category_id"),
                            resultSet.getString("name")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findAll() throws SQLException {
        return findAllCategories(); // -----Delegating to the more specific method
    }

    @Override
    public List<Category> findAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Category category = new Category(
                        resultSet.getString("category_id"),
                        resultSet.getString("name")
                );
                categories.add(category);
            }
        }
        return categories;
    }

    @Override
    public Optional<Category> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM categories WHERE name = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Category(rs.getString("category_id"), name));
                }
            }
        }
        return Optional.empty();
    }
}
