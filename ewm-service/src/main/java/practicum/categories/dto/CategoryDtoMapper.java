package practicum.categories.dto;

import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper {
    public CategoryDao convertToCategoryDao(NewCategoryDto newCategoryDto) {
        return CategoryDao.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public CategoryDto convertCategoryDaoToDto(CategoryDao categoryDao) {
        return CategoryDto.builder()
                .id(categoryDao.getId())
                .name(categoryDao.getName())
                .build();
    }
}
