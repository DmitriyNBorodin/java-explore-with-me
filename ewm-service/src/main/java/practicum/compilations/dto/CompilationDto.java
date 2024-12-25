package practicum.compilations.dto;

import lombok.Builder;
import lombok.Data;
import practicum.events.dto.EventShortDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CompilationDto {
    private Long id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
