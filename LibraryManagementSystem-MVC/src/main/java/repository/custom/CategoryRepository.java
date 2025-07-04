package repository.custom;

import model.entity.Category;
import repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CRUDRepository<Category, String> {
    // Additional methods specific to Category can be defined here if needed
    // For example, methods to find categories by name or other attributes can be added
    List<Category> findAllCategories() throws SQLException;
    Optional<Category> findByName(String name) throws SQLException; // For getOrCreateCategoryByName
}
