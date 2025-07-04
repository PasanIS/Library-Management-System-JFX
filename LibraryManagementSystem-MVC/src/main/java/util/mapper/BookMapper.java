package util.mapper;

import model.dto.AuthorDTO;
import model.dto.BookDTO;
import model.dto.CategoryDTO;
import model.dto.PublisherDTO;
import model.entity.Author;
import model.entity.Book;
import model.entity.Category;
import model.entity.Publisher;

import java.util.List;
import java.util.stream.Collectors;

// ---------Convert between Entity and DTO objects----------
// ---------This utility class provides methods to convert between Book entity and BookDTO, as well as Category entity and CategoryDTO.

public class BookMapper {

    public static BookDTO toDto(Book book) {
        if (book == null) {
            return null;
        }
        BookDTO dto = new BookDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setTotalQty(book.getTotalQty());
        dto.setAvailableQty(book.getAvailableQty());

        if (book.getAuthor() != null) {
            dto.setAuthor(new AuthorDTO(book.getAuthor().getAuthorId(), book.getAuthor().getName()));
        }
        if (book.getCategory() != null) {
            dto.setCategory(new CategoryDTO(book.getCategory().getCategoryId(), book.getCategory().getName()));
        }
        if (book.getPublisher() != null) {
            dto.setPublisher(new PublisherDTO(book.getPublisher().getPublisherId(), book.getPublisher().getName()));
        }
        return dto;
    }

    public static Book toEntity(BookDTO dto) {
        if (dto == null) {
            return null;
        }
        Book entity = new Book();
        entity.setBookId(dto.getBookId());
        entity.setTitle(dto.getTitle());
        entity.setIsbn(dto.getIsbn());
        entity.setTotalQty(dto.getTotalQty());
        entity.setAvailableQty(dto.getAvailableQty());

        if (dto.getAuthor() != null) {
            entity.setAuthor(new Author(dto.getAuthor().getAuthorId(), dto.getAuthor().getName()));
        }
        if (dto.getCategory() != null) {
            entity.setCategory(new Category(dto.getCategory().getCategoryId(), dto.getCategory().getName()));
        }
        if (dto.getPublisher() != null) {
            entity.setPublisher(new Publisher(dto.getPublisher().getPublisherId(), dto.getPublisher().getName()));
        }
        return entity;
    }

    public static List<BookDTO> toDtoList(List<Book> books) {
        return books.stream()
                .map(BookMapper::toDto)
                .collect(Collectors.toList());
    }

    public static CategoryDTO toDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());
        return dto;
    }

    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        Category entity = new Category();
        entity.setCategoryId(dto.getCategoryId());
        entity.setName(dto.getName());
        return entity;
    }

    public static List<CategoryDTO> toCategoryDtoList(List<Category> categories) {
        return categories.stream()
                .map(BookMapper::toDto) // Using the Category toDto mapper
                .collect(Collectors.toList());
    }
}
