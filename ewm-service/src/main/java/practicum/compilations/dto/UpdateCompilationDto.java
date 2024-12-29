package practicum.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import practicum.util.NullOrNotBlank;

import java.util.List;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationDto {
    List<Long> events;
    Boolean pinned;
    @NullOrNotBlank(message = "Title must not be blank")
    @Size(max = 50, message = "Max length on compilation title is 50")
    String title;
}
