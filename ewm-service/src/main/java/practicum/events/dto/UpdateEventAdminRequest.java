package practicum.events.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import practicum.events.states.StateAction;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "length of annotation must be between 20 and 2000")
    String annotation;
    Long category;
    @Size(min = 20, max = 7000, message = "length of description must be between 20 and 7000")
    String description;
    @FutureOrPresent(message = "new event date must be in future")
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero(message = "new participation limit cannot be negative")
    Long participantLimit;
    Boolean requestModeration;
    StateAction stateAction;
    @Size(min = 3, max = 120, message = "length of title must be between 3 and 120")
    String title;
}
