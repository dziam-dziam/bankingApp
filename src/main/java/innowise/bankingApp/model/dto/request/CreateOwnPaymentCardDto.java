package innowise.bankingApp.model.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateOwnPaymentCardDto(
        @NotBlank(message = "Card number cannot be empty")
        String number,

        @NotBlank(message = "Holder field cannot be empty")
        @Size(
                min = 1,
                max = 30,
                message = "Holder name must be between 1 and 30 characters"
        )
        String holder,

        @NotNull(message = "Expiration date cannot be null")
        @Future(message = "Expiration date must be in the future")
        LocalDate expirationDate
) {
}