package practicum.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.users.dto.NewUserDto;
import practicum.users.dto.User;
import practicum.users.dto.UserDto;
import practicum.users.dto.UserDtoMapper;
import practicum.util.AdditionalEmailValidationException;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;

    public List<UserDto> getUsers(List<String> ids, String from, String size) {
        log.info("Поиск пользователей по параметрам ids={}, from={}, size={}", ids, from, size);
        Long fromLong = Long.parseLong(from);
        Long sizeLong = Long.parseLong(size);
        List<Long> idsLong = null;
        if (ids != null) {
            idsLong = ids.stream().map(Long::parseLong).toList();
        }
        List<User> requiredUsers = userRepository.getUserDaoByIdList(idsLong, fromLong, sizeLong);
        log.info("Получены пользователи {}", requiredUsers);
        return requiredUsers.stream().map(userDtoMapper::convertToUserDto).toList();
    }

    public UserDto addNewUser(NewUserDto newUser) {
        log.info("Добавление пользователя {}", newUser);
        additionEmailValidation(newUser);
        User savedUser = userRepository.save(userDtoMapper.assambleNewUserDao(newUser));
        return userDtoMapper.convertToUserDto(savedUser);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        if (userRepository.getUserDaoById(userId).isEmpty())
            throw new ObjectNotFoundException("User with id=" + userId + " was not found");
        userRepository.deleteUserDaoById(userId);
    }

    public User getUserDaoById(Long userId) {
        return userRepository.getUserDaoById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User with id=" + userId + " was not found"));
    }

    private void additionEmailValidation(NewUserDto newUserDto) {
        String[] splittedEmail = newUserDto.getEmail().split("@");
        if (splittedEmail[0].length() > 64) {
            throw new AdditionalEmailValidationException("max length of email's Local part is 64");
        }
        String[] splittedDomain = splittedEmail[1].split("\\.");
        for (String dnsLabel : splittedDomain) {
            if (dnsLabel.length() > 63) {
                throw new AdditionalEmailValidationException("max length of DNS Label in email's domain is 63");
            }
        }
    }
}
