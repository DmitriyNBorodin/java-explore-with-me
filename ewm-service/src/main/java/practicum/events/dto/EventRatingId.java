package practicum.events.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRatingId implements Serializable {
    Long userId;
    Long eventId;
}
