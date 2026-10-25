package innowise.bankingApp.services;

import innowise.bankingApp.exceptions.PaymentCardNotFoundByIdException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        userRepository.delete(user);
    }

    @Transactional
    public void deletePaymentCard(Long cardId) {
        PaymentCard card = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        paymentCardRepository.delete(card);
    }

    @Transactional
    public void deleteAllUsers(){
        userRepository.deleteAll();
    }

    @Transactional
    public void deleteAllPaymentCards(){
        paymentCardRepository.deleteAll();
    }
}
