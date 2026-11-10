package innowise.bankingApp.auth.model.entity;

import innowise.bankingApp.auth.model.Role;
import innowise.bankingApp.model.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "credentials")
@Getter
@Setter
public class CredentialsEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentials_generator")
    @SequenceGenerator(name = "credentials_generator", sequenceName = "credentials_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Positive
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String login;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Role role;

    @Column(name = "refresh_token_hash", length = 64)
    private String refreshTokenHash;

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CredentialsEntity other)) {
            return false;
        }

        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return CredentialsEntity.class.hashCode();
    }
}