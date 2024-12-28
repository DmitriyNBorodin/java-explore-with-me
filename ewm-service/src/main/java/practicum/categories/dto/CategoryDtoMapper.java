package practicum.categories.dto;

import org.springframework.stereotype.Component;

import java.util.InputMismatchException;

@Component
public class CategoryDtoMapper {
    public Category convertToCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            throw new InputMismatchException("Failed to get new category data");
        }
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public CategoryDto convertCategoryToDto(Category categoryDao) {
        return CategoryDto.builder()
                .id(categoryDao.getId())
                .name(categoryDao.getName())
                .build();
    }
}
