package practicum.compilations.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    List<Long> events;
    @JsonSetter(nulls = Nulls.SKIP)
    Boolean pinned = false;
    @NotBlank(message = "Title required and must not be blank")
    @Size(max = 50, message = "Max length on compilation title is 50")
    String title;
}
