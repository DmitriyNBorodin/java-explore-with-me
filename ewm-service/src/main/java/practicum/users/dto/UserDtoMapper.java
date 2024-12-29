package practicum.users.dto;


import org.springframework.stereotype.Component;

import java.util.InputMismatchException;

@Component
public class UserDtoMapper {
    public User assembleNewUserDao(NewUserDto newUserDto) {
        if (newUserDto == null) {
            throw new InputMismatchException("Failed to get new user data");
        }
        return User.builder().name(newUserDto.getName()).email(newUserDto.getEmail()).build();
    }

    public UserShortDto convertToShortDto(User userDao) {
        return UserShortDto.builder()
                .id(userDao.getId())
                .name(userDao.getName()).build();
    }

    public UserDto convertToUserDto(User userDao) {
        return UserDto.builder()
                .id(userDao.getId())
                .name(userDao.getName())
                .email(userDao.getEmail())
                .build();
    }
}
