package practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatsDto, Long> {
    @Query("select d from practicum.StatsDto as d where d.uri in (:uris) and d.timestamp between :start and :end")
    List<StatsDto> getStatsDtoByDateTime(@Param("uris") List<String> uris,
            @Param("start")LocalDateTime start, @Param("end")LocalDateTime end);
}
