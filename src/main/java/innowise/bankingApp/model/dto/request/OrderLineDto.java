package innowise.bankingApp.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderLineDto(
        @NotNull @Positive Long itemId,
        @NotNull @Positive Integer quantity
) {
}
