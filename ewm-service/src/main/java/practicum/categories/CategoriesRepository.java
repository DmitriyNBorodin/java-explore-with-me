package practicum.categories;

import practicum.categories.dto.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<CategoryDto, Long> {
    @Query("select c from CategoryDto as c order by c.id offset ?1 rows fetch next ?2 rows only")
    List<CategoryDto> getAllCategoryDto(Long from, Long size);

    Optional<CategoryDto> getCategoryDtoById(Long id);

    Optional<CategoryDto> findCategoryDtoById(Long id);
}
