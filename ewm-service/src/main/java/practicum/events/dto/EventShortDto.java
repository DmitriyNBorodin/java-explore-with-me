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
public class EventShortDto {
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    Long views;

    public EventShortDto(String annotation, CategoryDto category, Integer confirmedRequests, LocalDateTime eventDate, Long id, UserShortDto initiator, Boolean paid, String title, Long views) {
    }
}
