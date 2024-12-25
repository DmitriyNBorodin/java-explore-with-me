package practicum.users;

import practicum.users.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserDto, Long> {
    Optional<UserDto> getUserDtoById(Long userId);

    void deleteUserDtoById(Long userId);

    @Query("select u from UserDto as u order by u.id offset ?1 rows fetch next ?2 rows only")
    List<UserDto> getAllUserDto(Long from, Long size);

    @Query("select u from UserDto as u where u.id in (coalesce(?1, u.id)) order by u.id offset ?2 rows fetch next ?3 rows only")
    List<UserDto> getUserDtoByIdList(List<Long> ids, Long from, Long size);
}
