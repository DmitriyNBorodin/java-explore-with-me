package practicum.users;

import org.springframework.data.repository.query.Param;
import practicum.users.dto.UserRatingProjection;
import practicum.users.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserById(Long userId);

    void deleteUserById(Long userId);

    @Query("select u from User as u where u.id in (coalesce(?1, u.id)) order by u.id offset ?2 rows fetch next ?3 rows only")
    List<User> getUserByIdList(List<Long> ids, Long from, Long size);

    @Query("select u.id as userId, sum(r.rating) as userRating from Event as e join e.initiator as u join e.rating as r where u.id in (:users) group by u.id")
    List<UserRatingProjection> getUserRating(@Param("users") List<Long> users);
}
