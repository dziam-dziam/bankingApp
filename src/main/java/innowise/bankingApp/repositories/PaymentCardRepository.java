package innowise.bankingApp.repositories;

import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.entities.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId);

    @Query(value = "SELECT * FROM payment_cards WHERE id = :id", nativeQuery = true)
    Optional<PaymentCard> findByIdNative(@Param("id") Long id);
}
