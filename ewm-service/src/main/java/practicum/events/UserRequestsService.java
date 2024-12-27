package practicum.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.events.dto.Event;
import practicum.events.dto.EventDtoMapper;
import practicum.events.dto.ParticipationRequest;
import practicum.events.dto.ParticipationRequestDto;
import practicum.events.states.EventState;
import practicum.events.states.RequestState;
import practicum.util.ForbiddenActionException;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRequestsService {
    private final RequestsRepository requestsRepository;
    private final EventRepository eventRepository;
    private final EventDtoMapper eventDtoMapper;

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestsRepository.findParticipationRequestDtoByRequester(userId).stream()
                .map(eventDtoMapper::convertParticipationRequestToDto).toList();
    }

    public ParticipationRequestDto addNewRequest(Long userId, String eventIdString) {
        Long eventId = Long.parseLong(eventIdString);
        Event eventToParticipate = eventRepository.findEventDaoById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не удалось получить данные о событии с id=" + eventId));
        List<ParticipationRequest> participationRequests = requestsRepository.findParticipationRequestDtoByEvent(eventId);
        log.info("Добавление заявки на участие пользователя id={} в событии id={}", userId, eventId);
        if (participationRequests.stream().map(ParticipationRequest::getRequester).collect(Collectors.toSet()).contains(userId)) {
            throw new ForbiddenActionException("Request already created");
        } else if (eventToParticipate.getInitiator().getId().equals(userId)) {
            throw new ForbiddenActionException("Trying to participate own event");
        } else if (!eventToParticipate.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenActionException("Trying to participate unpublished event");
        } else if (eventToParticipate.getParticipantLimit() != 0 &&
                   participationRequests.stream().filter(request -> request.getStatus().equals(RequestState.CONFIRMED))
                           .count() >= eventToParticipate.getParticipantLimit()) {
            throw new ForbiddenActionException("Participants limit has been reached");
        }
        ParticipationRequest newParticipationRequest = ParticipationRequest.builder()
                .event(eventId)
                .requester(userId)
                .created(LocalDateTime.now())
                .status(RequestState.PENDING)
                .build();
        if (eventToParticipate.getParticipantLimit() == 0 || !eventToParticipate.getRequestModeration()) newParticipationRequest.setStatus(RequestState.CONFIRMED);
        log.info("Создана заявка на участие {}", newParticipationRequest);
        ParticipationRequest savedRequest = requestsRepository.save(newParticipationRequest);
        return eventDtoMapper.convertParticipationRequestToDto(savedRequest);
    }

    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        ParticipationRequest requestToCancel = requestsRepository.findParticipationRequestDtoById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("No request found with id=" + requestId));
        if (!Objects.equals(requestToCancel.getRequester(), userId)) {
            throw new ObjectNotFoundException("Not request found with id=" + requestId + " for user with id=" + userId);
        }
        requestToCancel.setStatus(RequestState.CANCELED);

        return eventDtoMapper.convertParticipationRequestToDto(requestsRepository.save(requestToCancel));
    }
}
