package innowise.bankingApp.Dtos.ResponseDtos;

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
