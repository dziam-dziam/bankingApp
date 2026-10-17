package innowise.bankingApp.Dtos.ResponseDtos;

import java.time.LocalDate;

public record  ResponseUserDto(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        boolean active
) {
}
