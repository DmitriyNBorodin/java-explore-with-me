package practicum.events;

import practicum.events.dto.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestsRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findParticipationRequestDtoByRequester(Long requester);

    List<ParticipationRequest> findParticipationRequestDtoByEvent(Long event);

    Optional<ParticipationRequest> findParticipationRequestDtoById(Long id);

    List<ParticipationRequest> findParticipationRequestDtoByIdIn(List<Long> id);

    List<ParticipationRequest> findParticipationRequestDtoByEventIn(List<Long> event);
}
