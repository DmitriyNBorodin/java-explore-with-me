package practicum.events.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import practicum.categories.dto.CategoryDto;
import practicum.events.states.EventState;
import practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder(toBuilder = true)
public class EventFullDto extends EventShortDto {
    String annotation;
    CategoryDto category;
    LocalDateTime createdOn;
    Integer confirmedRequests;
    String description;
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Long participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;
    Long rating;
}
