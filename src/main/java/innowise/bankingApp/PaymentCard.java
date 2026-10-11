package innowise.bankingApp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
public class PaymentCard extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Card number cannot be empty")
    private String number;

    @NotBlank(message = "Holder field cannot be empty")
    @Size(min = 1, max = 30, message = "Holder name must be between 1 and 30 characters")
    private String holder;

    @Column(name = "expiration_date")
    @NotNull(message = "Expiration date cannot be null")
    private LocalDate expirationDate;

    private boolean active;
}
