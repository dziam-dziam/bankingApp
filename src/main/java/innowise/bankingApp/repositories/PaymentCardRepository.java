package innowise.bankingApp.repositories;

import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.entities.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId);
}
