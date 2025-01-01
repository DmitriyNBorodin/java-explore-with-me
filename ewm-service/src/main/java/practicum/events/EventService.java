package practicum.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import practicum.events.dto.Event;
import practicum.events.dto.EventDtoMapper;
import practicum.events.dto.EventFullDto;
import practicum.events.dto.EventShortDto;
import practicum.events.states.EventState;
import practicum.events.states.SortOption;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByAnyone(String text, List<String> categoriesIdsString, String paidString, String startString, String endString,
                                                 String onlyAvailableString, String sortString, String fromString, String sizeString) {
        List<Long> categoriesIds = null;
        if (categoriesIdsString != null) {
            categoriesIds = categoriesIdsString.stream().map(Long::parseLong).toList();
        }
        Long from = Long.parseLong(fromString);
        Long size = Long.parseLong(sizeString);
        LocalDateTime start;
        LocalDateTime end;
        DateTimeFormatter dTF = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[ [HH][:mm][:ss][.SSS]]")
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter();
        if (startString != null) {
            start = LocalDateTime.parse(startString, dTF);
        } else {
            start = LocalDateTime.now();
        }
        if (endString != null) {
            end = LocalDateTime.parse(endString, dTF);
        } else {
            end = LocalDateTime.now().plusYears(50);
        }
        if (start.isAfter(end)) {
            throw new InputMismatchException("Start date must be before end date");
        }
        boolean onlyAvailable = Boolean.parseBoolean(onlyAvailableString);
        Boolean paid = null;
        if (paidString != null) {
            paid = Boolean.parseBoolean(paidString);
        }
        SortOption sort;
        if (sortString != null) {
            sort = SortOption.valueOf(sortString);
        } else {
            sort = SortOption.UNSORTED;
        }
        log.info("Получение информации о событиях за период {} - {}. Параметры текст={}, категории={}",
                start, end, text, categoriesIds);
        List<Event> eventDaoByRequest = eventRepository.findAllEventByAnyone(text, categoriesIds, start, end, from, size);
        List<EventFullDto> eventDtoList = eventDaoByRequest.stream().map(eventDtoMapper::assembleEventFullDto).toList();
        Stream<EventFullDto> eventStream = eventDtoList.stream().filter(event -> event.getState().equals(EventState.PUBLISHED));
        if (onlyAvailable) {
            eventStream = eventStream.filter(eventDto -> eventDto.getParticipantLimit() != 0 && eventDto.getConfirmedRequests() < eventDto.getParticipantLimit());
        }
        if (paid != null) {
            if (paid) {
                eventStream = eventStream.filter(EventFullDto::getPaid);
            } else {
                eventStream = eventStream.filter(eventDto -> !eventDto.getPaid());
            }
        }
        if (sort.equals(SortOption.EVENT_DATE)) {
            eventStream = eventStream.sorted(Comparator.comparing(EventFullDto::getEventDate).reversed());
        } else if (sort.equals(SortOption.VIEWS)) {
            eventStream = eventStream.sorted(Comparator.comparing(EventFullDto::getViews).reversed());
        } else if (sort.equals(SortOption.RATING)) {
            eventStream = eventStream.sorted(Comparator.comparing(EventFullDto::getRating).reversed());
        }
        List<EventShortDto> listOfEvents = eventStream.map(eventDtoMapper::convertToShortDto).toList();
        log.info("Получены события {}", listOfEvents);
        return listOfEvents;
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByAnyone(Long eventId) {
        Event requiredEventDao = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не удалось получить данные о событии с id=" + eventId));
        if (!requiredEventDao.getState().equals(EventState.PUBLISHED)) {
            throw new ObjectNotFoundException("Событие с id=" + eventId + " недоступно");
        }
        EventFullDto requiredEvent = eventDtoMapper.assembleEventFullDto(requiredEventDao);
        return eventDtoMapper.assignViewsAndRequests(requiredEvent);
    }
}
