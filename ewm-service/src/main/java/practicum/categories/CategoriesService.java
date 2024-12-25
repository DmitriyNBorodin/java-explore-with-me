package practicum.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.categories.dto.CategoryDto;
import practicum.categories.dto.NewCategoryDto;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriesService {
    private final CategoriesRepository categoryRepository;

    public List<CategoryDto> getAllCategories(String from, String size) {
        Long fromLong = Long.parseLong(from);
        Long sizeLong = Long.parseLong(size);
        return categoryRepository.getAllCategoryDto(fromLong, sizeLong);
    }

    public CategoryDto getCategoryById(Long catId) {
        log.info("Получение категории по id={}", catId);
        return categoryRepository.getCategoryDtoById(catId).orElseThrow(() -> new ObjectNotFoundException("Отсутствует категория с id=" + catId));
    }

    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории {}", newCategoryDto);
        return categoryRepository.save(convertToCategoryDto(newCategoryDto));
    }

    public void deleteCategoryById(Long catId) {
        log.info("Удаление категории id={}", catId);
        checkCategoryExistence(catId);
        categoryRepository.deleteById(catId);
    }


    public CategoryDto updateCategory(Long catId, NewCategoryDto updatingCategory) {
        CategoryDto categoryToUpdate = checkCategoryExistence(catId);
        log.info("Обновление категории {} на {}", categoryToUpdate, updatingCategory);
        categoryToUpdate.setName(updatingCategory.getName());
        return categoryRepository.save(categoryToUpdate);
    }

    private CategoryDto convertToCategoryDto(NewCategoryDto newCategoryDto) {
        return CategoryDto.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public CategoryDto checkCategoryExistence(Long catId) {
        return categoryRepository.findCategoryDtoById(catId).orElseThrow(
                () -> new ObjectNotFoundException("Category with id=" + catId + " was not found"));
    }
}
