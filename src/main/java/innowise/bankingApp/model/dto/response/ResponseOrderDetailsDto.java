package innowise.bankingApp.model.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ResponseOrderDetailsDto(
        Long id,
        Long userId,
        String status,
        BigDecimal totalPrice,
        boolean deleted,
        List<ResponseOrderItemDto> items
) {
}