package practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@JsonDeserialize
public class NewEventDto {
    @NotBlank(message = "annotation required")
    @Size(min = 20, max = 2000, message = "length of annotation must be between 20 and 2000")
    String annotation;
    @NotNull(message = "category required")
    Long category;
    @NotBlank(message = "description required")
    @Size(min = 20, max = 7000, message = "length of description must be between 20 and 7000")
    String description;
    @NotNull(message = "event date required")
    @FutureOrPresent(message = "event date must be in future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull(message = "location required")
    Location location;
    @JsonSetter(nulls = Nulls.SKIP)
    Boolean paid = false;
    @JsonSetter(nulls = Nulls.SKIP)
    @PositiveOrZero(message = "participation limit cannot be negative")
    Long participantLimit = 0L;
    @JsonSetter(nulls = Nulls.SKIP)
    Boolean requestModeration = true;
    @NotBlank(message = "title required")
    @Size(min = 3, max = 120, message = "length of title must be between 3 and 120")
    String title;
}
