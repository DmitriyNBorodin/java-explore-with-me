package practicum.users.dto;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import practicum.users.UserRepository;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserDtoMapper {
    private final UserRepository userRepository;

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

    public List<UserDto> assignRating(List<UserDto> rawUserList) {
        List<UserRatingProjection> ratingList = userRepository.getUserRating(rawUserList.stream().map(UserDto::getId).toList());
        if (ratingList.isEmpty()) {
            return rawUserList;
        }
        Map<Long, UserDto> rawUserMap = rawUserList.stream().collect(Collectors.toMap(UserDto::getId, Function.identity()));
        for (UserRatingProjection rating : ratingList) {
            rawUserMap.get(rating.getUserId()).setRating(rating.getUserRating());
        }
        return rawUserMap.values().stream().toList();
    }
}
