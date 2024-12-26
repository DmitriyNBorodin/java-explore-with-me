package practicum.events;

import practicum.events.dto.ParticipationRequestDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestsRepository extends JpaRepository<ParticipationRequestDao, Long> {
    List<ParticipationRequestDao> findParticipationRequestDtoByRequester(Long requester);

    List<ParticipationRequestDao> findParticipationRequestDtoByEvent(Long event);

    Optional<ParticipationRequestDao> findParticipationRequestDtoById(Long id);

    List<ParticipationRequestDao> findParticipationRequestDtoByIdIn(List<Long> id);

    List<ParticipationRequestDao> findParticipationRequestDtoByEventIn(List<Long> event);
}
