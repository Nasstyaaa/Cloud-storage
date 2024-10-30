package org.nastya.filestorage.DTO;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {
    @Size(min=2, max = 25, message = "Invalid name length")
    private String username;

    @Size(min = 8, max = 17, message = "The password length cannot be less than 8")
    private String password;
}
