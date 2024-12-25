package practicum.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import practicum.events.dto.EventFullDto;
import practicum.events.dto.UpdateEventAdminRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
@Slf4j
public class AdminEventsController {
    private final AdminEventService adminEventService;

    @GetMapping
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false) List<String> users, @RequestParam(required = false) List<String> states,
                                               @RequestParam(required = false) List<String> categories, @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd, @RequestParam(defaultValue = "0") String from,
                                               @RequestParam(defaultValue = "10") String size) {
        return adminEventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId, @Validated @RequestBody UpdateEventAdminRequest updateRequest) {
        EventFullDto updatedEventFullDto = adminEventService.updateEventByAdmin(eventId, updateRequest);
        log.info("Событие после обновления администратором: {}", updatedEventFullDto);
        return updatedEventFullDto;
    }

}
