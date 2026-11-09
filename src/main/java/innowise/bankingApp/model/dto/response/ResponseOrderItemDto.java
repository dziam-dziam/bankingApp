package innowise.bankingApp.model.dto.response;

public record ResponseOrderItemDto(
        Long id,
        Long itemId,
        String itemName,
        Integer quantity
) {
}
