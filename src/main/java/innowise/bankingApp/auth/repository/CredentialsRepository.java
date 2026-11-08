package innowise.bankingApp.auth.repository;

import innowise.bankingApp.auth.model.entity.CredentialsEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CredentialsRepository
        extends JpaRepository<CredentialsEntity, Long> {

    Optional<CredentialsEntity> findByLogin(String login);

    Optional<CredentialsEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CredentialsEntity c where c.userId = :userId")
    Optional<CredentialsEntity> findByUserIdForUpdate(
            @Param("userId") Long userId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CredentialsEntity c where c.login = :login")
    Optional<CredentialsEntity> findByLoginForUpdate(
            @Param("login") String login
    );
}