package practicum.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class NewCategoryDto {
    @NotBlank(message = "name required and must not be blank")
    @Size(max = 50, message = "max category name length is 50")
    String name;
}
