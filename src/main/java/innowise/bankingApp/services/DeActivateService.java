package innowise.bankingApp.services;

import innowise.bankingApp.exceptions.*;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponsePaymentCardDto;
import innowise.bankingApp.model.dtos.ResponseDtos.ResponseUserDto;
import innowise.bankingApp.model.entities.PaymentCard;
import innowise.bankingApp.model.entities.User;
import innowise.bankingApp.mappers.PaymentCardMapper;
import innowise.bankingApp.mappers.UserMapper;
import innowise.bankingApp.repositories.PaymentCardRepository;
import innowise.bankingApp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeActivateService {

    private final PaymentCardMapper paymentCardMapper;
    private final UserMapper userMapper;
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponsePaymentCardDto activatePaymentCard(Long cardId) {
        if (cardId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (paymentCard.isActive()) {
            throw new PaymentCardAlreadyActiveException();
        }

        paymentCard.setActive(true);
        return paymentCardMapper.toDto(paymentCard);
    }

    @Transactional
    public ResponsePaymentCardDto deactivatePaymentCard(Long cardId) {
        if (cardId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        PaymentCard paymentCard = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new PaymentCardNotFoundByIdException(cardId));

        if (!paymentCard.isActive()) {
            throw new PaymentCardAlreadyDeactivatedException();
        }

        paymentCard.setActive(false);
        return paymentCardMapper.toDto(paymentCard);
    }

    @Transactional
    public ResponseUserDto activateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (user.isActive()) {
            throw new UserAlreadyActiveException();
        }

        user.setActive(true);
        return userMapper.toDto(user);
    }

    @Transactional
    public ResponseUserDto deactivateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundByIdException(userId));

        if (!user.isActive()) {
            throw new UserAlreadyDeactivatedException();
        }

        user.setActive(false);
        return userMapper.toDto(user);
    }

}
