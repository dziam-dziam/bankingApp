package innowise.bankingApp.Dtos.DeActivateDtos;

import jakarta.validation.constraints.NotNull;

public record DeActivatePaymentCardDto(
        @NotNull(message = "Active field cannot be null")
        boolean active
) {
}
