package innowise.bankingApp.services;

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

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

    public void deletePaymentCard(Long cardId){
        paymentCardRepository.deleteById(cardId);
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
