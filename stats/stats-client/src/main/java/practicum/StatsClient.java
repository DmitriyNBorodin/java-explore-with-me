package practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String statsServiceUrl = "http://localhost:9090/";

    public void saveEvent(HttpServletRequest request) {
        StatsDto newEvent = StatsDto.builder().app("ewm-main-service").uri(request.getRequestURI())
                .ip(request.getRemoteAddr()).timestamp(LocalDateTime.now()).build();
        HttpEntity<StatsDto> httpEntity = new HttpEntity<>(newEvent);
        restTemplate.postForObject(statsServiceUrl + "hit", httpEntity, StatsDto.class);
    }

    public List<GatheredStatsDto> getStatistics(String params) {
        List<GatheredStatsDto> statistics = restTemplate.getForObject(statsServiceUrl + "stats?" + params, List.class);
        return statistics;
    }
}
