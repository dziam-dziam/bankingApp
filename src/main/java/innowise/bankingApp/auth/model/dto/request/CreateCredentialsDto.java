package innowise.bankingApp.auth.model.dto.request;

import innowise.bankingApp.auth.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateCredentialsDto(
        @NotNull(message = "UserEntity Id cannot be null")
        @Positive(message = "UserEntity Id must be positive")
        Long userId,

        @NotBlank(message = "Login cannot be blank")
        @Pattern(
                regexp = "[a-zA-Z0-9._@-]{3,100}",
                message = "Login must contain 3-100 letters, digits or characters . _ @ -"
        )
        String login,

        @NotBlank(message = "Password cannot be blank")
        @Size(
                min = 8,
                max = 72,
                message = "Password must have between 8 and 72 characters"
        )
        String password,

        @NotNull(message = "Role cannot be null")
        Role role
) {
}