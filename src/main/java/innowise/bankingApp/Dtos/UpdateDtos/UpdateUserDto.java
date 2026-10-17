package innowise.bankingApp.Dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateUserDto(
        @NotNull(message = "User Id cannot be null")
        @Positive(message = "User Id cannot be negative number")
        Long id,

        @NotBlank(message = "Name cannot be empty")
        @Size(min = 1, max = 30, message = "Name has to be between 1 and 30 characters")
        String name,

        @NotBlank(message = "Surname cannot be empty")
        @Size(min = 1, max = 30, message = "Surname has to be between 1 and 30 characters")
        String surname,

        @Column(name = "birth_date")
        @NotNull(message = "Birth date cannot be null")
        @PastOrPresent
        LocalDate birthDate,

        @Email
        @NotBlank(message = "Email cannot be empty")
        @Column(nullable = false, unique = true)
        String email) {

}
