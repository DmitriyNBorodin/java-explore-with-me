package practicum.events;

import practicum.events.dto.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestsRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findParticipationRequestByRequester(Long requester);

    List<ParticipationRequest> findParticipationRequestByEvent(Long event);

    Optional<ParticipationRequest> findParticipationRequestById(Long id);

    List<ParticipationRequest> findParticipationRequestByIdIn(List<Long> id);

    List<ParticipationRequest> findParticipationRequestByEventIn(List<Long> event);
}
