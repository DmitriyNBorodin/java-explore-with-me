package practicum.events.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import practicum.events.states.RequestState;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationRequestDto {
    Long id;
    LocalDateTime created;
    Long event;
    Long requester;
    RequestState status;
}
