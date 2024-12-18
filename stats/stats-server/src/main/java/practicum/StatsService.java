package practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {
    private final StatsRepository statsRepository;
    private final String app = "ewm-main-service";

    public StatsDto saveEvent(StatsDto newEvent) {
        log.info("Saving event {}", newEvent);
        return statsRepository.save(newEvent);
    }

    public List<GatheredStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        DateTimeFormatter dTF =
                new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ [HH][:mm][:ss][.SSS]]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter();
        LocalDateTime startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), dTF);
        LocalDateTime endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), dTF);
        log.info("Gathering statistics for timegap {} - {}", startTime, endTime);
        List<StatsDto> requiredDto = statsRepository.getStatsDtoByDateTime(startTime, endTime);
        log.info("Extracted {} dto", requiredDto.size());
        Map<String, List<String>> rawStatistics = requiredDto.stream().collect(groupingBy(StatsDto::getUri))
                .entrySet().stream()
                .map(stringListEntry -> new AbstractMap.SimpleEntry<>(stringListEntry.getKey(),
                        stringListEntry.getValue().stream().map(StatsDto::getIp).collect(Collectors.toList())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String, List<String>> filteredByUriStatistics;
        List<GatheredStatsDto> statisticsList;
        if (uris != null && !uris.isEmpty()) {
            filteredByUriStatistics = rawStatistics.entrySet().stream()
                    .filter(stringListEntry -> uris.contains(stringListEntry.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } else {
            filteredByUriStatistics = rawStatistics;
        }
        if (unique) {
            statisticsList = filteredByUriStatistics.entrySet().stream()
                    .map(entry -> new GatheredStatsDto(app, entry.getKey(), new HashSet<>(entry.getValue()).size()))
                    .sorted(Comparator.comparing(GatheredStatsDto::getHits).reversed())
                    .collect(Collectors.toList());
        } else {
            statisticsList = filteredByUriStatistics.entrySet().stream()
                    .map(entry -> new GatheredStatsDto(app, entry.getKey(), entry.getValue().size()))
                    .sorted(Comparator.comparing(GatheredStatsDto::getHits).reversed())
                    .collect(Collectors.toList());
        }
        return statisticsList;
    }
}
