package innowise.bankingApp.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateOrderDto(
        @NotBlank @Size(max = 32) String status,
        @Size(min = 1) List<@NotNull @Valid OrderLineDto> items
) {
}
