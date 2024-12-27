package practicum.categories.dto;

import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper {
    public Category convertToCategoryDao(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public CategoryDto convertCategoryDaoToDto(Category categoryDao) {
        return CategoryDto.builder()
                .id(categoryDao.getId())
                .name(categoryDao.getName())
                .build();
    }
}
