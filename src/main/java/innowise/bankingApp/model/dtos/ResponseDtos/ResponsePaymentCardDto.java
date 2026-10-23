package innowise.bankingApp.model.dtos.ResponseDtos;

import java.time.LocalDate;

public record ResponsePaymentCardDto(
        Long id,
        Long userId,
        String number,
        String holder,
        LocalDate expirationDate,
        Boolean active
) {
}
