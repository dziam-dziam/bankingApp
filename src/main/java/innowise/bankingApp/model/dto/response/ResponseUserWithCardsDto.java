package innowise.bankingApp.model.dto.response;

import java.util.List;

public record ResponseUserWithCardsDto(
        ResponseUserDto user,
        List<ResponsePaymentCardDto> cards
) {
}