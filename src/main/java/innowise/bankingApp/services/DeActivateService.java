package innowise.bankingApp.services;

import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.exceptions.PaymentCardNotFoundByIdException;
import innowise.bankingApp.exceptions.UserNotFoundByIdException;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeActivateService {

    private final PaymentCardMapper paymentCardMapper;
    private final UserMapper userMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public ResponsePaymentCardDto activatePaymentCard(Long cardId) {
        if (cardId == null) {
            throw new IllegalStateException("Id cannot be null");
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (paymentCard.isActive()) {
            throw new IllegalStateException("Payment card is already active");
        }

        paymentCard.setActive(true);
        paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(paymentCard);
    }

    public ResponsePaymentCardDto deactivatePaymentCard(Long cardId) {
        if (cardId == null) {
            throw new IllegalStateException("Id cannot be null");
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (!paymentCard.isActive()) {
            throw new IllegalStateException("Payment card is already deactivated");
        }

        paymentCard.setActive(false);
        paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(paymentCard);
    }

    public ResponseUserDto activateUser(Long userId) {
        if (userId == null) {
            throw new IllegalStateException("Id cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (user.isActive()) {
            throw new IllegalStateException("User is already active");
        }

        user.setActive(true);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public ResponseUserDto deactivateUser(Long userId) {
        if (userId == null) {
            throw new IllegalStateException("Id cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (!user.isActive()) {
            throw new IllegalStateException("User is already deactivated");
        }

        user.setActive(false);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

}
