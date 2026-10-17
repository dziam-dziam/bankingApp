package innowise.bankingApp.Dtos;
import java.time.LocalDate;

public record CreateUserDto(
        String name,
        String surname,
        LocalDate brithDate,
        String email
) {
}
