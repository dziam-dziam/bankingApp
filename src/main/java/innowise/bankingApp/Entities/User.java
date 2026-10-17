package innowise.bankingApp.Entities;
import innowise.bankingApp.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 30, message = "Name has to be between 1 and 30 characters")
    private String name;

    @NotBlank(message = "Surname cannot be empty")
    @Size(min = 1, max = 30, message = "Surname has to be between 1 and 30 characters")
    private String surname;

    @Column(name = "birth_date")
    @NotNull(message = "Birth date cannot be null")
    @PastOrPresent
    private LocalDate birthDate;

    @Email
    @NotBlank(message = "Email cannot be empty")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "active", nullable = false)
    private boolean active;
}
