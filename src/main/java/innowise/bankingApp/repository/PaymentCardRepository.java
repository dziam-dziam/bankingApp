package innowise.bankingApp.repository;

import innowise.bankingApp.model.entity.PaymentCardEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends
        JpaRepository<PaymentCardEntity, Long>,
        JpaSpecificationExecutor<PaymentCardEntity> {

    List<PaymentCardEntity> findAllByUserId(Long userId);

    Optional<PaymentCardEntity> findByIdAndUserId(Long id, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select card
            from PaymentCardEntity card
            where card.id = :cardId and card.user.id = :userId
            """)
    Optional<PaymentCardEntity> findOwnedByIdForUpdate(
            @Param("cardId") Long cardId,
            @Param("userId") Long userId
    );
}