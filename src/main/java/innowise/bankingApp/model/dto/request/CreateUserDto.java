package innowise.bankingApp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record CreateUserDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 1, max = 30, message = "Name has to be between 1 and 30 characters")
        String name,

        @NotBlank(message = "Surname cannot be empty")
        @Size(min = 1, max = 30, message = "Surname has to be between 1 and 30 characters")
        String surname,

        @NotNull(message = "Birth date cannot be null")
        @PastOrPresent
        LocalDate birthDate,

        @Email
        @NotBlank(message = "Email cannot be empty")
        String email
) {
}
