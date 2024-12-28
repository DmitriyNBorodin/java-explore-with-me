package practicum.categories;

import practicum.categories.dto.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category as c order by c.id offset ?1 rows fetch next ?2 rows only")
    List<Category> getAllCategory(Long from, Long size);

    Optional<Category> getCategoryById(Long id);

    Optional<Category> findCategoryById(Long id);
}
