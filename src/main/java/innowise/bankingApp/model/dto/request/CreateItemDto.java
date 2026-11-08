package innowise.bankingApp.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateItemDto(
        @NotBlank(message = "Name cannot be empty")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        @Digits(
                integer = 17,
                fraction = 2,
                message = "Price can have up to 17 integer digits and 2 decimal places"
        )
        BigDecimal price
) {
}