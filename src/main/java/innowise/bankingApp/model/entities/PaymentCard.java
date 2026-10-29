package innowise.bankingApp.model.entities;

import innowise.bankingApp.AuditableEntity;
import innowise.bankingApp.exceptions.TooManyPaymentCardsException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import java.time.LocalDate;

@Entity
@Table(name = "payment_cards")
@Getter
@Setter
public class PaymentCard extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull(message = "User cannot be null")
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
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

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof PaymentCard other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return PaymentCard.class.hashCode();
    }

    public void assignUser(User user) {
        if (user.getPaymentCards().size() == 5) {
            throw new TooManyPaymentCardsException();
        }
        this.user = user;

    }
}
