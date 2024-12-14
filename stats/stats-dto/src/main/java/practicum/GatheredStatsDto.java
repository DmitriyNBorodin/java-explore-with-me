package practicum;

import lombok.Data;

@Data
public class GatheredStatsDto {
    private final String app;
    private final String uri;
    private final long hits;
}
