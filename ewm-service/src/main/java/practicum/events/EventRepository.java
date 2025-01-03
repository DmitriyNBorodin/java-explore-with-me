package practicum.events;

import practicum.events.dto.Event;
import practicum.events.states.EventState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select ed from Event as ed join ed.initiator as u where u.id = :userId order by ed.id" +
           " offset :from rows fetch next :size rows only")
    List<Event> findUserEvents(@Param("userId") Long userId, @Param("from") Long from, @Param("size") Long size);

    Optional<Event> findEventById(Long eventId);

    @Query("select ed from Event as ed join ed.initiator as u join ed.category as c where u.id in (coalesce(:userIds, u.id)) and " +
           "ed.state in (coalesce(:states, ed.state)) and c.id in (coalesce(:categoriesIds, c.id)) and ed.eventDate between" +
           " :start and :end order by ed.id offset :from rows fetch next :size rows only")
    List<Event> findAllEventByAdmin(@Param("userIds") List<Long> userIds, @Param("states") List<EventState> states,
                                       @Param("categoriesIds") List<Long> categoriesIds, @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end, @Param("from") Long from, @Param("size") Long size);

    @Query("select ed from Event as ed join ed.category as c where (coalesce(%:text%, ed.description) ilike ed.description " +
           "or coalesce(%:text%, ed.annotation) ilike ed.annotation) and c.id in (coalesce(:categoriesIds, c.id)) and ed.eventDate " +
           "between :start and :end order by ed.id offset :from rows fetch next :size rows only")
    List<Event> findAllEventByAnyone(@Param("text") String text, @Param("categoriesIds") List<Long> categoriesIds,
                                        @Param("start") LocalDateTime start, @Param("end") LocalDateTime end,
                                        @Param("from") Long from, @Param("size") Long size);

    List<Event> findEventByIdIn(List<Long> id);
}
