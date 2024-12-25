package practicum.events;

import practicum.events.dto.ParticipationRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestsRepository extends JpaRepository<ParticipationRequestDto, Long> {
    List<ParticipationRequestDto> findParticipationRequestDtoByRequester(Long requester);

    List<ParticipationRequestDto> findParticipationRequestDtoByEvent(Long event);

    Optional<ParticipationRequestDto> findParticipationRequestDtoById(Long id);

    List<ParticipationRequestDto> findParticipationRequestDtoByIdIn(List<Long> id);

    List<ParticipationRequestDto> findParticipationRequestDtoByEventIn(List<Long> event);
}
