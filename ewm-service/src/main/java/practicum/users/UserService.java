package practicum.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import practicum.users.dto.NewUserDto;
import practicum.users.dto.UserDto;
import practicum.users.dto.UserShortDto;
import practicum.util.AdditionalEmailValidationException;
import practicum.util.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public List<UserDto> getUsers(List<String> ids, String from, String size) {
        log.info("Поиск пользователей по параметрам ids={}, from={}, size={}", ids, from, size);
        Long fromLong = Long.parseLong(from);
        Long sizeLong = Long.parseLong(size);
        List<Long> idsLong = null;
        if (ids != null) {
            idsLong = ids.stream().map(Long::parseLong).toList();
        }
        List<UserDto> requiredUsers = userRepository.getUserDtoByIdList(idsLong, fromLong, sizeLong);
        log.info("Получены пользователи {}", requiredUsers);
        return requiredUsers;
    }

    public UserDto addNewUser(NewUserDto newUser) {
        log.info("Добавление пользователя {}", newUser);
        additionEmailValidation(newUser);
        return userRepository.save(convertToUserDto(newUser));
    }

    @Transactional
    public void deleteUserById(Long userId) {
        if (userRepository.getUserDtoById(userId).isEmpty())
            throw new ObjectNotFoundException("User with id=" + userId + " was not found");
        userRepository.deleteUserDtoById(userId);
    }

    public UserDto getUserDtoById(Long userId) {
        return userRepository.getUserDtoById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User with id=" + userId + " was not found"));
    }

    private UserDto convertToUserDto(NewUserDto newUserDto) {
        return UserDto.builder().name(newUserDto.getName()).email(newUserDto.getEmail()).build();
    }

    public UserShortDto convertToShortDto(UserDto userDto) {
        return new UserShortDto(userDto.getName(), userDto.getId());
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
