package practicum.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import practicum.events.dto.EventFullDto;
import practicum.events.dto.EventRequestStatusUpdateRequest;
import practicum.events.dto.EventRequestStatusUpdateResult;
import practicum.events.dto.EventShortDto;
import practicum.events.dto.NewEventDto;
import practicum.events.dto.ParticipationRequestDto;
import practicum.events.dto.UpdateEventUserRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class UserEventsController {
    private final UserEventsService userEventsService;

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable Long userId, @RequestParam(defaultValue = "0") String from,
                                               @RequestParam(defaultValue = "10") String size) {
        return userEventsService.getEventsByUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@PathVariable Long userId, @Validated @RequestBody NewEventDto newEvent) {
        return userEventsService.addNewEvent(userId, newEvent);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId, @PathVariable Long eventId,
                                         HttpServletRequest request) {
        return userEventsService.getUserEventById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId, @PathVariable Long eventId,
                                          @Validated @RequestBody UpdateEventUserRequest updateRequest) {
        return userEventsService.updateEventByUser(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return userEventsService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId,
                                                                  @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return userEventsService.updateUserEventRequests(userId, eventId, updateRequest);
    }
}
