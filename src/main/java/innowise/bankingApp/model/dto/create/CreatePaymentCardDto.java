package innowise.bankingApp.model.dto.create;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreatePaymentCardDto(
        @NotNull(message = "User Id cannot be null")
        @Positive(message = "User Id cannot be negative number")
        Long userId,

        @NotBlank(message = "Card number cannot be empty")
        String number,

        @NotBlank(message = "Holder field cannot be empty")
        @Size(min = 1, max = 30, message = "Holder name must be between 1 and 30 characters")
        String holder,

        @NotNull(message = "Expiration date cannot be null")
        @Future(message = "Expiration date must be in the future")
        LocalDate expirationDate
) {
}
