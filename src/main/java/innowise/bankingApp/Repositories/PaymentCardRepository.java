package innowise.bankingApp.Repositories;

import innowise.bankingApp.Dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.Entities.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

    List<ResponsePaymentCardDto> getAllPaymentCardsByUserId(Long userId);
}
