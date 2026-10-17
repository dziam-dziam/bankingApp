package innowise.bankingApp.Repositories;

import innowise.bankingApp.Entities.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {
}
