package innowise.bankingApp.model.dto.response;

import java.math.BigDecimal;

public record ResponseItemDto(
        Long id,
        String name,
        BigDecimal price
) {
}