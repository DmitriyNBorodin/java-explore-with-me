package practicum;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
public class GatheredStatsDto {
    private final String app;
    private final String uri;
    private final long hits;
}
