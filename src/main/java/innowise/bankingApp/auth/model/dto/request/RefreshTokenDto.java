package innowise.bankingApp.auth.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenDto(
        @NotBlank(message = "Refresh token cannot be blank")
        @Size(
                max = 8192,
                message = "Refresh token cannot exceed 8192 characters"
        )
        String refreshToken
) {
}