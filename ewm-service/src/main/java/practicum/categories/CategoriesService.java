package practicum.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.categories.dto.CategoryDao;
import practicum.categories.dto.CategoryDto;
import practicum.categories.dto.CategoryDtoMapper;
import practicum.categories.dto.NewCategoryDto;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriesService {
    private final CategoriesRepository categoryRepository;
    private final CategoryDtoMapper categoryDtoMapper;

    public List<CategoryDto> getAllCategories(String from, String size) {
        Long fromLong = Long.parseLong(from);
        Long sizeLong = Long.parseLong(size);
        return categoryRepository.getAllCategoryDao(fromLong, sizeLong).stream()
                .map(categoryDtoMapper::convertCategoryDaoToDto).toList();
    }

    public CategoryDto getCategoryDtoById(Long catId) {
        log.info("Получение dto категории по id={}", catId);
        CategoryDao requiredCategory = categoryRepository.getCategoryDaoById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Отсутствует категория с id=" + catId));
        return categoryDtoMapper.convertCategoryDaoToDto(requiredCategory);
    }

    public CategoryDao getCategoryDaoById(Long catId) {
        log.info("Получение категории по id={}", catId);
        return categoryRepository.getCategoryDaoById(catId).orElseThrow(() -> new ObjectNotFoundException("Отсутствует категория с id=" + catId));
    }

    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории {}", newCategoryDto);
        CategoryDao savedCategory = categoryRepository.save(categoryDtoMapper.convertToCategoryDao(newCategoryDto));
        return categoryDtoMapper.convertCategoryDaoToDto(savedCategory);
    }

    public void deleteCategoryById(Long catId) {
        log.info("Удаление категории id={}", catId);
        checkCategoryExistence(catId);
        categoryRepository.deleteById(catId);
    }


    public CategoryDto updateCategory(Long catId, NewCategoryDto updatingCategory) {
        CategoryDao categoryToUpdate = checkCategoryExistence(catId);
        log.info("Обновление категории {} на {}", categoryToUpdate, updatingCategory);
        categoryToUpdate.setName(updatingCategory.getName());
        return categoryDtoMapper.convertCategoryDaoToDto(categoryRepository.save(categoryToUpdate));
    }


    public CategoryDao checkCategoryExistence(Long catId) {
        return categoryRepository.findCategoryDaoById(catId).orElseThrow(
                () -> new ObjectNotFoundException("Category with id=" + catId + " was not found"));
    }
}
