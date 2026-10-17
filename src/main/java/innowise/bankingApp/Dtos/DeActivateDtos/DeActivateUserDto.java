package innowise.bankingApp.Dtos.DeActivateDtos;

import jakarta.validation.constraints.NotNull;

public record DeActivateUserDto(
        @NotNull(message = "Active field cannot be null")
        Boolean active
) {
}
