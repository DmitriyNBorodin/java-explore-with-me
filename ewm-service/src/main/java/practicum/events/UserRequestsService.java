package practicum.events;

import lombok.RequiredArgsConstructor;
import practicum.events.dto.EventDao;
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
public class UserRequestsService {
    private final RequestsRepository requestsRepository;
    private final EventRepository eventRepository;

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestsRepository.findParticipationRequestDtoByRequester(userId);
    }

    public ParticipationRequestDto addNewRequest(Long userId, String eventIdString) {
        Long eventId = Long.parseLong(eventIdString);
        EventDao eventToParticipate = eventRepository.findEventDaoById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Не удалось получить данные о событии с id=" + eventId));
        List<ParticipationRequestDto> participationRequests = requestsRepository.findParticipationRequestDtoByEvent(eventId);
        if (participationRequests.stream().map(ParticipationRequestDto::getRequester).collect(Collectors.toSet()).contains(userId)) {
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
        ParticipationRequestDto newParticipationRequest = ParticipationRequestDto.builder()
                .event(eventId)
                .requester(userId)
                .created(LocalDateTime.now())
                .status(RequestState.PENDING)
                .build();
        if (eventToParticipate.getParticipantLimit() == 0) newParticipationRequest.setStatus(RequestState.CONFIRMED);
        return requestsRepository.save(newParticipationRequest);
    }

    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        ParticipationRequestDto requestToCancel = requestsRepository.findParticipationRequestDtoById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("No request found with id=" + requestId));
        if (!Objects.equals(requestToCancel.getRequester(), userId)) {
            throw new ObjectNotFoundException("Not request found with id=" + requestId + " for user with id=" + userId);
        }
        requestToCancel.setStatus(RequestState.CANCELED);
        return requestsRepository.save(requestToCancel);
    }
}
