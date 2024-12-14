package practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<StatsDto, Long> {
    @Query("select d from practicum.StatsDto as d where d.timestamp between ?1 and ?2")
    List<StatsDto> getStatsDtoByDateTime(LocalDateTime start, LocalDateTime end);
}
