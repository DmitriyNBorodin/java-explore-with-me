package practicum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatheredStatsDto {
    private String app;
    private String uri;
    private long hits;
}
