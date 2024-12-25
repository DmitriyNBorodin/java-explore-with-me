package practicum.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserDto {
    @NotBlank(message = "Field name required and must not be blank")
    @Size(min = 2, max = 250, message = "Name length should be from 2 to 250")
    private final String name;
    @NotBlank(message = "Field email required and must not be blank")
    @Size(min = 6, max = 254, message = "Email length should be from 6 to 254")
    private final String email;
}
