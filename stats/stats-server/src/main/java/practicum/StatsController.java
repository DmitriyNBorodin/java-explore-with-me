package practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatsDto saveEvent(@RequestBody StatsDto newEvent) {
        return statsService.saveEvent(newEvent);
    }

    @GetMapping("/stats")
    public List<GatheredStatsDto> getStatistics(@RequestParam(required = false) String start, @RequestParam(required = false) String end,
                                                @RequestParam(required = false) List<String> uris,
                                                @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.getStatistics(start, end, uris, unique);
    }
}
