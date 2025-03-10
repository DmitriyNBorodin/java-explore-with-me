package practicum.categories;

import org.springframework.data.repository.query.Param;
import practicum.categories.dto.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category as c order by c.id offset :from rows fetch next :size rows only")
    List<Category> getAllCategory(@Param("from") Long from, @Param("size") Long size);

    Optional<Category> getCategoryById(Long id);

    Optional<Category> findCategoryById(Long id);
}
