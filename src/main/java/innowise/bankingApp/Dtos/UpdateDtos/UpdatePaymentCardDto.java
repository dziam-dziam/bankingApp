package innowise.bankingApp.Dtos;

import innowise.bankingApp.Entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdatePaymentCardDto(
        @NotNull(message = "Payment card Id cannot be null")
        @Positive(message = "Payment card Id cannot be negative number")
        Long id,

        @NotNull(message = "User cannot be null")
        @JoinColumn(name = "user_id", nullable = false)
        User user,

        @NotBlank(message = "Card number cannot be empty")
        String number,

        @NotBlank(message = "Holder field cannot be empty")
        @Size(min = 1, max = 30, message = "Holder name must be between 1 and 30 characters")
        String holder,

        @Column(name = "expiration_date")
        @NotNull(message = "Expiration date cannot be null")
        LocalDate expirationDate
) {
}
