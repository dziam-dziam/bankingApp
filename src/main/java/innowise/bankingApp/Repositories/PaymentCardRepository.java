package innowise.bankingApp.Repositories;

import innowise.bankingApp.Entities.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
}
