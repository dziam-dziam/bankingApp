package innowise.bankingApp.auth.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDto(
        @NotBlank(message = "Login cannot be blank")
        @Size(max = 100, message = "Login cannot exceed 100 characters")
        String login,

        @NotBlank(message = "Password cannot be blank")
        @Size(max = 72, message = "Password cannot exceed 72 characters")
        String password
) {
}