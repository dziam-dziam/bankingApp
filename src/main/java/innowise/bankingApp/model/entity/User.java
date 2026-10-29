package innowise.bankingApp.model.entity;

import innowise.bankingApp.exceptions.TooManyPaymentCardsException;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @Size(max = 5, message = "User cannot have more than 5 cards")
    private List<PaymentCard> paymentCards = new ArrayList<>();

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

    @Override
    public final boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof User other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public final int hashCode() {
        return User.class.hashCode();
    }

    public void addCard(PaymentCard newCard) {
        if (this.paymentCards.size() == 5) {
            throw new TooManyPaymentCardsException();
        }
        newCard.assignUser(this);
        paymentCards.add(newCard);
    }
}
