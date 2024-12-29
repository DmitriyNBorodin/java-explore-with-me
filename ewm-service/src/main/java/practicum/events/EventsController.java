package practicum.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.StatsClient;
import practicum.events.dto.EventFullDto;
import practicum.events.dto.EventShortDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class EventsController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text, @RequestParam(required = false) List<String> categories,
                                         @RequestParam(required = false) String paid, @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd, @RequestParam(required = false) String onlyAvailable,
                                         @RequestParam(required = false) String sort, @RequestParam(defaultValue = "0") String from,
                                         @RequestParam(defaultValue = "10") String size, HttpServletRequest request) {
        statsClient.saveEvent(request, "ewm-main-service");
        return eventService.getEventsByAnyone(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Получение события с id={}", eventId);
        statsClient.saveEvent(request, "ewm-main-service");
        return eventService.getEventByAnyone(eventId);
    }
}
