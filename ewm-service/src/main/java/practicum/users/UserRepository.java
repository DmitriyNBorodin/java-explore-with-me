package practicum.users;

import practicum.users.dto.UserDao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDao, Long> {
    Optional<UserDao> getUserDaoById(Long userId);

    void deleteUserDaoById(Long userId);

    @Query("select u from UserDao as u order by u.id offset ?1 rows fetch next ?2 rows only")
    List<UserDao> getAllUserDao(Long from, Long size);

    @Query("select u from UserDao as u where u.id in (coalesce(?1, u.id)) order by u.id offset ?2 rows fetch next ?3 rows only")
    List<UserDao> getUserDaoByIdList(List<Long> ids, Long from, Long size);
}
