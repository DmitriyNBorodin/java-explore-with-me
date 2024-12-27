package practicum.compilations.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import practicum.events.dto.EventShortDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    List<EventShortDto> events;
    Boolean pinned;
    String title;
}
