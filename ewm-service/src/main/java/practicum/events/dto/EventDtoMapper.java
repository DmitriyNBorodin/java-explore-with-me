package practicum.events.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.categories.CategoriesService;
import practicum.categories.dto.CategoryDtoMapper;
import practicum.events.RequestsRepository;
import practicum.events.states.EventState;
import practicum.events.states.RequestState;
import practicum.events.states.StateAction;
import practicum.users.UserService;
import practicum.GatheredStatsDto;
import practicum.StatsClient;
import practicum.users.dto.UserDtoMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
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
    private final CategoryDtoMapper categoryDtoMapper;
    private final UserDtoMapper userDtoMapper;

    public Event assembleNewEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto == null) {
            throw new InputMismatchException("Failed to get new event data");
        }
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(categoriesService.checkCategoryExistence(newEventDto.getCategory()))
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(userService.getUserById(userId))
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

    public EventFullDto assembleEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDtoMapper.convertCategoryToDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(userDtoMapper.convertToShortDto(event.getInitiator()))
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .confirmedRequests(0)
                .views(0L)
                .build();
    }

    public EventShortDto assembleEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDtoMapper.convertCategoryToDto(event.getCategory()))
                .confirmedRequests(0)
                .eventDate(event.getEventDate())
                .initiator(userDtoMapper.convertToShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(0L)
                .build();
    }

    public <T extends EventShortDto> T assignViewsAndRequests(T eventDto) {
        return assignViewsAndRequests(Collections.singletonList(eventDto)).getFirst();
    }

    public <T extends EventShortDto> List<T> assignViewsAndRequests(List<T> eventDto) {
        if (!eventDto.isEmpty()) {
            Map<Long, T> eventDtoMap = eventDto.stream().collect(Collectors.toMap(EventShortDto::getId,
                    Function.identity()));
            List<Long> eventIdList = eventDto.stream().map(EventShortDto::getId).collect(Collectors.toList());
            List<GatheredStatsDto> statsList = statsClient.getStatistics(eventIdList, LocalDateTime.now().minusYears(50),
                    LocalDateTime.now(), "true");
            List<ParticipationRequest> requestsOfEvents = requestsRepository.findParticipationRequestByEventIn(eventIdList);
            Map<Long, Long> mapOfConfirmedRequests = requestsOfEvents.stream()
                    .filter(request -> request.getStatus().equals(RequestState.CONFIRMED))
                    .collect(Collectors.groupingBy(ParticipationRequest::getEvent, Collectors.counting()));
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

    public ParticipationRequestDto convertParticipationRequestToDto(ParticipationRequest participationRequest) {
        if (participationRequest == null) {
            throw new InputMismatchException("Failed to get new participation request data");
        }
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .created(participationRequest.getCreated())
                .event(participationRequest.getEvent())
                .requester(participationRequest.getRequester())
                .status(participationRequest.getStatus())
                .build();
    }

    public Event updateEventFieldsByAdmin(Event updatingEvent, UpdateEventAdminRequest updateRequest) {
        if (updateRequest == null) {
            throw new InputMismatchException("Failed to get update request data");
        }
        if (updateRequest.getAnnotation() != null) {
            updatingEvent.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            updatingEvent.setCategory(categoriesService.getCategoryById(updateRequest.getCategory()));
        }
        if (updateRequest.getDescription() != null) {
            updatingEvent.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            updatingEvent.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            updatingEvent.setLat(updateRequest.getLocation().getLat());
            updatingEvent.setLon(updateRequest.getLocation().getLon());
        }
        if (updateRequest.getPaid() != null) {
            updatingEvent.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            updatingEvent.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            updatingEvent.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            updatingEvent.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
                updatingEvent.setState(EventState.PUBLISHED);
                updatingEvent.setPublishedOn(LocalDateTime.now());
            }
            if (updateRequest.getStateAction().equals(StateAction.REJECT_EVENT))
                updatingEvent.setState(EventState.CANCELED);
        }
        return updatingEvent;
    }

    public Event updateEventFieldsByUser(Event updatingEvent, UpdateEventUserRequest updateRequest) {
        if (updateRequest == null) {
            throw new InputMismatchException("Failed to get update request data");
        }
        if (updateRequest.getAnnotation() != null) {
            updatingEvent.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            updatingEvent.setCategory(categoriesService.getCategoryById(updateRequest.getCategory()));
        }
        if (updateRequest.getDescription() != null) {
            updatingEvent.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getEventDate() != null) {
            updatingEvent.setEventDate(updateRequest.getEventDate());
        }
        if (updateRequest.getLocation() != null) {
            updatingEvent.setLat(updateRequest.getLocation().getLat());
            updatingEvent.setLon(updateRequest.getLocation().getLon());
        }
        if (updateRequest.getPaid() != null) {
            updatingEvent.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            updatingEvent.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            updatingEvent.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            updatingEvent.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getStateAction() != null) {
            if (updateRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW))
                updatingEvent.setState(EventState.PENDING);
            if (updateRequest.getStateAction().equals(StateAction.CANCEL_REVIEW))
                updatingEvent.setState(EventState.CANCELED);
        }
        return updatingEvent;
    }
}
