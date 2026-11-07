package innowise.bankingApp.model.dto.response;

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
