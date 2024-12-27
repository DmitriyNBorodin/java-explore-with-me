package practicum.events;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.categories.CategoriesService;
import practicum.events.dto.Event;
import practicum.events.dto.EventDtoMapper;
import practicum.events.dto.EventFullDto;
import practicum.events.dto.EventRequestStatusUpdateRequest;
import practicum.events.dto.EventRequestStatusUpdateResult;
import practicum.events.dto.EventShortDto;
import practicum.events.dto.NewEventDto;
import practicum.events.dto.ParticipationRequest;
import practicum.events.dto.UpdateEventUserRequest;
import practicum.events.states.EventState;
import practicum.events.states.RequestState;
import practicum.events.states.StateAction;
import practicum.util.ForbiddenActionException;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventsService {
    private final EventDtoMapper eventDtoMapper;
    private final EventRepository eventRepository;
    private final CategoriesService categoriesService;
    private final RequestsRepository requestsRepository;

    @Transactional
    public List<EventShortDto> getEventsByUser(Long userId, String from, String size) {
        Long fromLong = Long.parseLong(from);
        Long sizeLong = Long.parseLong(size);
        List<EventShortDto> requiredEventsList = eventRepository.findUserEvents(userId, fromLong, sizeLong).stream()
                .map(eventDtoMapper::assembleEventShortDto).toList();
        return eventDtoMapper.assignViewsAndRequests(requiredEventsList);
    }

    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        log.info("Добавление нового события {} пользователем с id={}", newEventDto, userId);
        validateEventDate(newEventDto.getEventDate());
        return eventDtoMapper.assembleEventFullDto(eventRepository.save(eventDtoMapper.assembleNewEventDao(userId, newEventDto)));
    }

    @Transactional
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        log.info("Получение информации о событии id={} пользователем id={}", eventId, userId);
        Event requiredEvent = eventRepository.findEventDaoById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не удалось получить данные о событии с id=" + eventId));
        if (!Objects.equals(requiredEvent.getInitiator().getId(), userId)) {
            throw new ObjectNotFoundException("Event with id=" + eventId + " was not found");
        }
        EventFullDto requiredEventDto = eventDtoMapper.assembleEventFullDto(requiredEvent);
        return eventDtoMapper.assignViewsAndRequests(Collections.singletonList(requiredEventDto)).getFirst();
    }

    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        log.info("Получение информации о событии id={} пользователем id={} для обновления", eventId, userId);
        Event eventToUpdate = eventRepository.findEventDaoById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не удалось получить данные о событии с id=" + eventId));
        log.info("По id={} получено событие {}", eventId, eventToUpdate);
        if (!eventToUpdate.getInitiator().getId().equals(userId) || eventToUpdate.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenActionException("Event with id=" + eventId + " unavailable");
        }
        validateEventDate(updateRequest.getEventDate());
        Event updatedEvent = updateEventDaoFieldsByUser(eventToUpdate, updateRequest);
        eventRepository.save(updatedEvent);
        EventFullDto requiredEventDto = eventDtoMapper.assembleEventFullDto(updatedEvent);
        return eventDtoMapper.assignViewsAndRequests(requiredEventDto);
    }

    @Transactional
    public List<ParticipationRequest> getUserEventRequests(Long userId, Long eventId) {
        validateUserEventAndGetLimit(userId, eventId);
        return requestsRepository.findParticipationRequestDtoByEvent(eventId);
    }

    @Transactional
    public EventRequestStatusUpdateResult updateUserEventRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        Long eventParticipantsLimit = validateUserEventAndGetLimit(userId, eventId);
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        long willBeConfirmed = 0L;
        List<ParticipationRequest> requestsToUpdateList = requestsRepository.findParticipationRequestDtoByIdIn(updateRequest.getRequestIds());
        if (requestsToUpdateList.stream().anyMatch(request -> !request.getStatus().equals(RequestState.PENDING))) {
            throw new ForbiddenActionException("Only pending requests available for updating");
        }
        List<ParticipationRequest> eventRequestsList = requestsRepository.findParticipationRequestDtoByEvent(eventId);
        if (updateRequest.getStatus().equals(RequestState.CONFIRMED)) {
            willBeConfirmed = eventRequestsList.stream().filter(request -> request.getStatus().equals(RequestState.CONFIRMED)).count()
                              + updateRequest.getRequestIds().size();
        }
        if (updateRequest.getStatus().equals(RequestState.CONFIRMED) && eventParticipantsLimit != 0 && willBeConfirmed > eventParticipantsLimit) {
            throw new ForbiddenActionException("Participants limit has been reached");
        }
        List<ParticipationRequest> listOfUpdatingRequests = requestsToUpdateList.stream()
                .peek(request -> request.setStatus(updateRequest.getStatus())).toList();
        requestsRepository.saveAll(listOfUpdatingRequests);
        if (updateRequest.getStatus().equals(RequestState.REJECTED)) {
            result.setRejectedRequests(listOfUpdatingRequests.stream().map(eventDtoMapper::convertParticipationRequestToDto).toList());
        }
        if (updateRequest.getStatus().equals(RequestState.CONFIRMED)) {
            result.setConfirmedRequests(listOfUpdatingRequests.stream().map(eventDtoMapper::convertParticipationRequestToDto).toList());
            if (willBeConfirmed == eventParticipantsLimit) {
                List<ParticipationRequest> listOfSpareRequests = eventRequestsList.stream()
                        .filter(request -> !updateRequest.getRequestIds().contains(request.getId()))
                        .filter(request -> !request.getStatus().equals(RequestState.CONFIRMED))
                        .peek(request -> request.setStatus(RequestState.REJECTED)).toList();
                requestsRepository.saveAll(listOfSpareRequests);
                result.setRejectedRequests(listOfSpareRequests.stream().map(eventDtoMapper::convertParticipationRequestToDto).toList());
            }
        }
        return result;
    }

    private Event updateEventDaoFieldsByUser(Event updatingEvent, UpdateEventUserRequest updateRequest) {
        if (updateRequest.getAnnotation() != null) {
            updatingEvent.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            updatingEvent.setCategory(categoriesService.getCategoryDaoById(updateRequest.getCategory()));
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

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.minusHours(2).isBefore(LocalDateTime.now())) {
            throw new ForbiddenActionException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: "
                                               + eventDate);
        }
    }

    private Long validateUserEventAndGetLimit(Long userId, Long eventId) {
        Event eventToCheck = eventRepository.findEventDaoById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не удалось получить данные о событии с id=" + eventId));
        if (!Objects.equals(userId, eventToCheck.getInitiator().getId())) {
            throw new ForbiddenActionException("Пользователь с id=" + userId + " не является создателем события с id=" + eventId);
        }
        return eventToCheck.getParticipantLimit();
    }
}
