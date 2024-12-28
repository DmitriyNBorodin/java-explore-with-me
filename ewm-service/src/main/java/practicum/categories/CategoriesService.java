package practicum.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import practicum.categories.dto.Category;
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

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(String from, String size) {
        Long fromLong = Long.parseLong(from);
        Long sizeLong = Long.parseLong(size);
        return categoryRepository.getAllCategory(fromLong, sizeLong).stream()
                .map(categoryDtoMapper::convertCategoryToDto).toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryDtoById(Long catId) {
        log.info("Получение dto категории по id={}", catId);
        Category requiredCategory = categoryRepository.getCategoryById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Отсутствует категория с id=" + catId));
        return categoryDtoMapper.convertCategoryToDto(requiredCategory);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long catId) {
        log.info("Получение категории по id={}", catId);
        return categoryRepository.getCategoryById(catId)
                .orElseThrow(() -> new ObjectNotFoundException("Отсутствует категория с id=" + catId));
    }

    @Transactional
    public CategoryDto addNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории {}", newCategoryDto);
        Category savedCategory = categoryRepository.save(categoryDtoMapper.convertToCategory(newCategoryDto));
        return categoryDtoMapper.convertCategoryToDto(savedCategory);
    }

    @Transactional
    public void deleteCategoryById(Long catId) {
        log.info("Удаление категории id={}", catId);
        checkCategoryExistence(catId);
        categoryRepository.deleteById(catId);
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, NewCategoryDto updatingCategory) {
        Category categoryToUpdate = checkCategoryExistence(catId);
        log.info("Обновление категории {} на {}", categoryToUpdate, updatingCategory);
        categoryToUpdate.setName(updatingCategory.getName());
        return categoryDtoMapper.convertCategoryToDto(categoryRepository.save(categoryToUpdate));
    }

    public Category checkCategoryExistence(Long catId) {
        return categoryRepository.findCategoryById(catId).orElseThrow(
                () -> new ObjectNotFoundException("Category with id=" + catId + " was not found"));
    }
}
