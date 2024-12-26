package practicum.users.dto;


import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserDao assambleNewUserDao(NewUserDto newUserDto) {
        return UserDao.builder().name(newUserDto.getName()).email(newUserDto.getEmail()).build();
    }

    public UserShortDto convertToShortDto(UserDao userDao) {
        return UserShortDto.builder()
                .id(userDao.getId())
                .name(userDao.getName()).build();
    }

    public UserDto convertToUserDto(UserDao userDao) {
        return UserDto.builder()
                .id(userDao.getId())
                .name(userDao.getName())
                .email(userDao.getEmail())
                .build();
    }
}
