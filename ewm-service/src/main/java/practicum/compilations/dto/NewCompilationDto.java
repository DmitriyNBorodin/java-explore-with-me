package practicum.compilations.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private List<Long> events;
    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean pinned = false;
    @NotBlank(message = "Title required and must not be blank")
    @Size(max = 50, message = "Max length on compilation title is 50")
    private String title;
}
