package practicum.compilations;

import practicum.compilations.dto.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompilationsRepository extends JpaRepository<Compilation, Long> {
    Long deleteCompilationDaoById(Long id);

    Optional<Compilation> findCompilationDaoById(Long id);

    @Query("select c from Compilation as c where coalesce(:pinned, c.pinned) = c.pinned " +
           "order by c.id offset :from rows fetch next :size rows only")
    List<Compilation> findAllCompilations(@Param("pinned") Boolean pinned, @Param("from") Long from,
                                          @Param("size") Long size);
}
