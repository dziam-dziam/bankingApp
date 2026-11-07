package innowise.bankingApp.model.dto.response;

import java.time.LocalDate;

public record ResponseUserDto(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        Boolean active
) {
}
