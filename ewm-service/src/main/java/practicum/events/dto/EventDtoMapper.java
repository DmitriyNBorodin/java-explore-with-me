package practicum.events.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.categories.CategoriesService;
import practicum.events.RequestsRepository;
import practicum.events.states.EventState;
import practicum.events.states.RequestState;
import practicum.users.UserService;
import practicum.GatheredStatsDto;
import practicum.StatsClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventDtoMapper {
    private final UserService userService;
    private final CategoriesService categoriesService;
    private final StatsClient statsClient;
    private final RequestsRepository requestsRepository;

    public EventDao assembleNewEventDao(Long userId, NewEventDto newEventDto) {
        return EventDao.builder()
                .annotation(newEventDto.getAnnotation())
                .category(categoriesService.checkCategoryExistence(newEventDto.getCategory()))
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(userService.getUserDtoById(userId))
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .publishedOn(null)
                .requestModeration(newEventDto.getRequestModeration())
                .state(EventState.PENDING)
                .title(newEventDto.getTitle())
                .build();
    }

    public EventFullDto assembleEventFullDto(EventDao eventDao) {
        return EventFullDto.builder()
                .id(eventDao.getId())
                .annotation(eventDao.getAnnotation())
                .category(eventDao.getCategory())
                .createdOn(eventDao.getCreatedOn())
                .description(eventDao.getDescription())
                .eventDate(eventDao.getEventDate())
                .initiator(userService.convertToShortDto(eventDao.getInitiator()))
                .location(new Location(eventDao.getLat(), eventDao.getLon()))
                .paid(eventDao.getPaid())
                .participantLimit(eventDao.getParticipantLimit())
                .publishedOn(eventDao.getPublishedOn())
                .requestModeration(eventDao.getRequestModeration())
                .state(eventDao.getState())
                .title(eventDao.getTitle())
                .confirmedRequests(0)
                .views(0L)
                .build();
    }

    public EventShortDto assembleEventShortDto(EventDao eventDao) {
        return EventShortDto.builder()
                .id(eventDao.getId())
                .annotation(eventDao.getAnnotation())
                .category(eventDao.getCategory())
                .confirmedRequests(0)
                .eventDate(eventDao.getEventDate())
                .initiator(userService.convertToShortDto(eventDao.getInitiator()))
                .paid(eventDao.getPaid())
                .title(eventDao.getTitle())
                .views(0L)
                .build();
    }

    public <T extends EventShortDto> List<T> assignViewsAndRequests(List<T> eventDto) {
        if (!eventDto.isEmpty()) {
            Map<Long, T> eventDtoMap = eventDto.stream().collect(Collectors.toMap(EventShortDto::getId,
                    Function.identity()));
            List<Long> eventIdList = eventDto.stream().map(EventShortDto::getId).collect(Collectors.toList());
            List<GatheredStatsDto> statsList = statsClient.getStatistics(eventIdList, LocalDateTime.now().minusYears(50),
                    LocalDateTime.now(), "true");
            List<ParticipationRequestDto> requestsOfEvents = requestsRepository.findParticipationRequestDtoByEventIn(eventIdList);
            Map<Long, Long> mapOfConfirmedRequests = requestsOfEvents.stream()
                    .filter(request -> request.getStatus().equals(RequestState.CONFIRMED))
                    .collect(Collectors.groupingBy(ParticipationRequestDto::getEvent, Collectors.counting()));
            for (GatheredStatsDto statsDto : statsList) {
                String[] splittedUri = statsDto.getUri().split("/");
                if (splittedUri.length == 3) {
                    Long id = Long.parseLong(splittedUri[2]);
                    eventDtoMap.get(id).setViews(statsDto.getHits());
                }
            }
            for (Map.Entry<Long, Long> entry : mapOfConfirmedRequests.entrySet()) {
                eventDtoMap.get(entry.getKey()).setConfirmedRequests(entry.getValue().intValue());
            }
            return eventDtoMap.values().stream().toList();
        } else {
            return new ArrayList<>();
        }
    }

    public EventShortDto convertToShortDto(EventFullDto fullDto) {
        return EventShortDto.builder()
                .annotation(fullDto.getAnnotation())
                .category(fullDto.getCategory())
                .confirmedRequests(fullDto.getConfirmedRequests())
                .eventDate(fullDto.getEventDate())
                .id(fullDto.getId())
                .initiator(fullDto.getInitiator())
                .paid(fullDto.getPaid())
                .title(fullDto.getTitle())
                .views(fullDto.getViews())
                .build();
    }
}
