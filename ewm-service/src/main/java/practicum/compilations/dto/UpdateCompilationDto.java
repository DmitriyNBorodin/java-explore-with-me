package practicum.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import practicum.util.NullOrNotBlank;

import java.util.List;

@Getter
@ToString
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NullOrNotBlank(message = "Title must not be blank")
    @Size(max = 50, message = "Max length on compilation title is 50")
    private String title;
}
