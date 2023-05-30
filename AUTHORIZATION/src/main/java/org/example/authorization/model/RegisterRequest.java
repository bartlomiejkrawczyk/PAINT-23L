package org.example.authorization.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotBlank(message = "Login must be provided")
    String login;

    @NotBlank(message = "Password must be provided")
    @Size(min = 8, max = 20)
    String password;

    @NotBlank(message = "Password must be provided")
    @Size(min = 8, max = 20)
    String confirmPassword;
}
