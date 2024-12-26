package practicum.categories;

import practicum.categories.dto.CategoryDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<CategoryDao, Long> {
    @Query("select c from CategoryDao as c order by c.id offset ?1 rows fetch next ?2 rows only")
    List<CategoryDao> getAllCategoryDao(Long from, Long size);

    Optional<CategoryDao> getCategoryDaoById(Long id);

    Optional<CategoryDao> findCategoryDaoById(Long id);
}
