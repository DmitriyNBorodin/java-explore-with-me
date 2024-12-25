package practicum;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String statsHost = "stats-server";
    private final int statsServicePort = 9090;
    private final String statsServiceScheme = "http";
    private final DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveEvent(HttpServletRequest request, String app) {
        StatsDto newEvent = StatsDto.builder().app(app).uri(request.getRequestURI())
                .ip(request.getRemoteAddr()).timestamp(LocalDateTime.now()).build();
        HttpEntity<StatsDto> httpEntity = new HttpEntity<>(newEvent);
        URI uri;
        try {
            uri = new URIBuilder().setHost(statsHost)
                    .setPort(statsServicePort)
                    .setScheme(statsServiceScheme)
                    .setPath("/hit").build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Не удалось сохранить событие");
        }
        log.info("Событие {} сохраняется по запросу c uri={}", httpEntity, uri);
        restTemplate.postForObject(uri, httpEntity, StatsDto.class);
    }

    public List<GatheredStatsDto> getStatistics(List<Long> params, LocalDateTime start, LocalDateTime end, String unique) {
        log.info("Получение статистики для событий {}", params);
        List<NameValuePair> paramList = new ArrayList<>();
        for (Long eventId : params) {
            paramList.add(new BasicNameValuePair("uris", "/events/" + eventId));
        }
        if (start != null) {
            String encodedStart = URLEncoder.encode(start.format(customFormatter), StandardCharsets.UTF_8);
            paramList.add(new BasicNameValuePair("start", encodedStart));
        }
        if (end != null) {
            String encodedEnd = URLEncoder.encode(end.format(customFormatter), StandardCharsets.UTF_8);
            paramList.add(new BasicNameValuePair("end", encodedEnd));
        }
        if (unique != null && unique.equals("true")) {
            paramList.add(new BasicNameValuePair("unique", unique));
        }
        URI uri;
        try {
            uri = new URIBuilder().setHost(statsHost)
                    .setPort(statsServicePort)
                    .setScheme(statsServiceScheme)
                    .setPath("/stats")
                    .addParameters(paramList).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Не удалось получить статистику");
        }
        log.info("Получение статистики по uri {}", uri);
        ResponseEntity<GatheredStatsDto[]> statistics = restTemplate.getForEntity(uri, GatheredStatsDto[].class);
        return Arrays.stream(statistics.getBody()).toList();
    }
}
