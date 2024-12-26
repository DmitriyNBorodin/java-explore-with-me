package practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import practicum.exceptions.IncorrectRequestException;

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
        DateTimeFormatter dTF = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ [HH][:mm][:ss][.SSS]]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        LocalDateTime startTime;
        if (start != null) {
            startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), dTF);
        } else {
            throw new IncorrectRequestException("Start time required");
        }
        LocalDateTime endTime;
        if (end != null) {
            endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), dTF);
        } else {
            throw new IncorrectRequestException("End time required");
        }
        if (startTime.isAfter(endTime)) {
            throw new IncorrectRequestException("Start at " + startTime + "cannot be after end at " + endTime);
        }
        if (uris == null) {
            throw new IncorrectRequestException("Uris required");
        }
        log.info("Gathering statistics with uri {} for time gap {} - {}", uris, startTime, endTime);
        List<StatsDto> requiredDto = statsRepository.getStatsDtoByDateTime(uris, startTime, endTime);
        log.info("Extracted {} dto", requiredDto.size());
        Map<String, List<String>> rawStatistics = requiredDto.stream().collect(groupingBy(StatsDto::getUri))
                .entrySet().stream()
                .map(stringListEntry -> new AbstractMap.SimpleEntry<>(stringListEntry.getKey(),
                        stringListEntry.getValue().stream().map(StatsDto::getIp).collect(Collectors.toList())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        List<GatheredStatsDto> statisticsList;
        if (unique) {
            statisticsList = rawStatistics.entrySet().stream()
                    .map(entry -> new GatheredStatsDto(app, entry.getKey(), new HashSet<>(entry.getValue()).size()))
                    .sorted(Comparator.comparing(GatheredStatsDto::getHits).reversed())
                    .collect(Collectors.toList());
        } else {
            statisticsList = rawStatistics.entrySet().stream()
                    .map(entry -> new GatheredStatsDto(app, entry.getKey(), entry.getValue().size()))
                    .sorted(Comparator.comparing(GatheredStatsDto::getHits).reversed())
                    .collect(Collectors.toList());
        }
        log.info("statistics for uri {} is {}", uris, statisticsList);
        return statisticsList;
    }
}
