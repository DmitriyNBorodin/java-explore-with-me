package practicum.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NewUserDto {
    @NotBlank(message = "Field name required and must not be blank")
    @Size(min = 2, max = 250, message = "Name length should be from 2 to 250")
    String name;
    @NotBlank(message = "Field email required and must not be blank")
    @Size(min = 6, max = 254, message = "Email length should be from 6 to 254")
    String email;
}
