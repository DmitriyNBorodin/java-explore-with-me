package practicum.events;

import org.springframework.data.jpa.repository.JpaRepository;
import practicum.events.dto.EventRating;
import practicum.events.dto.EventRatingId;

public interface RatingRepository extends JpaRepository<EventRating, EventRatingId> {
}