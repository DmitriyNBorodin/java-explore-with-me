package practicum.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import practicum.categories.CategoriesService;
import practicum.events.dto.Event;
import practicum.events.dto.EventDtoMapper;
import practicum.events.dto.EventFullDto;
import practicum.events.dto.UpdateEventAdminRequest;
import practicum.events.states.EventState;
import practicum.events.states.StateAction;
import practicum.util.ForbiddenActionException;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;
    private final CategoriesService categoriesService;

    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<String> userIdsString, List<String> statesString, List<String> categoriesIdsString,
                                               String startString, String endString, String fromString, String sizeString) {
        List<Long> userIds = null;
        if (userIdsString != null) {
            userIds = userIdsString.stream().map(Long::parseLong).toList();
        }
        List<Long> categoriesIds = null;
        if (categoriesIdsString != null) {
            categoriesIds = categoriesIdsString.stream().map(Long::parseLong).toList();
        }
        List<EventState> states = null;
        if (statesString != null) {
            states = statesString.stream().map(EventState::valueOf).toList();
        }
        Long from = Long.parseLong(fromString);
        Long size = Long.parseLong(sizeString);
        LocalDateTime start;
        LocalDateTime end;
        DateTimeFormatter dTF =
                new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ [HH][:mm][:ss][.SSS]]")
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter();
        if (startString != null) {
            start = LocalDateTime.parse(URLDecoder.decode(startString, StandardCharsets.UTF_8), dTF);
        } else {
            start = LocalDateTime.now().minusYears(20);
        }
        if (endString != null) {
            end = LocalDateTime.parse(URLDecoder.decode(endString, StandardCharsets.UTF_8), dTF);
        } else {
            end = LocalDateTime.now().plusYears(20);
        }
        log.info("Получение событий администратором по параметрам userIds={}, states={}, categoriesId={}, start={}, end={}, from={}, size={}",
                userIds, states, categoriesIds, start, end, from, size);
        List<Event> requiredEvents = eventRepository.findAllEventByAdmin(userIds, states, categoriesIds, start, end, from, size);
        log.info("Получено {} событий", requiredEvents.size());
        List<EventFullDto> requiredEventsDto = requiredEvents.stream().map(eventDtoMapper::assembleEventFullDto).toList();
        return eventDtoMapper.assignViewsAndRequests(requiredEventsDto);
    }

    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        log.info("Обновление полей {} события с id={}", updateRequest, eventId);
        Event eventToUpdate = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не удалось получить данные о событии с id=" + eventId));
        validateUpdateConditions(eventToUpdate, updateRequest);
        Event updatedEvent = eventDtoMapper.updateEventFieldsByAdmin(eventToUpdate, updateRequest);
        eventRepository.save(updatedEvent);
        log.info("Обновлено событие {}", updatedEvent);
        EventFullDto updatedEventDto = eventDtoMapper.assembleEventFullDto(updatedEvent);
        return eventDtoMapper.assignViewsAndRequests(updatedEventDto);
    }

    private void validateUpdateConditions(Event eventToUpdate, UpdateEventAdminRequest updateRequest) {
        if (updateRequest.getEventDate() != null && updateRequest.getEventDate().minusHours(1).isBefore(LocalDateTime.now())) {
            throw new ForbiddenActionException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: "
                                               + updateRequest.getEventDate());
        }
        if (updateRequest.getStateAction() != null && updateRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)
            && !eventToUpdate.getState().equals(EventState.PENDING)) {
            throw new ForbiddenActionException("Cannot publish the event because it's not in the right state: " + eventToUpdate.getState());
        }
        if (updateRequest.getStateAction() != null && updateRequest.getStateAction().equals(StateAction.REJECT_EVENT)
            && eventToUpdate.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenActionException("Cannot reject the event because it's not in the right state: " + eventToUpdate.getState());
        }
    }
}
