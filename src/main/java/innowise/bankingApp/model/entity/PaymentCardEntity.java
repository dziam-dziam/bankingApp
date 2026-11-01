package innowise.bankingApp.model.entity;

import innowise.bankingApp.exceptions.TooManyPaymentCardsException;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
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
public class PaymentCardEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull(message = "UserEntity cannot be null")
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private UserEntity user;

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

        if (!(object instanceof PaymentCardEntity other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return PaymentCardEntity.class.hashCode();
    }

    public void assignUser(UserEntity user) {
        if (user.getPaymentCards().size() == 5) {
            throw new TooManyPaymentCardsException();
        }
        this.user = user;

    }
}
