package practicum.events.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import practicum.categories.dto.CategoryDto;
import practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
public class RatedEventDto {
    String annotation;
    CategoryDto category;
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    String title;
    Long rating;
}
