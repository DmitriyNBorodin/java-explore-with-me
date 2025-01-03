package practicum.events.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Entity
@IdClass(EventRatingId.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Table(name = "events_ratings")
@NoArgsConstructor
@AllArgsConstructor
public class EventRating {
    @Id
    @Column(name = "user_id")
    Long userId;
    @Id
    @Column(name = "event_id")
    Long eventId;
    int rating;
}
